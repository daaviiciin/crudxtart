package com.example.crudxtart.servlet;

import com.example.crudxtart.models.Factura;
import com.example.crudxtart.models.FacturaProducto;
import com.example.crudxtart.models.Presupuestos;
import com.example.crudxtart.service.FacturaProductoService;
import com.example.crudxtart.service.FacturaService;
import com.example.crudxtart.service.PresupuestosService;
import com.example.crudxtart.utils.JsonUtil;
import jakarta.inject.Inject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.format.DateTimeFormatter;
import java.time.YearMonth;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@WebServlet("/informes/*")
public class InformesServlet extends HttpServlet {


    @Inject
    private FacturaService facturaService;

    @Inject
    private PresupuestosService presupuestosService;

    @Inject
    private FacturaProductoService facturaProductoService;

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
            switch (path) {

                case "/ventas-empleado":
                    ejecutarConThread(() -> {
                        try {
                            handleVentasPorEmpleado(resp, desde, hasta);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }, resp);
                    break;

                case "/presupuestos-estado":
                    ejecutarConExecutor(() -> {
                        try {
                            handleEstadoPresupuestos(resp, desde, hasta);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }, resp);
                    break;

                case "/facturacion-mensual":
                    ejecutarConExecutor(() -> {
                        try {
                            handleFacturacionMensual(resp, desde, hasta);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }, resp);
                    break;

                case "/ventas-producto":
                    ejecutarConExecutor(() -> {
                        try {
                            handleVentasPorProducto(resp, desde, hasta);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }, resp);
                    break;

                case "/ratio-conversion":
                    ejecutarConExecutor(() -> {
                        try {
                            handleRatioConversion(resp, desde, hasta);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }, resp);
                    break;

                default:
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    sendError(resp, "Informe no soportado: " + path);
            }

        } catch (Exception ex) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            sendError(resp, ex.getMessage());
        }
    }

    private void ejecutarConThread(Runnable tarea, HttpServletResponse resp) throws IOException {
        Thread hilo = new Thread(() -> {
            try {
                tarea.run();
            } catch (Exception e) {
                e.printStackTrace();
                try { sendError(resp, e.getMessage()); } catch (IOException ex) {}
            }
        });
        hilo.start();
        try {
            hilo.join(); // Espera a que termine el hilo para enviar JSON real
        } catch (InterruptedException e) {
            sendError(resp, "Error ejecutando hilo: " + e.getMessage());
        }
    }

    private void ejecutarConExecutor(Runnable tarea, HttpServletResponse resp) throws IOException {
        Future<?> future = executor.submit(tarea);
        try {
            future.get(); // Espera a que termine la tarea antes de enviar JSON
        } catch (InterruptedException | ExecutionException e) {
            sendError(resp, "Error ejecutando tarea: " + e.getMessage());
        }
    }

    // ----------------------- HANDLE INFORMES ------------------------
    private void handleVentasPorEmpleado(HttpServletResponse resp, LocalDate desde, LocalDate hasta) throws IOException {
        List<Factura> facturas = facturaService.findAllFacturas();
        if (desde != null) {
            facturas = facturas.stream().filter(f -> f.getFecha_emision() != null && !f.getFecha_emision().isBefore(desde)).collect(Collectors.toList());
        }
        if (hasta != null) {
            facturas = facturas.stream().filter(f -> f.getFecha_emision() != null && !f.getFecha_emision().isAfter(hasta)).collect(Collectors.toList());
        }

        Map<String, Double> acumulado = new HashMap<>();
        for (Factura f : facturas) {
            if (f.getEmpleado() == null) continue;
            String nombre = safeUpperFirst(f.getEmpleado().getNombre());
            acumulado.merge(nombre, f.getTotal(), Double::sum);
        }

        List<Map<String, Object>> resultado = new ArrayList<>();
        for (Map.Entry<String, Double> e : acumulado.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("nombre", e.getKey());
            item.put("total", e.getValue());
            resultado.add(item);
        }

        sendSuccess(resp, resultado);
    }

    private void handleEstadoPresupuestos(HttpServletResponse resp, LocalDate desde, LocalDate hasta) throws IOException {
        List<Presupuestos> lista = presupuestosService.findAllPresupuestos();
        if (desde != null) lista = lista.stream().filter(p -> p.getFecha_apertura() != null && !p.getFecha_apertura().isBefore(desde)).collect(Collectors.toList());
        if (hasta != null) lista = lista.stream().filter(p -> p.getFecha_apertura() != null && !p.getFecha_apertura().isAfter(hasta)).collect(Collectors.toList());

        Map<String, Integer> conteo = new HashMap<>();
        conteo.put("APROBADO", 0); conteo.put("PENDIENTE", 0); conteo.put("RECHAZADO", 0);

        for (Presupuestos p : lista) {
            String raw = p.getEstado() != null ? p.getEstado().trim().toLowerCase(Locale.ROOT) : "";
            String estadoNorm;
            switch (raw) {
                case "aprobado": case "aceptado": case "cerrado": estadoNorm = "APROBADO"; break;
                case "pendiente": case "en_proceso": case "en proceso": estadoNorm = "PENDIENTE"; break;
                case "rechazado": case "denegado": estadoNorm = "RECHAZADO"; break;
                default: estadoNorm = "PENDIENTE";
            }
            conteo.merge(estadoNorm, 1, Integer::sum);
        }

        sendSuccess(resp, conteo);
    }

    private void handleFacturacionMensual(HttpServletResponse resp, LocalDate desde, LocalDate hasta) throws IOException {
        List<Factura> facturas = facturaService.findAllFacturas();
        if (desde != null) facturas = facturas.stream().filter(f -> f.getFecha_emision() != null && !f.getFecha_emision().isBefore(desde)).collect(Collectors.toList());
        if (hasta != null) facturas = facturas.stream().filter(f -> f.getFecha_emision() != null && !f.getFecha_emision().isAfter(hasta)).collect(Collectors.toList());

        Map<String, Double> porMes = new HashMap<>();
        for (Factura f : facturas) {
            if (f.getFecha_emision() == null) continue;
            String clave = YearMonth.from(f.getFecha_emision()).toString();
            porMes.merge(clave, f.getTotal(), Double::sum);
        }

        sendSuccess(resp, porMes);
    }

    private void handleVentasPorProducto(HttpServletResponse resp, LocalDate desde, LocalDate hasta) throws IOException {
        List<FacturaProducto> items = facturaProductoService.findAllFacturaProductos();
        Map<String, Double> acumulado = new HashMap<>();

        for (FacturaProducto fp : items) {
            Factura factura = fp.getFactura();
            if (factura != null) {
                LocalDate fecha = factura.getFecha_emision();
                if ((desde != null && fecha.isBefore(desde)) || (hasta != null && fecha.isAfter(hasta))) continue;
            }
            if (fp.getProducto() == null) continue;
            String nombre = safeUpperFirst(fp.getProducto().getNombre());
            double subtotal = fp.getSubtotal() == 0 ? fp.getCantidad() * fp.getprecio_unitario() : fp.getSubtotal();
            acumulado.merge(nombre, subtotal, Double::sum);
        }

        List<Map<String, Object>> resultado = new ArrayList<>();
        for (Map.Entry<String, Double> e : acumulado.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("producto", e.getKey());
            item.put("total", e.getValue());
            resultado.add(item);
        }

        sendSuccess(resp, resultado);
    }

    private void handleRatioConversion(HttpServletResponse resp, LocalDate desde, LocalDate hasta) throws IOException {
        List<Presupuestos> lista = presupuestosService.findAllPresupuestos();
        int convertidos=0, rechazados=0, pendientes=0, otros=0;

        for (Presupuestos p : lista) {
            String estado = p.getEstado()!=null ? p.getEstado().toLowerCase(Locale.ROOT) : "";
            switch(estado){
                case "aprobado": case "aceptado": convertidos++; break;
                case "rechazado": rechazados++; break;
                case "pendiente": pendientes++; break;
                default: otros++;
            }
        }

        Map<String, Integer> resultado = new HashMap<>();
        resultado.put("Convertidos", convertidos);
        resultado.put("Rechazados", rechazados);
        resultado.put("Pendientes", pendientes);
        resultado.put("Otros", otros);

        sendSuccess(resp, resultado);
    }

    // ------------------------- HELPERS --------------------------
    private LocalDate parseDate(String raw){ if(raw==null||raw.trim().isEmpty()) return null; try{ return LocalDate.parse(raw.trim(),DATE_FMT);}catch(DateTimeParseException e){return null;} }
    private String safeUpperFirst(String value){ if(value==null) return "Desconocido"; String trimmed=value.trim(); return trimmed.isEmpty()?"Desconocido":trimmed.substring(0,1).toUpperCase()+trimmed.substring(1);}
    private void sendSuccess(HttpServletResponse resp,Object data)throws IOException{resp.getWriter().write(JsonUtil.toJson(new Response(true,data)));}
    private void sendError(HttpServletResponse resp,String msg)throws IOException{resp.getWriter().write(JsonUtil.toJson(new Response(false,new ErrorMsg(msg))));}
    private static class Response{final boolean success; final Object data; Response(boolean success,Object data){this.success=success;this.data=data;}}
    private static class ErrorMsg{final String error; ErrorMsg(String error){this.error=error;}}

}

