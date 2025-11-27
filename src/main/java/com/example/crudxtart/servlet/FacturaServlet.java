package com.example.crudxtart.servlet;

import com.example.crudxtart.models.Factura;
import com.example.crudxtart.service.FacturaService;
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

@WebServlet("/facturas")
public class FacturaServlet extends HttpServlet {

    @Inject
    private FacturaService facturaService;

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

            // GET /facturas?id=10
            if (idParam != null) {
                Integer id = Integer.parseInt(idParam);
                Factura fac = facturaService.findFacturaById(id);

                if (fac == null) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().write(
                            gson.toJson(error("Factura no encontrada"))
                    );
                    return;
                }

                resp.getWriter().write(gson.toJson(success(fac)));
                return;
            }

            // GET /facturas (todas)
            List<Factura> lista = facturaService.findAllFacturas();
            resp.getWriter().write(gson.toJson(success(lista)));

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
            Factura fac = gson.fromJson(body, Factura.class);

            Factura creada = facturaService.createFactura(fac);

            resp.getWriter().write(
                    gson.toJson(success(creada))
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
            throws IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            String body = readBody(req);
            Factura fac = gson.fromJson(body, Factura.class);

            if (fac.getId_factura() == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(
                        gson.toJson(error("El campo id_factura es obligatorio para actualizar"))
                );
                return;
            }

            Factura actualizada = facturaService.updateFactura(fac);

            resp.getWriter().write(
                    gson.toJson(success(actualizada))
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

            int id = Integer.parseInt(idParam);
            facturaService.deleteFactura(id);

            resp.getWriter().write(
                    gson.toJson(success("Factura eliminada correctamente"))
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
