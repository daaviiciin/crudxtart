package com.example.crudxtart.servlet;

import com.example.crudxtart.models.Presupuestos;
import com.example.crudxtart.service.PresupuestosService;
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

@WebServlet("/presupuestos")
public class PresupuestosServlet extends HttpServlet {

    @Inject
    private PresupuestosService presupuestosService;

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

            // GET /presupuestos?id=7
            if (idParam != null) {
                Integer id = Integer.parseInt(idParam);
                Presupuestos prep = presupuestosService.findPresupuestoById(id);

                if (prep == null) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().write(
                            gson.toJson(error("Presupuesto no encontrado"))
                    );
                    return;
                }

                resp.getWriter().write(
                        gson.toJson(success(prep)));
                return;
            }

            // GET /presupuestos (todos)
            List<Presupuestos> lista = presupuestosService.findAllPresupuestos();

            resp.getWriter().write(
                    gson.toJson(success(lista)));

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
            throws IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            String body = readBody(req);
            Presupuestos prep = gson.fromJson(body, Presupuestos.class);

            Presupuestos creado = presupuestosService.createPresupuesto(prep);

            resp.getWriter().write(gson.toJson(success(creado)));

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
            throws IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            String body = readBody(req);
            Presupuestos prep = gson.fromJson(body, Presupuestos.class);

            if (prep.getId_Presupuesto() == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(
                        gson.toJson(error("El campo id_presupuesto es obligatorio para actualizar"))
                );
                return;
            }

            Presupuestos actualizado = presupuestosService.updatePresupuesto(prep);

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
            presupuestosService.deletePresupuesto(id);

            resp.getWriter().write(
                    gson.toJson(success("Presupuesto eliminado correctamente"))
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
