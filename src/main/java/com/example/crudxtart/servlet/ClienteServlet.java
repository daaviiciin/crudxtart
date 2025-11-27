package com.example.crudxtart.servlet;

import com.example.crudxtart.models.Cliente;
import com.example.crudxtart.service.ClienteService;
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

@WebServlet("/clientes")
public class ClienteServlet extends HttpServlet {

    @Inject
    private ClienteService clienteService;

    private final Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd")
            .create();

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

            // GET /clientes?id=5
            if (idParam != null) {
                Integer id = Integer.parseInt(idParam);
                Cliente cli = clienteService.findClienteById(id);

                if (cli == null) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().write(
                            gson.toJson(error("Cliente no encontrado"))
                    );
                    return;
                }

                resp.getWriter().write(
                        gson.toJson(success(cli))
                );
                return;
            }

            // GET /clientes (todos)
            List<Cliente> clientes = clienteService.findAllClientes();

            resp.getWriter().write(
                    gson.toJson(success(clientes))
            );

        } catch (Exception ex) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(
                    gson.toJson(error(ex.getMessage()))
            );
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
            Cliente cli = gson.fromJson(body, Cliente.class);

            Cliente creado = clienteService.createCliente(cli);

            resp.getWriter().write(
                    gson.toJson(success(creado))
            );

        } catch (Exception ex) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(
                    gson.toJson(error(ex.getMessage()))
            );
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
            Cliente cli = gson.fromJson(body, Cliente.class);

            if (cli.getId_cliente() == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(
                        gson.toJson(error("El campo id_cliente es obligatorio para actualizar"))
                );
                return;
            }

            Cliente actualizado = clienteService.upLocalDateCliente(cli);

            resp.getWriter().write(
                    gson.toJson(success(actualizado))
            );

        } catch (Exception ex) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(
                    gson.toJson(error(ex.getMessage()))
            );
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

        String idParam = req.getParameter("id");

        try {
            if (idParam == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(
                        gson.toJson(error("Debe proporcionar ?id= para eliminar"))
                );
                return;
            }

            Integer id = Integer.parseInt(idParam);
            clienteService.deleteCliente(id);

            resp.getWriter().write(
                    gson.toJson(success("Cliente eliminado correctamente"))
            );

        } catch (Exception ex) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(
                    gson.toJson(error(ex.getMessage()))
            );
        }
    }

    // ============================================================
    // Helpers internos
    // ============================================================
    private String readBody(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = req.getReader();
        String line;

        while ((line = reader.readLine()) != null) {
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
