package com.example.crudxtart.servlet;

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

@WebServlet("/roles")
public class RolesEmpleadoServlet extends HttpServlet {

    @Inject
    private Roles_empleadoService rolesService;


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

            // GET /roles?id=X
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

            // GET /roles (todos)
            List<Roles_empleado> roles = rolesService.findAllRoles_empleado();
            sendSuccess(resp, roles);

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
            Roles_empleado rol = JsonUtil.fromJson(readBody(req), Roles_empleado.class);

            Roles_empleado creado = rolesService.createRol(rol);
            sendSuccess(resp, creado);

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
            Roles_empleado rol = JsonUtil.fromJson(readBody(req), Roles_empleado.class);

            if (rol.getId_rol() == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                sendError(resp, "El campo id_rol es obligatorio para actualizar");
                return;
            }

            Roles_empleado actualizado = rolesService.updateRol(rol);
            sendSuccess(resp, actualizado);

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
            rolesService.deleteRol(id);

            sendSuccess(resp, "Rol eliminado correctamente");

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
