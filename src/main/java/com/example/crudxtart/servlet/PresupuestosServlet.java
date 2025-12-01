package com.example.crudxtart.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

import com.example.crudxtart.models.Presupuestos;
import com.example.crudxtart.models.Empleado;
import com.example.crudxtart.models.Cliente;
import com.example.crudxtart.models.Producto;
import com.example.crudxtart.service.PresupuestosService;
import com.example.crudxtart.service.EmpleadoService;
import com.example.crudxtart.service.ClienteService;
import com.example.crudxtart.service.ProductoService;
import com.example.crudxtart.utils.JsonUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.inject.Inject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/presupuestos")
public class PresupuestosServlet extends HttpServlet {

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
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            sendError(resp, "ID inválido: debe ser un número");
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
            
            // Crear nuevo objeto Presupuestos
            Presupuestos prep = new Presupuestos();
            prep.setId_Presupuesto(null); // Asegurar que el ID sea null para crear nuevo registro
            
            // Extraer y validar campos básicos
            if (jsonNode.has("presupuesto") && jsonNode.get("presupuesto").isNumber()) {
                prep.setPresupuesto(jsonNode.get("presupuesto").asDouble());
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                sendError(resp, "El campo 'presupuesto' es obligatorio y debe ser un número");
                return;
            }
            
            if (jsonNode.has("estado") && jsonNode.get("estado").isTextual()) {
                prep.setEstado(jsonNode.get("estado").asText());
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                sendError(resp, "El campo 'estado' es obligatorio");
                return;
            }
            
            // Manejar fecha_apertura
            if (jsonNode.has("fecha_apertura") && jsonNode.get("fecha_apertura").isTextual()) {
                try {
                    prep.setFecha_apertura(java.time.LocalDate.parse(jsonNode.get("fecha_apertura").asText()));
                } catch (Exception e) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    sendError(resp, "Formato de fecha_apertura inválido. Use formato YYYY-MM-DD");
                    return;
                }
            } else {
                // Si no viene fecha_apertura, establecerla automáticamente
                prep.setFecha_apertura(java.time.LocalDate.now());
            }
            
            // Manejar fecha_cierre (opcional)
            if (jsonNode.has("fecha_cierre") && jsonNode.get("fecha_cierre").isTextual() && !jsonNode.get("fecha_cierre").asText().isEmpty()) {
                try {
                    prep.setFecha_cierre(java.time.LocalDate.parse(jsonNode.get("fecha_cierre").asText()));
                } catch (Exception e) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    sendError(resp, "Formato de fecha_cierre inválido. Use formato YYYY-MM-DD");
                    return;
                }
            }
            
            // Manejar empleado (obligatorio)
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
            
            // Manejar cliente_pagador (obligatorio)
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
            
            // Manejar cliente_beneficiario (obligatorio)
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
            
            // Manejar producto (obligatorio)
            if (jsonNode.has("id_producto") && jsonNode.get("id_producto").isNumber()) {
                Integer productoId = jsonNode.get("id_producto").asInt();
                Producto prod = productoService.findProductoById(productoId);
                if (prod != null) {
                    prep.setProducto(prod);
                } else {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    sendError(resp, "Producto no encontrado con id: " + productoId);
                    return;
                }
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                sendError(resp, "El campo 'id_producto' es obligatorio");
                return;
            }

            Presupuestos creado = presupuestosService.createPresupuesto(prep);
            
            // Verificar que el ID se generó correctamente
            if (creado.getId_Presupuesto() == null) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                sendError(resp, "Error: No se pudo generar el ID del presupuesto");
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

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            String body = readBody(req);
            
            // Parsear JSON para verificar qué campos están presentes
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(body);

            // Verificar que id_Presupuesto esté presente
            if (!jsonNode.has("id_Presupuesto") || !jsonNode.get("id_Presupuesto").isNumber()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                sendError(resp, "El campo id_Presupuesto es obligatorio para actualizar");
                return;
            }

            Integer idPresupuesto = jsonNode.get("id_Presupuesto").asInt();

            // Cargar el presupuesto existente para preservar campos no enviados
            Presupuestos existente = presupuestosService.findPresupuestoById(idPresupuesto);
            if (existente == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                sendError(resp, "Presupuesto no encontrado");
                return;
            }
            
            // Actualizar solo los campos enviados (actualización parcial)
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
            
            if (jsonNode.has("fecha_cierre") && jsonNode.get("fecha_cierre").isTextual()) {
                try {
                    String fechaStr = jsonNode.get("fecha_cierre").asText();
                    if (fechaStr != null && !fechaStr.trim().isEmpty()) {
                        existente.setFecha_cierre(java.time.LocalDate.parse(fechaStr));
                    } else {
                        // Si se envía fecha_cierre como string vacío, establecer a null
                        existente.setFecha_cierre(null);
                    }
                } catch (Exception e) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    sendError(resp, "Formato de fecha_cierre inválido. Use formato YYYY-MM-DD");
                    return;
                }
            }
            
            // Manejar relaciones si vienen en el JSON
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
            
            if (jsonNode.has("id_producto") && jsonNode.get("id_producto").isNumber()) {
                Integer productoId = jsonNode.get("id_producto").asInt();
                Producto prod = productoService.findProductoById(productoId);
                if (prod != null) {
                    existente.setProducto(prod);
                } else {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    sendError(resp, "Producto no encontrado con id: " + productoId);
                    return;
                }
            }

            Presupuestos actualizado = presupuestosService.updatePresupuesto(existente);
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
            
            // Verificar que el presupuesto existe antes de eliminar
            Presupuestos prep = presupuestosService.findPresupuestoById(id);
            if (prep == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                sendError(resp, "Presupuesto no encontrado");
                return;
            }
            
            presupuestosService.deletePresupuesto(id);

            // Devolver null en data para indicar éxito sin datos
            sendSuccess(resp, null);

        } catch (NumberFormatException ex) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            sendError(resp, "ID inválido: debe ser un número");
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
