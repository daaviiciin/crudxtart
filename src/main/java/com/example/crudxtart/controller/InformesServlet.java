package com.example.crudxtart.controller;

import java.io.IOException;
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
import java.util.concurrent.atomic.AtomicReference;
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
            System.out.println("[INFORME] Petición recibida: " + path + " - desde: " + desde + ", hasta: " + hasta);
            
            Object resultado = null;

            // Ejecutar informes en hilos secundarios para no bloquear el hilo principal
            switch (path) {

                case "/ventas-empleado":
                    resultado = ejecutarConExecutor(() -> handleVentasPorEmpleado(desde, hasta));
                    break;

                case "/presupuestos-estado":
                    resultado = ejecutarConExecutor(() -> handleEstadoPresupuestos(desde, hasta));
                    break;

                case "/facturacion-mensual":
                    resultado = ejecutarConExecutor(() -> handleFacturacionMensual(desde, hasta));
                    break;

                case "/ventas-producto":
                    resultado = ejecutarConExecutor(() -> handleVentasPorProducto(desde, hasta));
                    break;

                case "/ratio-conversion":
                    resultado = ejecutarConExecutor(() -> handleRatioConversion(desde, hasta));
                    break;

                default:
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    sendError(resp, "Informe no soportado: " + path);
                    return;
            }

            if (resultado == null) {
                // Si el resultado es null, devolver estructura vacía según el tipo de informe
                switch (path) {
                    case "/ventas-empleado":
                    case "/ventas-producto":
                        resultado = new ArrayList<>();
                        break;
                    case "/presupuestos-estado":
                    case "/facturacion-mensual":
                    case "/ratio-conversion":
                        resultado = new HashMap<>();
                        break;
                    default:
                        resultado = new ArrayList<>();
                        break;
                }
            }
            System.out.println("[INFORME] Enviando respuesta - resultado tipo: " + (resultado != null ? resultado.getClass().getSimpleName() : "null") + 
                             ", tamaño: " + (resultado instanceof List ? ((List<?>) resultado).size() : 
                                           resultado instanceof Map ? ((Map<?, ?>) resultado).size() : "N/A"));
            sendSuccess(resp, resultado);

        } catch (Exception ex) {
            System.err.println("[INFORME] EXCEPCIÓN CAPTURADA: " + ex.getClass().getSimpleName());
            System.err.println("[INFORME] Mensaje: " + ex.getMessage());
            ex.printStackTrace(); // Log para debugging
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            String errorMsg = ex.getMessage();
            if (errorMsg == null || errorMsg.trim().isEmpty()) {
                errorMsg = ex.getClass().getSimpleName() + ": Error desconocido al generar informe";
            } else {
                errorMsg = "Error al generar informe: " + errorMsg;
            }
            try {
                sendError(resp, errorMsg);
            } catch (IOException ioEx) {
                ioEx.printStackTrace();
            }
        }
    }

    @Override
    public void destroy() {
        // Cerrar el ExecutorService cuando el servlet se destruye
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

    private <T> T ejecutarConThread(Callable<T> tarea) throws Exception {
        AtomicReference<T> resultado = new AtomicReference<>();
        AtomicReference<Exception> excepcion = new AtomicReference<>();

        Thread hilo = new Thread(() -> {
            try {
                resultado.set(tarea.call());
            } catch (Exception e) {
                excepcion.set(e);
            }
        });
        hilo.start();
        try {
            hilo.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new Exception("Error ejecutando hilo: " + e.getMessage(), e);
        }

        if (excepcion.get() != null) {
            throw excepcion.get();
        }

        return resultado.get();
    }

    private <T> T ejecutarConExecutor(Callable<T> tarea) throws Exception {
        Future<T> future = executor.submit(tarea);
        try {
            return future.get(); // Espera a que termine la tarea y devuelve el resultado
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new Exception("Error ejecutando tarea: interrupción", e);
        } catch (ExecutionException e) {
            Throwable causa = e.getCause();
            if (causa instanceof Exception) {
                throw (Exception) causa;
            }
            throw new Exception("Error ejecutando tarea: " + e.getMessage(), e);
        }
    }

    // ----------------------- HANDLE INFORMES ------------------------
    private List<Map<String, Object>> handleVentasPorEmpleado(LocalDate desde, LocalDate hasta) {
        System.out.println("[INFORME] ========================================");
        System.out.println("[INFORME] Iniciando handleVentasPorEmpleado");
        System.out.println("[INFORME] Parámetros: desde=" + desde + ", hasta=" + hasta);
        System.out.println("[INFORME] ========================================");
        
        // Crear EntityManager en el hilo secundario
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();
            
            List<Factura> facturas = em.createQuery(
                    "SELECT f FROM Factura f " +
                            "LEFT JOIN FETCH f.cliente_pagador " +
                            "LEFT JOIN FETCH f.empleado",
                    Factura.class
            ).getResultList();
            
            tx.commit();
            
            System.out.println("[INFORME] Total facturas encontradas en BD: " + facturas.size());
            
            // Mostrar todas las fechas de facturas para debugging
            if (facturas.size() > 0) {
                System.out.println("[INFORME] Fechas de todas las facturas:");
                facturas.forEach(f -> {
                    System.out.println("  - Factura ID: " + f.getId_factura() + 
                        ", Fecha: " + f.getFecha_emision() + 
                        ", Empleado: " + (f.getEmpleado() != null ? f.getEmpleado().getNombre() : "null") +
                        ", Total: " + f.getTotal());
                });
            }

            // Filtrar por fechas
            int facturasAntesFiltroDesde = facturas.size();
            if (desde != null) {
                facturas = facturas.stream()
                        .filter(f -> {
                            if (f.getFecha_emision() == null) {
                                System.out.println("[INFORME] Factura ID " + f.getId_factura() + " tiene fecha_emision NULL, excluida");
                                return false;
                            }
                            boolean pasa = !f.getFecha_emision().isBefore(desde);
                            if (!pasa) {
                                System.out.println("[INFORME] Factura ID " + f.getId_factura() + " fecha " + f.getFecha_emision() + " es anterior a desde " + desde);
                            }
                            return pasa;
                        })
                        .collect(Collectors.toList());
                System.out.println("[INFORME] Después de filtrar desde " + desde + ": " + facturas.size() + " facturas (de " + facturasAntesFiltroDesde + ")");
            }
            
            int facturasAntesFiltroHasta = facturas.size();
            if (hasta != null) {
                facturas = facturas.stream()
                        .filter(f -> {
                            if (f.getFecha_emision() == null) {
                                return false;
                            }
                            boolean pasa = !f.getFecha_emision().isAfter(hasta);
                            if (!pasa) {
                                System.out.println("[INFORME] Factura ID " + f.getId_factura() + " fecha " + f.getFecha_emision() + " es posterior a hasta " + hasta);
                            }
                            return pasa;
                        })
                        .collect(Collectors.toList());
                System.out.println("[INFORME] Después de filtrar hasta " + hasta + ": " + facturas.size() + " facturas (de " + facturasAntesFiltroHasta + ")");
            }
            
            // Debug: mostrar facturas que pasaron el filtro
            if (facturas.size() > 0) {
                System.out.println("[INFORME] Facturas que pasaron el filtro:");
                facturas.forEach(f -> System.out.println("  - Factura ID: " + f.getId_factura() + 
                    ", Fecha: " + f.getFecha_emision() + 
                    ", Empleado: " + (f.getEmpleado() != null ? f.getEmpleado().getNombre() : "null")));
            } else {
                System.out.println("[INFORME] No hay facturas que pasen el filtro de fechas");
            }

            // Agrupar por empleado
            Map<String, Double> acumulado = new HashMap<>();
            int facturasConEmpleado = 0;
            for (Factura f : facturas) {
                if (f.getEmpleado() == null) {
                    continue;
                }
                facturasConEmpleado++;
                
                String nombre = safeUpperFirst(f.getEmpleado().getNombre());
                double total = f.getTotal();
                acumulado.merge(nombre, total, Double::sum);
            }

            System.out.println("[INFORME] Facturas con empleado: " + facturasConEmpleado + ", Empleados únicos: " + acumulado.size());

            // Construir resultado
            List<Map<String, Object>> resultado = new ArrayList<>();
            for (Map.Entry<String, Double> e : acumulado.entrySet()) {
                Map<String, Object> item = new HashMap<>();
                item.put("nombre", e.getKey());
                item.put("total", e.getValue());
                resultado.add(item);
            }

            System.out.println("[INFORME] Resultado final: " + resultado.size() + " empleados");
            
            // Debug--> mostrar el contenido del resultado antes de devolverlo
            System.out.println("[INFORME] Contenido del resultado:");
            for (Map<String, Object> item : resultado) {
                System.out.println("  - " + item);
            }
            
            // Intentar serializar a JSON para verificar
            try {
                String jsonTest = JsonUtil.toJson(resultado);
                System.out.println("[INFORME] JSON serializado (primeros 500 chars): " + 
                    (jsonTest.length() > 500 ? jsonTest.substring(0, 500) + "..." : jsonTest));
            } catch (Exception e) {
                System.err.println("[INFORME] Error al serializar resultado a JSON: " + e.getMessage());
                e.printStackTrace();
            }
            
            return resultado;
        } catch (Exception e) {
            System.err.println("Error en handleVentasPorEmpleado: " + e.getMessage());
            e.printStackTrace();
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Error al generar informe de ventas por empleado: " + e.getMessage(), e);
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    private Map<String, Integer> handleEstadoPresupuestos(LocalDate desde, LocalDate hasta) {
        System.out.println("[INFORME] Iniciando handleEstadoPresupuestos - desde: " + desde + ", hasta: " + hasta);
        
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();
            
            List<Presupuestos> lista = em.createQuery("SELECT p FROM Presupuestos p", Presupuestos.class)
                    .getResultList();
            
            tx.commit();
            
            System.out.println("[INFORME] Total presupuestos encontrados: " + lista.size());
            
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
                String raw = p.getEstado() != null ? p.getEstado().trim().toUpperCase(Locale.ROOT) : "";
                String estadoNorm;
                // Normalizar a estados válidos: PENDIENTE, APROBADO, RECHAZADO
                if (raw.equals("APROBADO") || raw.equals("APROBADA")) {
                    estadoNorm = "APROBADO";
                } else if (raw.equals("RECHAZADO") || raw.equals("RECHAZADA")) {
                    estadoNorm = "RECHAZADO";
                } else {
                    estadoNorm = "PENDIENTE"; // Por defecto
                }
                conteo.merge(estadoNorm, 1, Integer::sum);
            }

            System.out.println("[INFORME] Resultado estado presupuestos: " + conteo);
            return conteo;
        } catch (Exception e) {
            System.err.println("Error en handleEstadoPresupuestos: " + e.getMessage());
            e.printStackTrace();
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Error al generar informe de estado de presupuestos: " + e.getMessage(), e);
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    private Map<String, Double> handleFacturacionMensual(LocalDate desde, LocalDate hasta) {
        System.out.println("[INFORME] Iniciando handleFacturacionMensual - desde: " + desde + ", hasta: " + hasta);
        
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();
            
            List<Factura> facturas = em.createQuery(
                    "SELECT f FROM Factura f",
                    Factura.class
            ).getResultList();
            
            tx.commit();
            
            System.out.println("[INFORME] Total facturas encontradas: " + facturas.size());
            
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
                if (f.getFecha_emision() == null)
                    continue;
                String clave = YearMonth.from(f.getFecha_emision()).toString();
                porMes.merge(clave, f.getTotal(), Double::sum);
            }

            System.out.println("[INFORME] Resultado facturación mensual: " + porMes.size() + " meses");
            return porMes;
        } catch (Exception e) {
            System.err.println("Error en handleFacturacionMensual: " + e.getMessage());
            e.printStackTrace();
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Error al generar informe de facturación mensual: " + e.getMessage(), e);
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    private List<Map<String, Object>> handleVentasPorProducto(LocalDate desde, LocalDate hasta) {
        System.out.println("[INFORME] Iniciando handleVentasPorProducto - desde: " + desde + ", hasta: " + hasta);
        
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();
            
            // Maldita mayuscula
            List<FacturaProducto> items = em.createQuery(
                    "SELECT fp FROM FacturaProducto fp " +
                            "LEFT JOIN FETCH fp.factura " +
                            "LEFT JOIN FETCH fp.Producto",
                    FacturaProducto.class
            ).getResultList();
            
            tx.commit();
            
            System.out.println("[INFORME] Total FacturaProducto encontrados: " + items.size());
            
            // Si no hay datos, intentar consulta directa a la tabla
            if (items.isEmpty()) {
                tx = em.getTransaction();
                tx.begin();
                Long count = (Long) em.createQuery("SELECT COUNT(fp) FROM FacturaProducto fp").getSingleResult();
                tx.commit();
                System.out.println("[INFORME] COUNT directo de FacturaProducto: " + count);
                
                // Verificar si hay facturas
                tx = em.getTransaction();
                tx.begin();
                Long facturasCount = (Long) em.createQuery("SELECT COUNT(f) FROM Factura f").getSingleResult();
                tx.commit();
                System.out.println("[INFORME] Total facturas en BD: " + facturasCount);
            }
            
            Map<String, Double> acumulado = new HashMap<>();
            int itemsProcesados = 0;
            int itemsFiltrados = 0;

            for (FacturaProducto fp : items) {
                Factura factura = fp.getFactura();
                if (factura != null) {
                    LocalDate fecha = factura.getFecha_emision();
                    if ((desde != null && fecha != null && fecha.isBefore(desde)) || 
                        (hasta != null && fecha != null && fecha.isAfter(hasta))) {
                        itemsFiltrados++;
                        continue;
                    }
                }
                if (fp.getProducto() == null) {
                    itemsFiltrados++;
                    continue;
                }
                itemsProcesados++;
                String nombre = safeUpperFirst(fp.getProducto().getNombre());
                double subtotal = fp.getSubtotal() == 0 ? fp.getCantidad() * fp.getPrecio_unitario() : fp.getSubtotal();
                acumulado.merge(nombre, subtotal, Double::sum);
            }

            System.out.println("[INFORME] Items procesados: " + itemsProcesados + ", filtrados: " + itemsFiltrados + ", productos únicos: " + acumulado.size());

            List<Map<String, Object>> resultado = new ArrayList<>();
            for (Map.Entry<String, Double> e : acumulado.entrySet()) {
                Map<String, Object> item = new HashMap<>();
                item.put("producto", e.getKey());
                item.put("total", e.getValue());
                resultado.add(item);
            }

            System.out.println("[INFORME] Resultado final ventas por producto: " + resultado.size() + " productos");
            return resultado;
        } catch (Exception e) {
            // Log detallado del error para debugging
            System.err.println("Error en handleVentasPorProducto: " + e.getMessage());
            e.printStackTrace();
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Error al generar informe de ventas por producto: " + e.getMessage(), e);
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    private Map<String, Integer> handleRatioConversion(LocalDate desde, LocalDate hasta) {
        System.out.println("[INFORME] Iniciando handleRatioConversion - desde: " + desde + ", hasta: " + hasta);
        
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();
            
            List<Presupuestos> lista = em.createQuery("SELECT p FROM Presupuestos p", Presupuestos.class)
                    .getResultList();
            
            tx.commit();
            
            System.out.println("[INFORME] Total presupuestos encontrados: " + lista.size());
            
            int convertidos = 0, rechazados = 0, pendientes = 0, otros = 0;

            for (Presupuestos p : lista) {
                String estado = p.getEstado() != null ? p.getEstado().toUpperCase(Locale.ROOT).trim() : "";
                // Estados válidos: PENDIENTE, APROBADO, RECHAZADO
                if (estado.equals("APROBADO")) {
                    convertidos++;
                } else if (estado.equals("RECHAZADO")) {
                    rechazados++;
                } else if (estado.equals("PENDIENTE")) {
                    pendientes++;
                } else {
                    otros++; // Estados no reconocidos
                }
            }

            Map<String, Integer> resultado = new HashMap<>();
            resultado.put("Convertidos", convertidos);
            resultado.put("Rechazados", rechazados);
            resultado.put("Pendientes", pendientes);
            resultado.put("Otros", otros);

            System.out.println("[INFORME] Resultado ratio conversión: " + resultado);
            return resultado;
        } catch (Exception e) {
            System.err.println("Error en handleRatioConversion: " + e.getMessage());
            e.printStackTrace();
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Error al generar informe de ratio de conversión: " + e.getMessage(), e);
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    // ------------------------- HELPERS --------------------------
    private LocalDate parseDate(String raw) {
        if (raw == null || raw.trim().isEmpty())
            return null;
        try {
            return LocalDate.parse(raw.trim(), DATE_FMT);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private String safeUpperFirst(String value) {
        if (value == null)
            return "Desconocido";
        String trimmed = value.trim();
        return trimmed.isEmpty() ? "Desconocido" : trimmed.substring(0, 1).toUpperCase() + trimmed.substring(1);
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
        
        // Getters necesarios para Jackson
        public boolean isSuccess() {
            return success;
        }
        
        public Object getData() {
            return data;
        }
    }

    private static class ErrorMsg {
        final String error;

        ErrorMsg(String error) {
            this.error = error;
        }
        
        // Getter necesario para Jackson
        public String getError() {
            return error;
        }
    }

}
