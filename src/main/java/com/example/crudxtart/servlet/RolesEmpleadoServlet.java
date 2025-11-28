package com.example.crudxtart.servlet;

import com.example.crudxtart.models.Roles_empleado;
import com.example.crudxtart.service.Roles_empleadoService;
import com.example.crudxtart.utils.JsonUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
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

    private final Gson gson = JsonUtil.gson;

    // ============================================================
    // GET (todos o por id)
    // ============================================================
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            String idParam = req.getParameter("id");

            // GET /roles
            if (idParam != null) {
                Integer id = Integer.parseInt(idParam);
                Roles_empleado r = rolesService.findRolById(id);

                if (r == null) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().write(gson.toJson(error("Rol no encontrado")));
                    return;
                }

                resp.getWriter().write(gson.toJson(success(r)));
                return;
            }

            // GET /roles (todos)
            List<Roles_empleado> roles = rolesService.findAllRoles_empleado();

            resp.getWriter().write(gson.toJson(success(roles)));

        } catch (Exception ex) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(error(ex.getMessage())));
        }
    }

    // ============================================================
    // POST (crear rol)
    // ============================================================
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            String body = readBody(req);
            Roles_empleado rol = gson.fromJson(body, Roles_empleado.class);

            Roles_empleado created = rolesService.createRol(rol);

            resp.getWriter().write(gson.toJson(success(created)));

        } catch (Exception ex) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(
                    gson.toJson(error(ex.getMessage())));
        }
    }

    // ============================================================
    // PUT (actualizar rol)
    // ============================================================
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            String body = readBody(req);
            Roles_empleado rol = gson.fromJson(body, Roles_empleado.class);

            if (rol.getId_rol() == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(
                        gson.toJson(error("El campo id_rol es obligatorio para actualizar"))
                );
                return;
            }

            Roles_empleado updated = rolesService.updateRol(rol);

            resp.getWriter().write(
                    gson.toJson(success(updated)));

        } catch (Exception ex) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(
                    gson.toJson(error(ex.getMessage())));
        }
    }

    // ============================================================
    // DELETE (eliminar rol)
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
                resp.getWriter().write(
                        gson.toJson(error("Debe proporcionar ?id= para eliminar"))
                );
                return;
            }

            Integer id = Integer.parseInt(idParam);
            rolesService.deleteRol(id);

            resp.getWriter().write(
                    gson.toJson(success("Rol eliminado correctamente"))
            );

        } catch (Exception ex) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(
                    gson.toJson(error(ex.getMessage()))
            );
        }
    }

    // ============================================================
    // Helpers
    // ============================================================
    private String readBody(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = req.getReader();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    private Object success(Object data) {
        return new Object() {
            final boolean success = true;
            final Object dataObj = data;
        };
    }

    private Object error(String message) {
        return new Object() {
            final boolean success = false;
            final String error = message;
        };
    }
}
