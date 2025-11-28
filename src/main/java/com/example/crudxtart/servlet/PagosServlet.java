package com.example.crudxtart.servlet;

import com.example.crudxtart.models.Pagos;
import com.example.crudxtart.service.PagosService;
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

@WebServlet("/pagos")
public class PagosServlet extends HttpServlet {

    @Inject
    private PagosService pagosService;

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

            // GET /pagos?id=10
            if (idParam != null) {
                Integer id = Integer.parseInt(idParam);
                Pagos pag = pagosService.findPagosById(id);

                if (pag == null) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().write(
                            gson.toJson(error("Pago no encontrado"))
                    );
                    return;
                }

                resp.getWriter().write(gson.toJson(success(pag)));
                return;
            }

            // GET /pagos (todos)
            List<Pagos> lista = pagosService.findAllPagos();
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
            throws ServletException, IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            String body = readBody(req);
            Pagos pag = gson.fromJson(body, Pagos.class);

            Pagos creado = pagosService.createPagos(pag);

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
            Pagos pag = gson.fromJson(body, Pagos.class);

            // Validar parámetro id_pago
            if (req.getParameter("id_pago") == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(
                        gson.toJson(error("El campo id_pago es obligatorio para actualizar"))
                );
                return;
            }

            // Parsear id_pago y asignarlo al objeto
            int id_pago = Integer.parseInt(req.getParameter("id_pago"));
            pag.setId_pago(id_pago);

            // Llamada correcta al servicio
            Pagos actualizado = pagosService.upLocalDatePagos(pag);

            resp.getWriter().write(gson.toJson(success(actualizado)));

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
            pagosService.deletePagos(id);

            resp.getWriter().write(
                    gson.toJson(success("Pago eliminado correctamente"))
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
