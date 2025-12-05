package com.example.crudxtart.controller;

import com.example.crudxtart.models.Roles_empleado;
import com.example.crudxtart.service.Roles_empleadoService;
import com.example.crudxtart.utils.JsonUtil;
import jakarta.inject.Inject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

@WebServlet("/roles_empleado")
public class RolesEmpleadoServlet extends HttpServlet {

    // LOGGING
    private static final Logger logger = Logger.getLogger(RolesEmpleadoServlet.class.getName());
    private static final String CODIGO_LOG = "CTL-ROL-";

    @Inject
    private Roles_empleadoService rolesService;

    // ============================================================
    // GET (todos o por id)
    // ============================================================
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        logger.info("[" + CODIGO_LOG + "001] doGet RolesEmpleado - inicio. id=" + req.getParameter("id"));

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            String idParam = req.getParameter("id");

            if (idParam != null) {
                Integer id = Integer.parseInt(idParam);
                Roles_empleado rol = rolesService.findRolById(id);

                if (rol == null) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    sendError(resp, "Rol no encontrado");
                    return;
                }

                sendSuccess(resp, rol);
                return;
            }

            List<Roles_empleado> lista = rolesService.findAllRoles_empleado();
            sendSuccess(resp, lista);

        } catch (NumberFormatException ex) {
            logger.severe("[" + CODIGO_LOG + "008] ERROR doGet RolesEmpleado - ID inválido: " + ex.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            sendError(resp, "ID inválido: debe ser un número");
        } catch (Exception ex) {
            logger.severe("[" + CODIGO_LOG + "009] ERROR doGet RolesEmpleado: " + ex.getMessage());
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

        logger.info("[" + CODIGO_LOG + "002] doPost RolesEmpleado - inicio");

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            Roles_empleado rol = JsonUtil.fromJson(readBody(req), Roles_empleado.class);

            rol.setId_rol(null);

            Roles_empleado creado = rolesService.createRol(rol);

            if (creado.getId_rol() == null) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                sendError(resp, "No se pudo generar el ID del rol");
                return;
            }

            sendSuccess(resp, creado);

        } catch (com.fasterxml.jackson.core.JsonProcessingException ex) {
            logger.severe("[" + CODIGO_LOG + "010] ERROR JSON doPost RolesEmpleado: " + ex.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            sendError(resp, "Error en el formato JSON: " + ex.getMessage());
        } catch (Exception ex) {
            logger.severe("[" + CODIGO_LOG + "011] ERROR doPost RolesEmpleado: " + ex.getMessage());
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

        logger.info("[" + CODIGO_LOG + "003] doPut RolesEmpleado - inicio");

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            Roles_empleado rol = JsonUtil.fromJson(readBody(req), Roles_empleado.class);

            if (rol.getId_rol() == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                sendError(resp, "El campo id_rol es obligatorio para actualizar");
                return;
            }

            Roles_empleado existente = rolesService.findRolById(rol.getId_rol());

            if (existente == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                sendError(resp, "Rol no encontrado");
                return;
            }

            if (rol.getNombre_rol() != null) existente.setNombre_rol(rol.getNombre_rol());

            Roles_empleado actualizado = rolesService.updateRol(existente);
            sendSuccess(resp, actualizado);

        } catch (com.fasterxml.jackson.core.JsonProcessingException ex) {
            logger.severe("[" + CODIGO_LOG + "012] ERROR JSON doPut RolesEmpleado: " + ex.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            sendError(resp, "Error en el formato JSON: " + ex.getMessage());
        } catch (Exception ex) {
            logger.severe("[" + CODIGO_LOG + "013] ERROR doPut RolesEmpleado: " + ex.getMessage());
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

        logger.info("[" + CODIGO_LOG + "004] doDelete RolesEmpleado - inicio. id=" + req.getParameter("id"));

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

            Roles_empleado rol = rolesService.findRolById(id);
            if (rol == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                sendError(resp, "Rol no encontrado");
                return;
            }

            rolesService.deleteRol(id);
            sendSuccess(resp, null);

        } catch (NumberFormatException ex) {
            logger.severe("[" + CODIGO_LOG + "014] ERROR doDelete RolesEmpleado - ID inválido: " + ex.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            sendError(resp, "ID inválido: debe ser un número");
        } catch (Exception ex) {
            logger.severe("[" + CODIGO_LOG + "015] ERROR doDelete RolesEmpleado: " + ex.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            sendError(resp, ex.getMessage());
        }
    }

    // ============================================================
    // Helpers
    // ============================================================
    private String readBody(HttpServletRequest req) throws IOException {
        logger.fine("[" + CODIGO_LOG + "005] readBody RolesEmpleado - leyendo cuerpo de la petición");
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = req.getReader()) {
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
        }
        return sb.toString();
    }

    private void sendSuccess(HttpServletResponse resp, Object data) throws IOException {
        logger.fine("[" + CODIGO_LOG + "006] sendSuccess RolesEmpleado - enviando respuesta de éxito");
        resp.getWriter().write(JsonUtil.toJson(new Response(true, data)));
    }

    private void sendError(HttpServletResponse resp, String msg) throws IOException {
        logger.fine("[" + CODIGO_LOG + "007] sendError RolesEmpleado - enviando error: " + msg);
        resp.getWriter().write(JsonUtil.toJson(new Response(false, new ErrorMsg(msg))));
    }

    private static class Response {
        final boolean success;
        final Object data;
        Response(boolean success, Object data) { this.success = success; this.data = data; }
    }

    private static class ErrorMsg {
        final String error;
        ErrorMsg(String error) { this.error = error; }
    }
}
