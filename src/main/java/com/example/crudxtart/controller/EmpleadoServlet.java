package com.example.crudxtart.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

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
            Empleado emp = JsonUtil.fromJson(body, Empleado.class);

            // Asegurar que el ID sea null para crear nuevo registro
            emp.setId_empleado(null);

            // Si no viene fecha_ingreso, establecerla automáticamente
            if (emp.getFecha_ingreso() == null) {
                emp.setFecha_ingreso(LocalDate.now());
            }

            // Si no viene estado, establecerlo a "activo" por defecto
            if (emp.getEstado() == null || emp.getEstado().trim().isEmpty()) {
                emp.setEstado("activo");
            }

            // Manejar id_rol si viene en el JSON
            // El frontend puede enviar: {"id_rol": {"id_rol": 1}}
            if (emp.getId_rol() != null && emp.getId_rol().getId_rol() != null) {
                // Cargar el rol desde la base de datos
                try {
                    com.example.crudxtart.models.Roles_empleado rol = 
                        rolesService.findRolById(emp.getId_rol().getId_rol());
                    if (rol != null) {
                        emp.setId_rol(rol);
                    } else {
                        // Si el rol no existe, establecer a null
                        emp.setId_rol(null);
                    }
                } catch (Exception ex) {
                    // Si no se puede cargar el rol, establecer a null
                    emp.setId_rol(null);
                }
            } else {
                emp.setId_rol(null);
            }

            Empleado creado = empleadoService.createEmpleado(emp);
            
            // Verificar que el ID se generó correctamente
            if (creado.getId_empleado() == null) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                sendError(resp, "Error: No se pudo generar el ID del empleado");
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
            Empleado emp = JsonUtil.fromJson(body, Empleado.class);

            if (emp.getId_empleado() == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                sendError(resp, "El campo id_empleado es obligatorio para actualizar");
                return;
            }

            // Cargar el empleado existente para preservar campos no enviados
            Empleado existente = empleadoService.findEmpleadoById(emp.getId_empleado());
            if (existente == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                sendError(resp, "Empleado no encontrado");
                return;
            }
            
            // Actualizar solo los campos enviados (si vienen null, mantener los existentes)
            if (emp.getNombre() != null) {
                existente.setNombre(emp.getNombre());
            }
            if (emp.getEmail() != null) {
                existente.setEmail(emp.getEmail());
            }
            if (emp.getTelefono() != null) {
                existente.setTelefono(emp.getTelefono());
            }
            if (emp.getPassword() != null) {
                existente.setPassword(emp.getPassword());
            }
            if (emp.getFecha_ingreso() != null) {
                existente.setFecha_ingreso(emp.getFecha_ingreso());
            }
            if (emp.getEstado() != null) {
                existente.setEstado(emp.getEstado());
            }
            // Manejar id_rol si viene en el JSON
            if (emp.getId_rol() != null && emp.getId_rol().getId_rol() != null) {
                // Cargar el rol desde la base de datos
                com.example.crudxtart.models.Roles_empleado rol = 
                    rolesService.findRolById(emp.getId_rol().getId_rol());
                if (rol != null) {
                    existente.setId_rol(rol);
                }
            }

            Empleado actualizado = empleadoService.upLocalDateEmpleado(existente);
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
            
            // Verificar que el empleado existe antes de eliminar
            Empleado emp = empleadoService.findEmpleadoById(id);
            if (emp == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                sendError(resp, "Empleado no encontrado");
                return;
            }
            
            empleadoService.deleteEmpleado(id);

            // Devolver null en data para indicar éxito sin datos
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

    private void sendSuccess(HttpServletResponse resp, Object dataObj) throws IOException {
        resp.getWriter().write(JsonUtil.toJson(new Response(true, dataObj)));
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
