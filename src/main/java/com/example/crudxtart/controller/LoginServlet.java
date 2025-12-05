package com.example.crudxtart.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Logger;

import com.example.crudxtart.models.Empleado;
import com.example.crudxtart.models.Cliente;
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

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    // ============================================================
    // LOG + CÓDIGO IDENTIFICADOR
    // ============================================================
    private static final Logger logger = Logger.getLogger(LoginServlet.class.getName());
    private static final String CODIGO_LOG = "CTL-LOG-";

    @Inject
    private EmpleadoService empleadoService;
    
    @Inject
    private ClienteService clienteService;

    // ============================================================
    // POST (LOGIN)
    // ============================================================
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        logger.info("[" + CODIGO_LOG + "001] doPost Login - inicio");
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            // Leer body JSON
            String body = readBody(req);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(body);

            // Validar campos
            String email = jsonNode.has("email") && jsonNode.get("email").isTextual()
                    ? jsonNode.get("email").asText()
                    : null;

            String password = jsonNode.has("password") && jsonNode.get("password").isTextual()
                    ? jsonNode.get("password").asText()
                    : null;

            if (email == null || password == null) {
                logger.severe("[" + CODIGO_LOG + "002] ERROR - Datos inválidos en login");
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                sendError(resp, "Debe enviar 'email' y 'password'");
                return;
            }

            logger.info("[" + CODIGO_LOG + "003] Validando credenciales de: " + email);

            // Buscar primero como empleado
            Empleado empleado = empleadoService.findEmpleadoByEmail(email);
            
            if (empleado != null) {
                // Validar password de empleado
                if (!empleado.getPassword().equals(password)) {
                    logger.severe("[" + CODIGO_LOG + "005] ERROR - Password incorrecta");
                    resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    sendError(resp, "Contraseña incorrecta");
                    return;
                }
                
                logger.info("[" + CODIGO_LOG + "006] Login exitoso (empleado) para: " + email);
                sendSuccess(resp, empleado);
                return;
            }
            
            // Si no es empleado, buscar como cliente
            Cliente cliente = clienteService.findClienteByEmail(email);
            
            if (cliente == null) {
                logger.severe("[" + CODIGO_LOG + "004] ERROR - Usuario no encontrado");
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                sendError(resp, "Usuario no encontrado");
                return;
            }
            
            // Validar password de cliente
            if (!cliente.getPassword().equals(password)) {
                logger.severe("[" + CODIGO_LOG + "005] ERROR - Password incorrecta");
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                sendError(resp, "Contraseña incorrecta");
                return;
            }
            
            logger.info("[" + CODIGO_LOG + "006] Login exitoso (cliente) para: " + email);
            sendSuccess(resp, cliente);

        } catch (com.fasterxml.jackson.core.JsonProcessingException ex) {
            logger.severe("[" + CODIGO_LOG + "007] ERROR JSON Login: " + ex.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            sendError(resp, "Error en el formato JSON");
        } catch (Exception ex) {
            logger.severe("[" + CODIGO_LOG + "008] ERROR general Login: " + ex.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            sendError(resp, ex.getMessage());
        }
    }

    // ============================================================
    // Helpers
    // ============================================================
    private String readBody(HttpServletRequest req) throws IOException {
        logger.fine("[" + CODIGO_LOG + "009] readBody - leyendo body petición");
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = req.getReader()) {
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
        }
        return sb.toString();
    }

    private void sendSuccess(HttpServletResponse resp, Object data) throws IOException {
        logger.fine("[" + CODIGO_LOG + "010] sendSuccess - login OK");
        resp.getWriter().write(JsonUtil.toJson(new Response(true, data)));
    }

    private void sendError(HttpServletResponse resp, String msg) throws IOException {
        logger.fine("[" + CODIGO_LOG + "011] sendError - " + msg);
        resp.getWriter().write(JsonUtil.toJson(new Response(false, new ErrorMsg(msg))));
    }

    // ============================================================
    // Objetos respuesta JSON
    // ============================================================
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
