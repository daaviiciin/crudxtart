package com.example.crudxtart.servlet;

import com.example.crudxtart.models.Producto;
import com.example.crudxtart.service.ProductoService;
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

@WebServlet("/productos")
public class ProductoServlet extends HttpServlet {

    @Inject
    private ProductoService productoService;

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

            // GET /productos
            if (idParam != null) {
                Integer id = Integer.parseInt(idParam);
                Producto prod = productoService.findProductoById(id);

                if (prod == null) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().write(gson.toJson(error("Producto no encontrado")));
                    return;
                }

                resp.getWriter().write(gson.toJson(success(prod)));
                return;
            }

            // GET /productos (todos)
            List<Producto> productos = productoService.findAllProductos();
            resp.getWriter().write(gson.toJson(success(productos)));

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
            throws IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            String body = readBody(req);
            Producto prod = gson.fromJson(body, Producto.class);

            Producto creado = productoService.createProducto(prod);

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
            Producto prod = gson.fromJson(body, Producto.class);

            if (prod.getId_producto() == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(
                        gson.toJson(error("El campo id_producto es obligatorio para actualizar"))
                );
                return;
            }

            Producto actualizado = productoService.updateProducto(prod);

            resp.getWriter().write(
                    gson.toJson(success(actualizado)));

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
            productoService.deleteProducto(id);

            resp.getWriter().write(
                    gson.toJson(success("Producto eliminado correctamente"))
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
