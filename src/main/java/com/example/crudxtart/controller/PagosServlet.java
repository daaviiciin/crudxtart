package com.example.crudxtart.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

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

    // LOGGING
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

        logger.info("[" + CODIGO_LOG + "001] doGet Pagos - inicio. id=" + req.getParameter("id"));

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

            // GET /pagos (todos)
            List<Pagos> lista = pagosService.findAllPagos();
            sendSuccess(resp, lista);

        } catch (NumberFormatException ex) {
            logger.severe("[" + CODIGO_LOG + "008] ERROR doGet Pagos - ID inválido: " + ex.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            sendError(resp, "ID inválido: debe ser un número");
        } catch (Exception ex) {
            logger.severe("[" + CODIGO_LOG + "009] ERROR doGet Pagos: " + ex.getMessage());
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

        logger.info("[" + CODIGO_LOG + "002] doPost Pagos - inicio");

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            String body = readBody(req);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(body);

            Pagos pago = new Pagos();
            pago.setId_pago(null);

            // Importe
            if (jsonNode.has("importe") && jsonNode.get("importe").isNumber()) {
                double importe = jsonNode.get("importe").asDouble();
                if (importe <= 0) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    sendError(resp, "El importe debe ser mayor que 0");
                    return;
                }
                pago.setImporte(importe);
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                sendError(resp, "El campo 'importe' es obligatorio y debe ser numérico");
                return;
            }

            // Método de pago
            if (jsonNode.has("metodo_pago") && jsonNode.get("metodo_pago").isTextual()) {
                pago.setMetodo_pago(jsonNode.get("metodo_pago").asText());
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                sendError(resp, "El campo 'metodo_pago' es obligatorio");
                return;
            }

            // Estado
            if (jsonNode.has("estado") && jsonNode.get("estado").isTextual()) {
                pago.setEstado(jsonNode.get("estado").asText());
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                sendError(resp, "El campo 'estado' es obligatorio");
                return;
            }

            // Fecha pago o fecha
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
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                sendError(resp, "Debe proporcionar 'fecha_pago' o 'fecha'");
                return;
            }

            // id_factura
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

            if (creado.getId_pago() == null) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                sendError(resp, "Error: No se pudo generar el ID del pago");
                return;
            }

            sendSuccess(resp, creado);

        } catch (com.fasterxml.jackson.core.JsonProcessingException ex) {
            logger.severe("[" + CODIGO_LOG + "010] ERROR JSON doPost Pagos: " + ex.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            sendError(resp, "Error en el formato JSON: " + ex.getMessage());
        } catch (Exception ex) {
            logger.severe("[" + CODIGO_LOG + "011] ERROR doPost Pagos: " + ex.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            sendError(resp, ex.getMessage());
        }
    }

    // ============================================================
    // PUT (actualizar, parcial)
    // ============================================================
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        logger.info("[" + CODIGO_LOG + "003] doPut Pagos - inicio");

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            String body = readBody(req);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(body);

            // id_pago obligatorio
            if (!jsonNode.has("id_pago") || !jsonNode.get("id_pago").isNumber()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                sendError(resp, "El campo id_pago es obligatorio para actualizar");
                return;
            }

            Integer idPago = jsonNode.get("id_pago").asInt();

            Pagos existente = pagosService.findPagosById(idPago);
            if (existente == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                sendError(resp, "Pago no encontrado");
                return;
            }

            // Actualización parcial
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

            // fecha_pago / fecha
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

            // factura opcional
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
            logger.severe("[" + CODIGO_LOG + "012] ERROR JSON doPut Pagos: " + ex.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            sendError(resp, "Error en el formato JSON: " + ex.getMessage());
        } catch (Exception ex) {
            logger.severe("[" + CODIGO_LOG + "013] ERROR doPut Pagos: " + ex.getMessage());
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

        logger.info("[" + CODIGO_LOG + "004] doDelete Pagos - inicio. id=" + req.getParameter("id"));

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

        } catch (NumberFormatException ex) {
            logger.severe("[" + CODIGO_LOG + "014] ERROR doDelete Pagos - ID inválido: " + ex.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            sendError(resp, "ID inválido: debe ser un número");
        } catch (Exception ex) {
            logger.severe("[" + CODIGO_LOG + "015] ERROR doDelete Pagos: " + ex.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            sendError(resp, ex.getMessage());
        }
    }

    // ============================================================
    // Helpers
    // ============================================================
    private String readBody(HttpServletRequest req) throws IOException {
        logger.fine("[" + CODIGO_LOG + "005] readBody Pagos - leyendo cuerpo de la petición");
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = req.getReader()) {
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
        }
        return sb.toString();
    }

    private void sendSuccess(HttpServletResponse resp, Object data) throws IOException {
        logger.fine("[" + CODIGO_LOG + "006] sendSuccess Pagos - enviando respuesta de éxito");
        resp.getWriter().write(JsonUtil.toJson(new Response(true, data)));
    }

    private void sendError(HttpServletResponse resp, String msg) throws IOException {
        logger.fine("[" + CODIGO_LOG + "007] sendError Pagos - enviando error: " + msg);
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
