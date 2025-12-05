package com.example.crudxtart.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

import com.example.crudxtart.models.Empleado;
import com.example.crudxtart.service.EmpleadoService;
import com.example.crudxtart.service.Roles_empleadoService;
import com.example.crudxtart.utils.JsonUtil;

import jakarta.inject.Inject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/empleados")
public class EmpleadoServlet extends HttpServlet {

    // LOGGING
    private static final Logger logger = Logger.getLogger(EmpleadoServlet.class.getName());
    private static final String CODIGO_LOG = "CTL-EMP-";

    @Inject
    private EmpleadoService empleadoService;

    @Inject
    private Roles_empleadoService rolesService;

    // ============================================================
    // GET (todos o por id)
    // ============================================================
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        logger.info("[" + CODIGO_LOG + "001] doGet Empleados - inicio. id=" + req.getParameter("id"));

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            String idParam = req.getParameter("id");

            // GET /empleados?id=7
            if (idParam != null) {
                Integer id = Integer.parseInt(idParam);
                Empleado emp = empleadoService.findEmpleadoById(id);

                if (emp == null) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    sendError(resp, "Empleado no encontrado");
                    return;
                }

                sendSuccess(resp, emp);
                return;
            }

            // GET /empleados (todos)
            List<Empleado> empleados = empleadoService.findAllEmpleados();
            sendSuccess(resp, empleados);

        } catch (Exception ex) {
            logger.severe("[" + CODIGO_LOG + "008] ERROR doGet Empleados: " + ex.getMessage());
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

        logger.info("[" + CODIGO_LOG + "002] doPost Empleados - inicio");

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            String body = readBody(req);
            Empleado emp = JsonUtil.fromJson(body, Empleado.class);

            // Asegurar que el ID sea null para crear nuevo registro
            emp.setId_empleado(null);

            // Si no viene fecha_ingreso, establecerla automáticamente
            if (emp.getFecha_ingreso() == null) {
                emp.setFecha_ingreso(LocalDate.now());
            }

            // Rol: el JSON puede traer {"rol":{"id_rol":1}}
            if (emp.getId_rol() != null && emp.getId_rol().getId_rol() != null) {
                var rol = rolesService.findRolById(emp.getId_rol().getId_rol());
                if (rol != null) {
                    emp.setId_rol(rol);
                } else {
                    emp.setId_rol(null);
                }
            } else {
                emp.setId_rol(null);
            }

            Empleado creado = empleadoService.createEmpleado(emp);

            if (creado.getId_empleado() == null) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                sendError(resp, "No se pudo generar el ID del empleado");
                return;
            }

            sendSuccess(resp, creado);

        } catch (com.fasterxml.jackson.core.JsonProcessingException ex) {
            logger.severe("[" + CODIGO_LOG + "009] ERROR JSON doPost Empleados: " + ex.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            sendError(resp, "Error en el formato JSON: " + ex.getMessage());
        } catch (Exception ex) {
            logger.severe("[" + CODIGO_LOG + "010] ERROR doPost Empleados: " + ex.getMessage());
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

        logger.info("[" + CODIGO_LOG + "003] doPut Empleados - inicio");

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            String body = readBody(req);
            Empleado emp = JsonUtil.fromJson(body, Empleado.class);

            if (emp.getId_empleado() == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                sendError(resp, "El campo id_empleado es obligatorio para actualizar");
                return;
            }

            // No se actualiza el rol desde aquí
            emp.setId_rol(null);

            Empleado existente = empleadoService.findEmpleadoById(emp.getId_empleado());
            if (existente == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                sendError(resp, "Empleado no encontrado");
                return;
            }

            if (emp.getNombre() != null) existente.setNombre(emp.getNombre());
            if (emp.getEmail() != null) existente.setEmail(emp.getEmail());
            if (emp.getTelefono() != null) existente.setTelefono(emp.getTelefono());
            if (emp.getPassword() != null) existente.setPassword(emp.getPassword());
            if (emp.getEstado() != null) existente.setEstado(emp.getEstado());
            if (emp.getFecha_ingreso() != null) existente.setFecha_ingreso(emp.getFecha_ingreso());

            Empleado actualizado = empleadoService.upLocalDateEmpleado(existente);
            sendSuccess(resp, actualizado);

        } catch (com.fasterxml.jackson.core.JsonProcessingException ex) {
            logger.severe("[" + CODIGO_LOG + "011] ERROR JSON doPut Empleados: " + ex.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            sendError(resp, "Error en el formato JSON: " + ex.getMessage());
        } catch (Exception ex) {
            logger.severe("[" + CODIGO_LOG + "012] ERROR doPut Empleados: " + ex.getMessage());
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

        logger.info("[" + CODIGO_LOG + "004] doDelete Empleados - inicio. id=" + req.getParameter("id"));

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

            Empleado emp = empleadoService.findEmpleadoById(id);
            if (emp == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                sendError(resp, "Empleado no encontrado");
                return;
            }

            empleadoService.deleteEmpleado(id);
            sendSuccess(resp, null);

        } catch (NumberFormatException ex) {
            logger.severe("[" + CODIGO_LOG + "013] ERROR doDelete Empleados - ID inválido: " + ex.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            sendError(resp, "ID inválido: debe ser un número");
        } catch (Exception ex) {
            logger.severe("[" + CODIGO_LOG + "014] ERROR doDelete Empleados: " + ex.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            sendError(resp, ex.getMessage());
        }
    }

    // ============================================================
    // Helpers
    // ============================================================
    private String readBody(HttpServletRequest req) throws IOException {
        logger.fine("[" + CODIGO_LOG + "005] readBody Empleados - leyendo cuerpo de la petición");
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
        }
        return sb.toString();
    }

    private void sendSuccess(HttpServletResponse resp, Object dataObj) throws IOException {
        logger.fine("[" + CODIGO_LOG + "006] sendSuccess Empleados - enviando respuesta de éxito");
        resp.getWriter().write(JsonUtil.toJson(new Response(true, dataObj)));
    }

    private void sendError(HttpServletResponse resp, String msg) throws IOException {
        logger.fine("[" + CODIGO_LOG + "007] sendError Empleados - enviando error: " + msg);
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
