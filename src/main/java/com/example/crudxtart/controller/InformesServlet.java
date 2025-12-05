package com.example.crudxtart.controller;

import java.io.IOException;
import java.util.logging.Logger;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.example.crudxtart.models.Factura;
import com.example.crudxtart.models.FacturaProducto;
import com.example.crudxtart.models.Presupuestos;
import com.example.crudxtart.service.FacturaProductoService;
import com.example.crudxtart.service.FacturaService;
import com.example.crudxtart.service.PresupuestosService;
import com.example.crudxtart.utils.JsonUtil;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/informes/*")
public class InformesServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(InformesServlet.class.getName());
    private static final String CODIGO_LOG = "CTL-INF-";

    @Inject
    private FacturaService facturaService;

    @Inject
    private PresupuestosService presupuestosService;

    @Inject
    private FacturaProductoService facturaProductoService;

    @Inject
    private EntityManagerFactory emf;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final ExecutorService executor = Executors.newFixedThreadPool(5);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        logger.info("[" + CODIGO_LOG + "001] doGet - inicio");
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String path = req.getPathInfo();
        if (path == null || path.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            sendError(resp, "Debe especificar el tipo de informe en la URL.");
            return;
        }

        LocalDate desde = parseDate(req.getParameter("desde"));
        LocalDate hasta = parseDate(req.getParameter("hasta"));

        try {
            logger.info("[" + CODIGO_LOG + "010] Petición recibida: " + path + " | desde=" + desde + " | hasta=" + hasta);

            Object resultado = null;

            switch (path) {

                case "/ventas-empleado":
                    logger.info("[" + CODIGO_LOG + "020] Ejecutando informe ventas-empleado");
                    resultado = ejecutarConExecutor(() -> handleVentasPorEmpleado(desde, hasta));
                    break;

                case "/presupuestos-estado":
                    logger.info("[" + CODIGO_LOG + "030] Ejecutando informe presupuestos-estado");
                    resultado = ejecutarConExecutor(() -> handleEstadoPresupuestos(desde, hasta));
                    break;

                case "/facturacion-mensual":
                    logger.info("[" + CODIGO_LOG + "040] Ejecutando informe facturacion-mensual");
                    resultado = ejecutarConExecutor(() -> handleFacturacionMensual(desde, hasta));
                    break;

                case "/ventas-producto":
                    logger.info("[" + CODIGO_LOG + "050] Ejecutando informe ventas-producto");
                    resultado = ejecutarConExecutor(() -> handleVentasPorProducto(desde, hasta));
                    break;

                case "/ratio-conversion":
                    logger.info("[" + CODIGO_LOG + "060] Ejecutando informe ratio-conversion");
                    resultado = ejecutarConExecutor(() -> handleRatioConversion(desde, hasta));
                    break;

                default:
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    sendError(resp, "Informe no soportado: " + path);
                    return;
            }

            logger.info("[" + CODIGO_LOG + "090] Informe generado correctamente. Serializando respuesta...");
            sendSuccess(resp, resultado);

        } catch (Exception ex) {
            logger.severe("[" + CODIGO_LOG + "999] EXCEPCION INFORME: " + ex.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            sendError(resp, "Error al generar informe: " + ex.getMessage());
        }
    }

    @Override
    public void destroy() {
        logger.info("[" + CODIGO_LOG + "998] destroy() - cerrando executor");
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    // ------------------------------------HANDLE INFORMES----------------------------------------


    private List<Map<String, Object>> handleVentasPorEmpleado(LocalDate desde, LocalDate hasta) {

        logger.info("[" + CODIGO_LOG + "100] handleVentasPorEmpleado - inicio");

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = null;

        try {
            tx = em.getTransaction();
            tx.begin();

            List<Factura> facturas = em.createQuery(
                    "SELECT f FROM Factura f LEFT JOIN FETCH f.cliente_pagador LEFT JOIN FETCH f.empleado",
                    Factura.class
            ).getResultList();

            tx.commit();

            logger.info("[" + CODIGO_LOG + "110] Total facturas: " + facturas.size());

            if (desde != null) {
                facturas = facturas.stream()
                        .filter(f -> f.getFecha_emision() != null && !f.getFecha_emision().isBefore(desde))
                        .collect(Collectors.toList());
            }

            if (hasta != null) {
                facturas = facturas.stream()
                        .filter(f -> f.getFecha_emision() != null && !f.getFecha_emision().isAfter(hasta))
                        .collect(Collectors.toList());
            }

            logger.info("[" + CODIGO_LOG + "120] Facturas después de filtros: " + facturas.size());

            Map<String, Double> acumulado = new HashMap<>();
            for (Factura f : facturas) {
                if (f.getEmpleado() == null) continue;
                acumulado.merge(safeUpperFirst(f.getEmpleado().getNombre()), f.getTotal(), Double::sum);
            }

            List<Map<String, Object>> resultado = new ArrayList<>();
            for (Map.Entry<String, Double> entry : acumulado.entrySet()) {
                Map<String, Object> item = new HashMap<>();
                item.put("nombre", entry.getKey());
                item.put("total", entry.getValue());
                resultado.add(item);
            }

            logger.info("[" + CODIGO_LOG + "130] Resultado empleados: " + resultado.size());
            return resultado;

        } catch (Exception e) {
            logger.severe("[" + CODIGO_LOG + "199] Error en handleVentasPorEmpleado: " + e.getMessage());
            if (tx != null && tx.isActive()) tx.rollback();
            throw new RuntimeException("Error informe ventas-empleado", e);
        } finally {
            if (em.isOpen()) em.close();
        }
    }



    private Map<String, Integer> handleEstadoPresupuestos(LocalDate desde, LocalDate hasta) {

        logger.info("[" + CODIGO_LOG + "200] handleEstadoPresupuestos - inicio");

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = null;

        try {
            tx = em.getTransaction();
            tx.begin();

            List<Presupuestos> lista = em.createQuery("SELECT p FROM Presupuestos p", Presupuestos.class)
                    .getResultList();

            tx.commit();

            logger.info("[" + CODIGO_LOG + "210] Total presupuestos: " + lista.size());

            if (desde != null)
                lista = lista.stream().filter(p -> p.getFecha_apertura() != null && !p.getFecha_apertura().isBefore(desde))
                        .collect(Collectors.toList());
            if (hasta != null)
                lista = lista.stream().filter(p -> p.getFecha_apertura() != null && !p.getFecha_apertura().isAfter(hasta))
                        .collect(Collectors.toList());

            Map<String, Integer> conteo = new HashMap<>();
            conteo.put("APROBADO", 0);
            conteo.put("PENDIENTE", 0);
            conteo.put("RECHAZADO", 0);

            for (Presupuestos p : lista) {
                String estado = p.getEstado() != null ? p.getEstado().trim().toUpperCase(Locale.ROOT) : "";
                if (estado.equals("APROBADO") || estado.equals("APROBADA"))
                    conteo.merge("APROBADO", 1, Integer::sum);
                else if (estado.equals("RECHAZADO") || estado.equals("RECHAZADA"))
                    conteo.merge("RECHAZADO", 1, Integer::sum);
                else
                    conteo.merge("PENDIENTE", 1, Integer::sum);
            }

            logger.info("[" + CODIGO_LOG + "220] Resultado: " + conteo);
            return conteo;

        } catch (Exception e) {
            logger.severe("[" + CODIGO_LOG + "299] Error en handleEstadoPresupuestos: " + e.getMessage());
            if (tx != null && tx.isActive()) tx.rollback();
            throw new RuntimeException("Error informe estado-presupuestos", e);
        } finally {
            if (em.isOpen()) em.close();
        }
    }



    private Map<String, Double> handleFacturacionMensual(LocalDate desde, LocalDate hasta) {

        logger.info("[" + CODIGO_LOG + "300] handleFacturacionMensual - inicio");

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = null;

        try {
            tx = em.getTransaction();
            tx.begin();

            List<Factura> facturas = em.createQuery("SELECT f FROM Factura f", Factura.class)
                    .getResultList();

            tx.commit();

            logger.info("[" + CODIGO_LOG + "310] Total facturas: " + facturas.size());

            if (desde != null)
                facturas = facturas.stream()
                        .filter(f -> f.getFecha_emision() != null && !f.getFecha_emision().isBefore(desde))
                        .collect(Collectors.toList());

            if (hasta != null)
                facturas = facturas.stream()
                        .filter(f -> f.getFecha_emision() != null && !f.getFecha_emision().isAfter(hasta))
                        .collect(Collectors.toList());

            Map<String, Double> porMes = new HashMap<>();
            for (Factura f : facturas) {
                if (f.getFecha_emision() == null) continue;
                porMes.merge(YearMonth.from(f.getFecha_emision()).toString(), f.getTotal(), Double::sum);
            }

            logger.info("[" + CODIGO_LOG + "320] Resultado facturación mensual: " + porMes.size() + " meses");
            return porMes;

        } catch (Exception e) {
            logger.severe("[" + CODIGO_LOG + "399] Error en handleFacturacionMensual: " + e.getMessage());
            if (tx != null && tx.isActive()) tx.rollback();
            throw new RuntimeException("Error informe facturación mensual", e);
        } finally {
            if (em.isOpen()) em.close();
        }
    }



    private List<Map<String, Object>> handleVentasPorProducto(LocalDate desde, LocalDate hasta) {

        logger.info("[" + CODIGO_LOG + "400] handleVentasPorProducto - inicio");

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = null;

        try {
            tx = em.getTransaction();
            tx.begin();

            List<FacturaProducto> items = em.createQuery(
                    "SELECT fp FROM FacturaProducto fp " +
                            "LEFT JOIN FETCH fp.factura " +
                            "LEFT JOIN FETCH fp.Producto",
                    FacturaProducto.class
            ).getResultList();

            tx.commit();

            logger.info("[" + CODIGO_LOG + "410] Total FacturaProducto: " + items.size());

            Map<String, Double> acumulado = new HashMap<>();

            for (FacturaProducto fp : items) {
                Factura factura = fp.getFactura();
                if (factura != null) {
                    LocalDate fecha = factura.getFecha_emision();
                    if ((desde != null && fecha != null && fecha.isBefore(desde)) ||
                            (hasta != null && fecha != null && fecha.isAfter(hasta))) {
                        continue;
                    }
                }
                if (fp.getProducto() == null) continue;

                double subtotal = fp.getSubtotal() == 0 ?
                        fp.getCantidad() * fp.getPrecio_unitario() :
                        fp.getSubtotal();

                acumulado.merge(safeUpperFirst(fp.getProducto().getNombre()), subtotal, Double::sum);
            }

            List<Map<String, Object>> resultado = new ArrayList<>();
            for (Map.Entry<String, Double> e : acumulado.entrySet()) {
                Map<String, Object> item = new HashMap<>();
                item.put("producto", e.getKey());
                item.put("total", e.getValue());
                resultado.add(item);
            }

            logger.info("[" + CODIGO_LOG + "420] Resultado ventas productos: " + resultado.size());
            return resultado;

        } catch (Exception e) {
            logger.severe("[" + CODIGO_LOG + "499] Error en handleVentasPorProducto: " + e.getMessage());
            if (tx != null && tx.isActive()) tx.rollback();
            throw new RuntimeException("Error informe ventas-producto", e);
        } finally {
            if (em.isOpen()) em.close();
        }
    }



    private Map<String, Integer> handleRatioConversion(LocalDate desde, LocalDate hasta) {

        logger.info("[" + CODIGO_LOG + "500] handleRatioConversion - inicio");

        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = null;

        try {
            tx = em.getTransaction();
            tx.begin();

            List<Presupuestos> lista = em.createQuery(
                            "SELECT p FROM Presupuestos p", Presupuestos.class)
                    .getResultList();

            tx.commit();

            logger.info("[" + CODIGO_LOG + "510] Total presupuestos: " + lista.size());

            int convertidos = 0, rechazados = 0, pendientes = 0, otros = 0;

            for (Presupuestos p : lista) {
                String estado = p.getEstado() != null ? p.getEstado().trim().toUpperCase(Locale.ROOT) : "";
                if (estado.equals("APROBADO")) convertidos++;
                else if (estado.equals("RECHAZADO")) rechazados++;
                else if (estado.equals("PENDIENTE")) pendientes++;
                else otros++;
            }

            Map<String, Integer> resultado = new HashMap<>();
            resultado.put("Convertidos", convertidos);
            resultado.put("Rechazados", rechazados);
            resultado.put("Pendientes", pendientes);
            resultado.put("Otros", otros);

            logger.info("[" + CODIGO_LOG + "520] Resultado ratio conversión: " + resultado);
            return resultado;

        } catch (Exception e) {
            logger.severe("[" + CODIGO_LOG + "599] Error en handleRatioConversion: " + e.getMessage());
            if (tx != null && tx.isActive()) tx.rollback();
            throw new RuntimeException("Error informe ratio-conversión", e);
        } finally {
            if (em.isOpen()) em.close();
        }
    }



    // ---------------------------------------HELPERS-------------------------------------------------

    private LocalDate parseDate(String raw) {
        if (raw == null || raw.trim().isEmpty()) return null;
        try {
            return LocalDate.parse(raw.trim(), DATE_FMT);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private <T> T ejecutarConExecutor(Callable<T> tarea) throws Exception {
        Future<T> future = executor.submit(tarea);
        try {
            return future.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new Exception("Ejecución interrumpida", e);
        } catch (ExecutionException e) {
            Throwable causa = e.getCause();
            if (causa instanceof Exception)
                throw (Exception) causa;
            throw new Exception("Error ejecutando tarea", e);
        }
    }

    private String safeUpperFirst(String value) {
        if (value == null) return "Desconocido";
        String trimmed = value.trim();
        return trimmed.isEmpty() ? "Desconocido" :
                trimmed.substring(0, 1).toUpperCase() + trimmed.substring(1);
    }

    private void sendSuccess(HttpServletResponse resp, Object data) throws IOException {
        resp.getWriter().write(JsonUtil.toJson(new Response(true, data)));
    }

    private void sendError(HttpServletResponse resp, String msg) throws IOException {
        resp.getWriter().write(JsonUtil.toJson(new Response(false, new ErrorMsg(msg))));
    }

    private static class Response {
        final boolean success;
        final Object data;
        Response(boolean success, Object data) {
            this.success = success;
            this.data = data;
        }
        public boolean isSuccess() { return success; }
        public Object getData() { return data; }
    }

    private static class ErrorMsg {
        final String error;
        ErrorMsg(String error) { this.error = error; }
        public String getError() { return error; }
    }
}
