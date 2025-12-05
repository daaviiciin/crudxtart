package com.example.crudxtart.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.List;

import com.example.crudxtart.models.Pagos;
import com.example.crudxtart.models.Factura;
import com.example.crudxtart.service.PagosService;
import com.example.crudxtart.service.FacturaService;
import com.example.crudxtart.utils.JsonUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.inject.Inject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/pagos")
public class PagosServlet extends HttpServlet {


    // 1º Cambio para el log de errores
    private static final Logger logger = Logger.getLogger(PagosServlet.class.getName());
    private static final String CODIGO_LOG = "CTL-PAG-";

    @Inject
    private PagosService pagosService;

    @Inject
    private FacturaService facturaService;


    // ============================================================
    // GET (todos o por id)
    // ============================================================
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        logger.info("[" + CODIGO_LOG + "001] doGet - inicio"); // CAMBIO LOG
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            String idParam = req.getParameter("id");

            // GET /pagos?id=x
            if (idParam != null) {
                Integer id = Integer.parseInt(idParam);
                Pagos pago = pagosService.findPagosById(id);

                if (pago == null) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    sendError(resp, "Pago no encontrado");
                    return;
                }

                sendSuccess(resp, pago);
                return;
            }

            // GET /pagos
            List<Pagos> lista = pagosService.findAllPagos();
            sendSuccess(resp, lista);

        } catch (Exception ex) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            sendError(resp, ex.getMessage());
        }
    }

    // ============================================================
    // POST (crear)
    // ============================================================
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        logger.info("[" + CODIGO_LOG + "002] doPost - inicio"); // CAMBIO LOG
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            String body = readBody(req);

            // Parsear JSON para obtener campos
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(body);

            // Crear nuevo objeto Pagos
            Pagos pago = new Pagos();
            pago.setId_pago(null); // Asegurar que el ID sea null para crear nuevo registro

            // Extraer y validar campos básicos
            if (jsonNode.has("importe") && jsonNode.get("importe").isNumber()) {
                pago.setImporte(jsonNode.get("importe").asDouble());
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                sendError(resp, "El campo 'importe' es obligatorio y debe ser un número");
                return;
            }

            if (jsonNode.has("metodo_pago") && jsonNode.get("metodo_pago").isTextual()) {
                pago.setMetodo_pago(jsonNode.get("metodo_pago").asText());
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                sendError(resp, "El campo 'metodo_pago' es obligatorio");
                return;
            }

            if (jsonNode.has("estado") && jsonNode.get("estado").isTextual()) {
                pago.setEstado(jsonNode.get("estado").asText());
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                sendError(resp, "El campo 'estado' es obligatorio");
                return;
            }

            // Manejar fecha_pago (o fecha)
            if (jsonNode.has("fecha_pago") && jsonNode.get("fecha_pago").isTextual()) {
                try {
                    pago.setFecha_pago(java.time.LocalDate.parse(jsonNode.get("fecha_pago").asText()));
                } catch (Exception e) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    sendError(resp, "Formato de fecha_pago inválido. Use formato YYYY-MM-DD");
                    return;
                }
            } else if (jsonNode.has("fecha") && jsonNode.get("fecha").isTextual()) {
                try {
                    pago.setFecha_pago(java.time.LocalDate.parse(jsonNode.get("fecha").asText()));
                } catch (Exception e) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    sendError(resp, "Formato de fecha inválido. Use formato YYYY-MM-DD");
                    return;
                }
            } else {
                // Si no viene fecha, establecerla automáticamente
                pago.setFecha_pago(java.time.LocalDate.now());
            }

            // Manejar factura (obligatorio)
            if (jsonNode.has("id_factura") && jsonNode.get("id_factura").isNumber()) {
                Integer facturaId = jsonNode.get("id_factura").asInt();
                Factura fac = facturaService.findFacturaById(facturaId);
                if (fac != null) {
                    pago.setFactura(fac);
                } else {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    sendError(resp, "Factura no encontrada con id: " + facturaId);
                    return;
                }
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                sendError(resp, "El campo 'id_factura' es obligatorio");
                return;
            }

            Pagos creado = pagosService.createPagos(pago);

            // Verificar que el ID se generó correctamente
            if (creado.getId_pago() == null) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                sendError(resp, "Error: No se pudo generar el ID del pago");
                return;
            }

            sendSuccess(resp, creado);

        } catch (com.fasterxml.jackson.core.JsonProcessingException ex) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            sendError(resp, "Error en el formato JSON: " + ex.getMessage());
        } catch (Exception ex) {
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

        logger.info("[" + CODIGO_LOG + "003] doPut - inicio"); // CAMBIO LOG
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            String body = readBody(req);

            // Parsear JSON para verificar qué campos están presentes
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(body);

            // Verificar que id_pago esté presente
            if (!jsonNode.has("id_pago") || !jsonNode.get("id_pago").isNumber()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                sendError(resp, "El campo id_pago es obligatorio para actualizar");
                return;
            }

            Integer idPago = jsonNode.get("id_pago").asInt();

            // Cargar el pago existente para preservar campos no enviados
            Pagos existente = pagosService.findPagosById(idPago);
            if (existente == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                sendError(resp, "Pago no encontrado");
                return;
            }

            // Actualizar solo los campos enviados (actualización parcial)
            if (jsonNode.has("importe") && jsonNode.get("importe").isNumber()) {
                double nuevoImporte = jsonNode.get("importe").asDouble();
                if (nuevoImporte > 0) {
                    existente.setImporte(nuevoImporte);
                }
            }

            if (jsonNode.has("metodo_pago") && jsonNode.get("metodo_pago").isTextual()) {
                String nuevoMetodo = jsonNode.get("metodo_pago").asText();
                if (nuevoMetodo != null && !nuevoMetodo.trim().isEmpty()) {
                    existente.setMetodo_pago(nuevoMetodo);
                }
            }

            if (jsonNode.has("estado") && jsonNode.get("estado").isTextual()) {
                String nuevoEstado = jsonNode.get("estado").asText();
                if (nuevoEstado != null && !nuevoEstado.trim().isEmpty()) {
                    existente.setEstado(nuevoEstado);
                }
            }

            if (jsonNode.has("fecha_pago") && jsonNode.get("fecha_pago").isTextual()) {
                try {
                    String fechaStr = jsonNode.get("fecha_pago").asText();
                    if (fechaStr != null && !fechaStr.trim().isEmpty()) {
                        existente.setFecha_pago(java.time.LocalDate.parse(fechaStr));
                    }
                } catch (Exception e) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    sendError(resp, "Formato de fecha_pago inválido. Use formato YYYY-MM-DD");
                    return;
                }
            } else if (jsonNode.has("fecha") && jsonNode.get("fecha").isTextual()) {
                try {
                    String fechaStr = jsonNode.get("fecha").asText();
                    if (fechaStr != null && !fechaStr.trim().isEmpty()) {
                        existente.setFecha_pago(java.time.LocalDate.parse(fechaStr));
                    }
                } catch (Exception e) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    sendError(resp, "Formato de fecha inválido. Use formato YYYY-MM-DD");
                    return;
                }
            }

            // Manejar factura si viene en el JSON
            if (jsonNode.has("id_factura") && jsonNode.get("id_factura").isNumber()) {
                Integer facturaId = jsonNode.get("id_factura").asInt();
                Factura fac = facturaService.findFacturaById(facturaId);
                if (fac != null) {
                    existente.setFactura(fac);
                } else {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    sendError(resp, "Factura no encontrada con id: " + facturaId);
                    return;
                }
            }

            Pagos actualizado = pagosService.upLocalDatePagos(existente);
            sendSuccess(resp, actualizado);

        } catch (com.fasterxml.jackson.core.JsonProcessingException ex) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            sendError(resp, "Error en el formato JSON: " + ex.getMessage());
        } catch (Exception ex) {
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

        logger.info("[" + CODIGO_LOG + "004] doDelete - inicio"); // CAMBIO LOG
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
            pagosService.deletePagos(id);

            sendSuccess(resp, null);

        } catch (Exception ex) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            sendError(resp, ex.getMessage());
        }
    }

    // ============================================================
    // Helpers
    // ============================================================
    private String readBody(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = req.getReader()) {
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
        }
        return sb.toString();
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

        ErrorMsg(String error) {
            this.error = error;
        }

        public String getError() { return error; }
    }
}
