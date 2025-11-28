package com.example.crudxtart.servlet;

import com.example.crudxtart.models.Empleado;
import com.example.crudxtart.models.Roles_empleado;
import com.example.crudxtart.service.EmpleadoService;
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
import java.time.LocalDate;
import java.util.List;

@WebServlet("/empleados")
public class EmpleadoServlet extends HttpServlet {

    @Inject
    private EmpleadoService empleadoService;

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

            // GET /empleados
            if (idParam != null) {
                Integer id = Integer.parseInt(idParam);
                Empleado emp = empleadoService.findEmpleadoById(id);

                if (emp == null) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().write(gson.toJson(error("Empleado no encontrado")));
                    return;
                }

                resp.getWriter().write(gson.toJson(success(emp)));
                return;
            }

            // GET /empleados (todos)
            List<Empleado> empleados = empleadoService.findAllEmpleados();
            resp.getWriter().write(gson.toJson(success(empleados)));

        } catch (Exception ex) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(error(ex.getMessage())));
        }
    }

    // ============================================================
    // POST (crear)
    // ============================================================
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            String body = readBody(req);
            Empleado emp = gson.fromJson(body, Empleado.class);

            if (emp.getFecha_ingreso() == null) {
                emp.setFecha_ingreso(LocalDate.now());
            }

            Empleado creado = empleadoService.createEmpleado(emp);

            resp.getWriter().write(gson.toJson(success(creado)));

        } catch (Exception ex) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(error(ex.getMessage())));
        }
    }

    // ============================================================
    // PUT (actualizar)
    // ============================================================
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            String body = readBody(req);
            Empleado emp = gson.fromJson(body, Empleado.class);

            if (emp.getId_empleado() == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(gson.toJson(error("El campo id_empleado es obligatorio para actualizar")));
                return;
            }

            Empleado actualizado = empleadoService.upLocalDateEmpleado(emp);

            resp.getWriter().write(gson.toJson(success(actualizado)));

        } catch (Exception ex) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(error(ex.getMessage())));
        }
    }

    // ============================================================
    // DELETE (eliminar)
    // ============================================================
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            String idParam = req.getParameter("id");

            if (idParam == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(gson.toJson(error("Debe proporcionar ?id= para eliminar")));
                return;
            }

            Integer id = Integer.parseInt(idParam);
            empleadoService.deleteEmpleado(id);

            resp.getWriter().write(gson.toJson(success("Empleado eliminado correctamente")));

        } catch (Exception ex) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(error(ex.getMessage())));
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

    private Object success(Object dataObj) {
        return new ResponseWrapper(true, dataObj);
    }

    private Object error(String msg) {
        return new ResponseWrapper(false, new ErrorWrapper(msg));
    }

    private static class ResponseWrapper {
        final boolean success;
        final Object data;

        ResponseWrapper(boolean success, Object data) {
            this.success = success;
            this.data = data;
        }
    }

    private static class ErrorWrapper {
        final String error;

        ErrorWrapper(String error) {
            this.error = error;
        }
    }

}
