package com.example.crudxtart.servlet;

import com.example.crudxtart.models.Empleado;
import com.example.crudxtart.models.Cliente;

import com.example.crudxtart.service.EmpleadoService;
import com.example.crudxtart.service.ClienteService;

import com.example.crudxtart.utils.JsonUtil;
import com.google.gson.*;

import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDate;

@WebServlet("/auth/login")
public class LoginServlet extends HttpServlet {

    @Inject
    private EmpleadoService empleadoService;

    @Inject
    private ClienteService clienteService;

    private final Gson gson = JsonUtil.gson;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json;charset=UTF-8");

        try {
            // Leer JSON
            String body = readBody(req);
            LoginRequest data = gson.fromJson(body, LoginRequest.class);

            if (data == null || data.email == null || data.password == null) {
                sendError(resp, "Email y password requeridos");
                return;
            }

            // ========================
            // LOGIN EMPLEADO
            // ========================
            Empleado emp = empleadoService.findEmpleadoByEmail(data.email);

            if (emp != null && emp.getPassword().equals(data.password)) {

                HttpSession session = req.getSession(true);
                session.setAttribute("userId", emp.getId_empleado());
                session.setAttribute("rol", emp.getId_rol().getNombre_rol());
                session.setAttribute("tipo", "empleado");

                sendSuccess(resp, new UserResponse(
                        emp.getId_empleado(),
                        emp.getNombre(),
                        emp.getEmail(),
                        emp.getId_rol().getNombre_rol(),
                        "empleado"
                ));
                return;
            }

            // ========================
            // LOGIN CLIENTE
            // ========================
            Cliente cli = clienteService.findClienteByEmail(data.email);

            if (cli != null && cli.getPassword().equals(data.password)) {

                HttpSession session = req.getSession(true);
                session.setAttribute("userId", cli.getId_cliente());
                session.setAttribute("rol", "cliente");
                session.setAttribute("tipo", "cliente");

                sendSuccess(resp, new UserResponse(
                        cli.getId_cliente(),
                        cli.getNombre(),
                        cli.getEmail(),
                        "cliente",
                        "cliente"
                ));
                return;
            }

            // Ningún usuario coincide
            sendError(resp, "Credenciales incorrectas");

        } catch (Exception ex) {
            sendError(resp, "Error: " + ex.getMessage());
        }
    }

    // =======================================================
    // Helpers
    // =======================================================

    private void sendSuccess(HttpServletResponse resp, Object dataObj) throws IOException {
        ResponseWrapper response = new ResponseWrapper(true, dataObj);
        resp.getWriter().write(gson.toJson(response));
    }

    private void sendError(HttpServletResponse resp, String msg) throws IOException {
        ResponseWrapper response = new ResponseWrapper(false, new ErrorWrapper(msg));
        resp.getWriter().write(gson.toJson(response));
    }

    private String readBody(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = req.getReader();
        String line;
        while ((line = br.readLine()) != null) sb.append(line);
        return sb.toString();
    }

    private static class LoginRequest {
        String email;
        String password;
    }

    // JSON limpio, estable, serializable
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

    private static class UserResponse {
        final int id;
        final String nombre;
        final String email;
        final String rol;
        final String tipo;

        UserResponse(int id, String nombre, String email, String rol, String tipo) {
            this.id = id;
            this.nombre = nombre;
            this.email = email;
            this.rol = rol;
            this.tipo = tipo;
        }
    }
}
