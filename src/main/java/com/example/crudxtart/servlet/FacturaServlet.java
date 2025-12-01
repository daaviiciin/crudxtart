package com.example.crudxtart.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

import com.example.crudxtart.models.Factura;
import com.example.crudxtart.models.Empleado;
import com.example.crudxtart.models.Cliente;
import com.example.crudxtart.service.FacturaService;
import com.example.crudxtart.service.EmpleadoService;
import com.example.crudxtart.service.ClienteService;
import com.example.crudxtart.utils.JsonUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.inject.Inject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/facturas")
public class FacturaServlet extends HttpServlet {

    @Inject
    private FacturaService facturaService;

    @Inject
    private EmpleadoService empleadoService;

    @Inject
    private ClienteService clienteService;


    // ============================================================
    // GET (todos o por id)
    // ============================================================
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            String idParam = req.getParameter("id");

            // GET /facturas?id=x
            if (idParam != null) {
                Integer id = Integer.parseInt(idParam);
                Factura fac = facturaService.findFacturaById(id);

                if (fac == null) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    sendError(resp, "Factura no encontrada");
                    return;
                }

                sendSuccess(resp, fac);
                return;
            }

            // GET /facturas
            List<Factura> lista = facturaService.findAllFacturas();
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

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            String body = readBody(req);
            
            // Parsear JSON para obtener campos
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(body);
            
            // Crear nuevo objeto Factura
            Factura fac = new Factura();
            fac.setId_factura(null); // Asegurar que el ID sea null para crear nuevo registro
            
            // Extraer y validar campos básicos
            if (jsonNode.has("num_factura") && jsonNode.get("num_factura").isTextual()) {
                fac.setNum_factura(jsonNode.get("num_factura").asText());
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                sendError(resp, "El campo 'num_factura' es obligatorio");
                return;
            }
            
            if (jsonNode.has("total") && jsonNode.get("total").isNumber()) {
                fac.setTotal(jsonNode.get("total").asDouble());
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                sendError(resp, "El campo 'total' es obligatorio y debe ser un número");
                return;
            }
            
            if (jsonNode.has("estado") && jsonNode.get("estado").isTextual()) {
                fac.setEstado(jsonNode.get("estado").asText());
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                sendError(resp, "El campo 'estado' es obligatorio");
                return;
            }
            
            if (jsonNode.has("notas") && jsonNode.get("notas").isTextual()) {
                fac.setNotas(jsonNode.get("notas").asText());
            } else {
                fac.setNotas(""); // Valor por defecto si no viene
            }
            
            // Manejar fecha_emision (o fecha)
            if (jsonNode.has("fecha_emision") && jsonNode.get("fecha_emision").isTextual()) {
                try {
                    fac.setFecha_emision(java.time.LocalDate.parse(jsonNode.get("fecha_emision").asText()));
                } catch (Exception e) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    sendError(resp, "Formato de fecha_emision inválido. Use formato YYYY-MM-DD");
                    return;
                }
            } else if (jsonNode.has("fecha") && jsonNode.get("fecha").isTextual()) {
                try {
                    fac.setFecha_emision(java.time.LocalDate.parse(jsonNode.get("fecha").asText()));
                } catch (Exception e) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    sendError(resp, "Formato de fecha inválido. Use formato YYYY-MM-DD");
                    return;
                }
            } else {
                // Si no viene fecha, establecerla automáticamente
                fac.setFecha_emision(java.time.LocalDate.now());
            }
            
            // Manejar cliente_pagador (obligatorio)
            if (jsonNode.has("id_cliente") && jsonNode.get("id_cliente").isNumber()) {
                Integer clienteId = jsonNode.get("id_cliente").asInt();
                Cliente cli = clienteService.findClienteById(clienteId);
                if (cli != null) {
                    fac.setCliente_pagador(cli);
                } else {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    sendError(resp, "Cliente no encontrado con id: " + clienteId);
                    return;
                }
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                sendError(resp, "El campo 'id_cliente' es obligatorio");
                return;
            }
            
            // Manejar empleado (obligatorio)
            if (jsonNode.has("id_empleado") && jsonNode.get("id_empleado").isNumber()) {
                Integer empleadoId = jsonNode.get("id_empleado").asInt();
                Empleado emp = empleadoService.findEmpleadoById(empleadoId);
                if (emp != null) {
                    fac.setEmpleado(emp);
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

            Factura creada = facturaService.createFactura(fac);
            
            // Verificar que el ID se generó correctamente
            if (creada.getId_factura() == null) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                sendError(resp, "Error: No se pudo generar el ID de la factura");
                return;
            }
            
            sendSuccess(resp, creada);

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

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            String body = readBody(req);
            
            // Parsear JSON para verificar qué campos están presentes
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(body);

            // Verificar que id_factura esté presente
            if (!jsonNode.has("id_factura") || !jsonNode.get("id_factura").isNumber()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                sendError(resp, "El campo id_factura es obligatorio para actualizar");
                return;
            }

            Integer idFactura = jsonNode.get("id_factura").asInt();

            // Cargar la factura existente para preservar campos no enviados
            Factura existente = facturaService.findFacturaById(idFactura);
            if (existente == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                sendError(resp, "Factura no encontrada");
                return;
            }
            
            // Actualizar solo los campos enviados (actualización parcial)
            if (jsonNode.has("num_factura") && jsonNode.get("num_factura").isTextual()) {
                existente.setNum_factura(jsonNode.get("num_factura").asText());
            }
            
            if (jsonNode.has("total") && jsonNode.get("total").isNumber()) {
                double nuevoTotal = jsonNode.get("total").asDouble();
                if (nuevoTotal > 0) {
                    existente.setTotal(nuevoTotal);
                }
            }
            
            if (jsonNode.has("estado") && jsonNode.get("estado").isTextual()) {
                String nuevoEstado = jsonNode.get("estado").asText();
                if (nuevoEstado != null && !nuevoEstado.trim().isEmpty()) {
                    existente.setEstado(nuevoEstado);
                }
            }
            
            if (jsonNode.has("notas") && jsonNode.get("notas").isTextual()) {
                existente.setNotas(jsonNode.get("notas").asText());
            }
            
            if (jsonNode.has("fecha_emision") && jsonNode.get("fecha_emision").isTextual()) {
                try {
                    String fechaStr = jsonNode.get("fecha_emision").asText();
                    if (fechaStr != null && !fechaStr.trim().isEmpty()) {
                        existente.setFecha_emision(java.time.LocalDate.parse(fechaStr));
                    }
                } catch (Exception e) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    sendError(resp, "Formato de fecha_emision inválido. Use formato YYYY-MM-DD");
                    return;
                }
            } else if (jsonNode.has("fecha") && jsonNode.get("fecha").isTextual()) {
                try {
                    String fechaStr = jsonNode.get("fecha").asText();
                    if (fechaStr != null && !fechaStr.trim().isEmpty()) {
                        existente.setFecha_emision(java.time.LocalDate.parse(fechaStr));
                    }
                } catch (Exception e) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    sendError(resp, "Formato de fecha inválido. Use formato YYYY-MM-DD");
                    return;
                }
            }
            
            // Manejar relaciones si vienen en el JSON
            if (jsonNode.has("id_cliente") && jsonNode.get("id_cliente").isNumber()) {
                Integer clienteId = jsonNode.get("id_cliente").asInt();
                Cliente cli = clienteService.findClienteById(clienteId);
                if (cli != null) {
                    existente.setCliente_pagador(cli);
                } else {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    sendError(resp, "Cliente no encontrado con id: " + clienteId);
                    return;
                }
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

            Factura actualizada = facturaService.updateFactura(existente);
            sendSuccess(resp, actualizada);

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

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            String idParam = req.getParameter("id");

            if (idParam == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                sendError(resp, "Debe proporcionar ?id= para eliminar");
                return;
            }

            int id = Integer.parseInt(idParam);
            facturaService.deleteFactura(id);

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
