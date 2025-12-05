package com.example.crudxtart.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.List;

import com.example.crudxtart.models.Cliente;
import com.example.crudxtart.models.Empleado;
import com.example.crudxtart.models.Factura;
import com.example.crudxtart.models.PresupuestoProducto;
import com.example.crudxtart.models.Presupuestos;
import com.example.crudxtart.models.Producto;
import com.example.crudxtart.service.ClienteService;
import com.example.crudxtart.service.EmpleadoService;
import com.example.crudxtart.service.PresupuestosService;
import com.example.crudxtart.service.ProductoService;
import com.example.crudxtart.utils.JsonUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.inject.Inject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/presupuestos/*")
public class PresupuestosServlet extends HttpServlet {

    // Logger + prefijo de códigos
    private static final Logger logger = Logger.getLogger(PresupuestosServlet.class.getName());
    private static final String CODIGO_LOG = "CTL-PRE-";

    @Inject
    private PresupuestosService presupuestosService;

    @Inject
    private EmpleadoService empleadoService;

    @Inject
    private ClienteService clienteService;

    @Inject
    private ProductoService productoService;


    // ============================================================
    // GET (todos o por id)
    // ============================================================
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        logger.info("[" + CODIGO_LOG + "001] doGet - inicio");
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            String idParam = req.getParameter("id");

            // GET /presupuestos?id=x
            if (idParam != null) {
                Integer id = Integer.parseInt(idParam);
                Presupuestos prep = presupuestosService.findPresupuestoById(id);

                if (prep == null) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    sendError(resp, "Presupuesto no encontrado");
                    return;
                }

                sendSuccess(resp, prep);
                return;
            }

            // GET /presupuestos
            List<Presupuestos> lista = presupuestosService.findAllPresupuestos();
            sendSuccess(resp, lista);

        } catch (NumberFormatException ex) {
            logger.severe("[" + CODIGO_LOG + "008] ERROR doGet - ID inválido: " + ex.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            sendError(resp, "ID inválido: debe ser un número");
        } catch (Exception ex) {
            logger.severe("[" + CODIGO_LOG + "009] ERROR doGet Presupuestos: " + ex.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            sendError(resp, ex.getMessage());
        }
    }

    // ============================================================
    // POST (crear / generar facturas)
    // ============================================================
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        logger.info("[" + CODIGO_LOG + "002] doPost - inicio");
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            String pathInfo = req.getPathInfo();
            if (pathInfo != null && pathInfo.matches("/\\d+/generar-facturas")) {
                handleGenerarFacturas(req, resp);
                return;
            }

            String body = readBody(req);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(body);

            Presupuestos prep = new Presupuestos();
            prep.setId_Presupuesto(null);

            // presupuesto (double)
            if (jsonNode.has("presupuesto") && jsonNode.get("presupuesto").isNumber()) {
                prep.setPresupuesto(jsonNode.get("presupuesto").asDouble());
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                sendError(resp, "El campo 'presupuesto' es obligatorio y debe ser un número");
                return;
            }

            // estado
            if (jsonNode.has("estado") && jsonNode.get("estado").isTextual()) {
                prep.setEstado(jsonNode.get("estado").asText());
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                sendError(resp, "El campo 'estado' es obligatorio");
                return;
            }

            // fecha_apertura
            if (jsonNode.has("fecha_apertura") && jsonNode.get("fecha_apertura").isTextual()) {
                try {
                    prep.setFecha_apertura(java.time.LocalDate.parse(jsonNode.get("fecha_apertura").asText()));
                } catch (Exception e) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    sendError(resp, "Formato de fecha_apertura inválido. Use formato YYYY-MM-DD");
                    return;
                }
            } else {
                prep.setFecha_apertura(java.time.LocalDate.now());
            }

            // fecha_cierre (opcional)
            if (jsonNode.has("fecha_cierre") &&
                    jsonNode.get("fecha_cierre").isTextual() &&
                    !jsonNode.get("fecha_cierre").asText().isEmpty()) {

                try {
                    prep.setFecha_cierre(java.time.LocalDate.parse(jsonNode.get("fecha_cierre").asText()));
                } catch (Exception e) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    sendError(resp, "Formato de fecha_cierre inválido. Use formato YYYY-MM-DD");
                    return;
                }
            }

            // empleado
            if (jsonNode.has("id_empleado") && jsonNode.get("id_empleado").isNumber()) {
                Integer empleadoId = jsonNode.get("id_empleado").asInt();
                Empleado emp = empleadoService.findEmpleadoById(empleadoId);
                if (emp != null) {
                    prep.setEmpleado(emp);
                } else {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    sendError(resp, "Empleado no encontrado con id: " + empleadoId);
                    return;
                }
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                sendError(resp, "El campo 'id_empleado' es obligatorio");
                return;
            }

            // cliente pagador
            if (jsonNode.has("id_cliente_pagador") && jsonNode.get("id_cliente_pagador").isNumber()) {
                Integer clienteId = jsonNode.get("id_cliente_pagador").asInt();
                Cliente cli = clienteService.findClienteById(clienteId);
                if (cli != null) {
                    prep.setCliente_pagador(cli);
                } else {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    sendError(resp, "Cliente pagador no encontrado con id: " + clienteId);
                    return;
                }
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                sendError(resp, "El campo 'id_cliente_pagador' es obligatorio");
                return;
            }

            // cliente beneficiario
            if (jsonNode.has("id_cliente_beneficiario") && jsonNode.get("id_cliente_beneficiario").isNumber()) {
                Integer clienteId = jsonNode.get("id_cliente_beneficiario").asInt();
                Cliente cli = clienteService.findClienteById(clienteId);
                if (cli != null) {
                    prep.setCliente_beneficiario(cli);
                } else {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    sendError(resp, "Cliente beneficiario no encontrado con id: " + clienteId);
                    return;
                }
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                sendError(resp, "El campo 'id_cliente_beneficiario' es obligatorio");
                return;
            }

            // productos (array obligatorio)
            if (jsonNode.has("productos") && jsonNode.get("productos").isArray()) {
                java.util.List<PresupuestoProducto> productos = new java.util.ArrayList<>();
                for (JsonNode productoNode : jsonNode.get("productos")) {
                    PresupuestoProducto pp = new PresupuestoProducto();

                    if (!productoNode.has("id_producto") || !productoNode.get("id_producto").isNumber()) {
                        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        sendError(resp, "Cada producto debe tener un 'id_producto' válido");
                        return;
                    }

                    Integer productoId = productoNode.get("id_producto").asInt();
                    Producto prod = productoService.findProductoById(productoId);
                    if (prod == null) {
                        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        sendError(resp, "Producto no encontrado con id: " + productoId);
                        return;
                    }
                    pp.setProducto(prod);

                    Integer beneficiarioId =
                            productoNode.has("id_cliente_beneficiario") &&
                                    productoNode.get("id_cliente_beneficiario").isNumber()
                                    ? productoNode.get("id_cliente_beneficiario").asInt()
                                    : prep.getId_cliente_beneficiario();

                    Cliente beneficiario = clienteService.findClienteById(beneficiarioId);
                    if (beneficiario == null) {
                        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        sendError(resp, "Cliente beneficiario no encontrado con id: " + beneficiarioId);
                        return;
                    }
                    pp.setCliente_beneficiario(beneficiario);

                    int cantidad =
                            productoNode.has("cantidad") && productoNode.get("cantidad").isNumber()
                                    ? productoNode.get("cantidad").asInt()
                                    : 1;
                    pp.setCantidad(cantidad);

                    double precioUnitario =
                            productoNode.has("precio_unitario") && productoNode.get("precio_unitario").isNumber()
                                    ? productoNode.get("precio_unitario").asDouble()
                                    : prod.getPrecio();
                    pp.setPrecio_unitario(precioUnitario);

                    double subtotal =
                            productoNode.has("subtotal") && productoNode.get("subtotal").isNumber()
                                    ? productoNode.get("subtotal").asDouble()
                                    : precioUnitario * cantidad;
                    pp.setSubtotal(subtotal);

                    pp.setPresupuesto(prep);
                    productos.add(pp);
                }
                prep.setPresupuestoProductos(productos);
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                sendError(resp, "El campo 'productos' (array) es obligatorio");
                return;
            }

            Presupuestos creado = presupuestosService.createPresupuesto(prep);

            if (creado.getId_Presupuesto() == null) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                sendError(resp, "Error: No se pudo generar el ID del presupuesto");
                return;
            }

            sendSuccess(resp, creado);

        } catch (com.fasterxml.jackson.core.JsonProcessingException ex) {
            logger.severe("[" + CODIGO_LOG + "010] ERROR JSON doPost Presupuestos: " + ex.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            sendError(resp, "Error en el formato JSON: " + ex.getMessage());
        } catch (Exception ex) {
            logger.severe("[" + CODIGO_LOG + "011] ERROR doPost Presupuestos: " + ex.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            sendError(resp, ex.getMessage());
        }
    }

    // ============================================================
    // PUT (actualizar)
    // ============================================================
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        logger.info("[" + CODIGO_LOG + "003] doPut - inicio");
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            String body = readBody(req);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(body);

            if (!jsonNode.has("id_Presupuesto") || !jsonNode.get("id_Presupuesto").isNumber()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                sendError(resp, "El campo id_Presupuesto es obligatorio para actualizar");
                return;
            }

            Integer idPresupuesto = jsonNode.get("id_Presupuesto").asInt();

            Presupuestos existente = presupuestosService.findPresupuestoById(idPresupuesto);
            if (existente == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                sendError(resp, "Presupuesto no encontrado");
                return;
            }

            String estadoOriginal = existente.getEstado();

            if (jsonNode.has("presupuesto") && jsonNode.get("presupuesto").isNumber()) {
                double nuevoPresupuesto = jsonNode.get("presupuesto").asDouble();
                if (nuevoPresupuesto > 0) {
                    existente.setPresupuesto(nuevoPresupuesto);
                }
            }

            if (jsonNode.has("estado") && jsonNode.get("estado").isTextual()) {
                String nuevoEstado = jsonNode.get("estado").asText();
                if (nuevoEstado != null && !nuevoEstado.trim().isEmpty()) {
                    existente.setEstado(nuevoEstado);
                }
            }

            if (jsonNode.has("fecha_apertura") && jsonNode.get("fecha_apertura").isTextual()) {
                try {
                    String fechaStr = jsonNode.get("fecha_apertura").asText();
                    if (fechaStr != null && !fechaStr.trim().isEmpty()) {
                        existente.setFecha_apertura(java.time.LocalDate.parse(fechaStr));
                    }
                } catch (Exception e) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    sendError(resp, "Formato de fecha_apertura inválido. Use formato YYYY-MM-DD");
                    return;
                }
            }

            boolean cambiaAAprobado =
                    jsonNode.has("estado") &&
                            jsonNode.get("estado").isTextual() &&
                            jsonNode.get("estado").asText().equalsIgnoreCase("APROBADO") &&
                            (estadoOriginal == null || !estadoOriginal.equalsIgnoreCase("APROBADO"));

            if (jsonNode.has("fecha_cierre") &&
                    jsonNode.get("fecha_cierre").isTextual() &&
                    !cambiaAAprobado) {

                try {
                    String fechaStr = jsonNode.get("fecha_cierre").asText();
                    if (fechaStr != null && !fechaStr.trim().isEmpty()) {
                        existente.setFecha_cierre(java.time.LocalDate.parse(fechaStr));
                    } else {
                        existente.setFecha_cierre(null);
                    }
                } catch (Exception e) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    sendError(resp, "Formato de fecha_cierre inválido. Use formato YYYY-MM-DD");
                    return;
                }
            } else if (!jsonNode.has("fecha_cierre") &&
                    existente.getEstado() != null &&
                    existente.getEstado().equalsIgnoreCase("APROBADO") &&
                    existente.getFecha_cierre() != null) {
                // preservar fecha_cierre existente
            }

            if (jsonNode.has("id_empleado") && jsonNode.get("id_empleado").isNumber()) {
                Integer empleadoId = jsonNode.get("id_empleado").asInt();
                Empleado emp = empleadoService.findEmpleadoById(empleadoId);
                if (emp != null) {
                    existente.setEmpleado(emp);
                } else {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    sendError(resp, "Empleado no encontrado con id: " + empleadoId);
                    return;
                }
            }

            if (jsonNode.has("id_cliente_pagador") && jsonNode.get("id_cliente_pagador").isNumber()) {
                Integer clienteId = jsonNode.get("id_cliente_pagador").asInt();
                Cliente cli = clienteService.findClienteById(clienteId);
                if (cli != null) {
                    existente.setCliente_pagador(cli);
                } else {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    sendError(resp, "Cliente pagador no encontrado con id: " + clienteId);
                    return;
                }
            }

            if (jsonNode.has("id_cliente_beneficiario") && jsonNode.get("id_cliente_beneficiario").isNumber()) {
                Integer clienteId = jsonNode.get("id_cliente_beneficiario").asInt();
                Cliente cli = clienteService.findClienteById(clienteId);
                if (cli != null) {
                    existente.setCliente_beneficiario(cli);
                } else {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    sendError(resp, "Cliente beneficiario no encontrado con id: " + clienteId);
                    return;
                }
            }

            if (jsonNode.has("productos") && jsonNode.get("productos").isArray()) {
                existente.getPresupuestoProductos().clear();

                for (JsonNode productoNode : jsonNode.get("productos")) {
                    PresupuestoProducto pp = new PresupuestoProducto();

                    if (!productoNode.has("id_producto") || !productoNode.get("id_producto").isNumber()) {
                        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        sendError(resp, "Cada producto debe tener un 'id_producto' válido");
                        return;
                    }

                    Integer productoId = productoNode.get("id_producto").asInt();
                    Producto prod = productoService.findProductoById(productoId);
                    if (prod == null) {
                        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        sendError(resp, "Producto no encontrado con id: " + productoId);
                        return;
                    }
                    pp.setProducto(prod);

                    Integer beneficiarioId =
                            productoNode.has("id_cliente_beneficiario") &&
                                    productoNode.get("id_cliente_beneficiario").isNumber()
                                    ? productoNode.get("id_cliente_beneficiario").asInt()
                                    : existente.getId_cliente_beneficiario();

                    Cliente beneficiario = clienteService.findClienteById(beneficiarioId);
                    if (beneficiario == null) {
                        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        sendError(resp, "Cliente beneficiario no encontrado con id: " + beneficiarioId);
                        return;
                    }
                    pp.setCliente_beneficiario(beneficiario);

                    int cantidad =
                            productoNode.has("cantidad") && productoNode.get("cantidad").isNumber()
                                    ? productoNode.get("cantidad").asInt()
                                    : 1;
                    pp.setCantidad(cantidad);

                    double precioUnitario =
                            productoNode.has("precio_unitario") && productoNode.get("precio_unitario").isNumber()
                                    ? productoNode.get("precio_unitario").asDouble()
                                    : prod.getPrecio();
                    pp.setPrecio_unitario(precioUnitario);

                    double subtotal =
                            productoNode.has("subtotal") && productoNode.get("subtotal").isNumber()
                                    ? productoNode.get("subtotal").asDouble()
                                    : precioUnitario * cantidad;
                    pp.setSubtotal(subtotal);

                    pp.setPresupuesto(existente);
                    existente.getPresupuestoProductos().add(pp);
                }
            }

            Presupuestos actualizado = presupuestosService.updatePresupuesto(existente);
            sendSuccess(resp, actualizado);

        } catch (com.fasterxml.jackson.core.JsonProcessingException ex) {
            logger.severe("[" + CODIGO_LOG + "012] ERROR JSON doPut Presupuestos: " + ex.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            sendError(resp, "Error en el formato JSON: " + ex.getMessage());
        } catch (Exception ex) {
            logger.severe("[" + CODIGO_LOG + "013] ERROR doPut Presupuestos: " + ex.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            sendError(resp, ex.getMessage());
        }
    }

    // ============================================================
    // DELETE (eliminar)
    // ============================================================
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        logger.info("[" + CODIGO_LOG + "004] doDelete - inicio");
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            String idParam = req.getParameter("id");

            if (idParam == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                sendError(resp, "Debe proporcionar ?id= para eliminar");
                return;
            }

            Integer id = Integer.parseInt(idParam);

            Presupuestos prep = presupuestosService.findPresupuestoById(id);
            if (prep == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                sendError(resp, "Presupuesto no encontrado");
                return;
            }

            presupuestosService.deletePresupuesto(id);

            sendSuccess(resp, null);

        } catch (NumberFormatException ex) {
            logger.severe("[" + CODIGO_LOG + "014] ERROR doDelete - ID inválido: " + ex.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            sendError(resp, "ID inválido: debe ser un número");
        } catch (Exception ex) {
            logger.severe("[" + CODIGO_LOG + "015] ERROR doDelete Presupuestos: " + ex.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            sendError(resp, ex.getMessage());
        }
    }

    // ============================================================
    // Generar facturas desde presupuesto
    // ============================================================
    private void handleGenerarFacturas(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        logger.info("[" + CODIGO_LOG + "016] handleGenerarFacturas - inicio");

        try {
            String pathInfo = req.getPathInfo();
            String[] pathParts = pathInfo.split("/");
            if (pathParts.length < 2) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                sendError(resp, "ID de presupuesto no válido en la ruta");
                return;
            }

            Integer presupuestoId;
            try {
                presupuestoId = Integer.parseInt(pathParts[1]);
            } catch (NumberFormatException e) {
                logger.severe("[" + CODIGO_LOG + "017] ERROR handleGenerarFacturas - ID inválido: " + e.getMessage());
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                sendError(resp, "ID de presupuesto inválido: " + pathParts[1]);
                return;
            }

            String body = readBody(req);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(body);

            if (!jsonNode.has("num_plazos") || !jsonNode.get("num_plazos").isNumber()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                sendError(resp, "El campo 'num_plazos' es obligatorio y debe ser un número");
                return;
            }

            Integer numPlazos = jsonNode.get("num_plazos").asInt();

            List<Factura> facturas = presupuestosService.generarFacturasDesdePresupuesto(presupuestoId, numPlazos);
            sendSuccess(resp, facturas);

        } catch (com.fasterxml.jackson.core.JsonProcessingException ex) {
            logger.severe("[" + CODIGO_LOG + "018] ERROR JSON handleGenerarFacturas: " + ex.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            sendError(resp, "Error en el formato JSON: " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            logger.severe("[" + CODIGO_LOG + "019] ERROR de validación handleGenerarFacturas: " + ex.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            sendError(resp, ex.getMessage());
        } catch (Exception ex) {
            logger.severe("[" + CODIGO_LOG + "020] ERROR general handleGenerarFacturas: " + ex.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            sendError(resp, "Error al generar facturas: " + ex.getMessage());
        }
    }

    // ============================================================
    // Helpers
    // ============================================================
    private String readBody(HttpServletRequest req) throws IOException {
        logger.fine("[" + CODIGO_LOG + "005] readBody - leyendo cuerpo de la petición");
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = req.getReader()) {
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
        }
        return sb.toString();
    }

    private void sendSuccess(HttpServletResponse resp, Object data) throws IOException {
        logger.fine("[" + CODIGO_LOG + "006] sendSuccess - enviando respuesta de éxito");
        resp.getWriter().write(JsonUtil.toJson(new Response(true, data)));
    }

    private void sendError(HttpServletResponse resp, String msg) throws IOException {
        logger.fine("[" + CODIGO_LOG + "007] sendError - enviando error: " + msg);
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

        ErrorMsg(String error) {
            this.error = error;
        }

        public String getError() { return error; }
    }
}
