package com.example.crudxtart.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import com.example.crudxtart.models.Producto;
import com.example.crudxtart.service.ProductoService;
import com.example.crudxtart.utils.JsonUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.inject.Inject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/productos")
public class ProductoServlet extends HttpServlet {

    // ============================================================
    // LOGGING
    // ============================================================
    private static final Logger logger = Logger.getLogger(ProductoServlet.class.getName());
    private static final String CODIGO_LOG = "CTL-PRD-";

    @Inject
    private ProductoService productoService;

    // ============================================================
    // GET (todos o por id)
    // ============================================================
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        logger.info("[" + CODIGO_LOG + "001] doGet Productos - inicio. id=" + req.getParameter("id"));

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            String idParam = req.getParameter("id");

            // GET /productos?id=x
            if (idParam != null) {
                Integer id = Integer.parseInt(idParam);
                Producto prod = productoService.findProductoById(id);

                if (prod == null) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    sendError(resp, "Producto no encontrado");
                    return;
                }

                sendSuccess(resp, prod);
                return;
            }

            // GET /productos
            List<Producto> productos = productoService.findAllProductos();
            sendSuccess(resp, productos);

        } catch (NumberFormatException ex) {
            logger.severe("[" + CODIGO_LOG + "008] ERROR doGet Productos - ID inválido: " + ex.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            sendError(resp, "ID inválido: debe ser un número");
        } catch (Exception ex) {
            logger.severe("[" + CODIGO_LOG + "009] ERROR doGet Productos: " + ex.getMessage());
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

        logger.info("[" + CODIGO_LOG + "002] doPost Productos - inicio");

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            String body = readBody(req);
            Producto prod = JsonUtil.fromJson(body, Producto.class);

            // Asegurar que el ID sea null para crear nuevo registro
            prod.setId_producto(null);

            // Si no viene activo, establecerlo a true por defecto
            // (el campo boolean no puede ser null, pero podemos asegurarnos)

            Producto creado = productoService.createProducto(prod);

            // Verificar que el ID se generó correctamente
            if (creado.getId_producto() == null) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                sendError(resp, "Error: No se pudo generar el ID del producto");
                return;
            }

            sendSuccess(resp, creado);

        } catch (com.fasterxml.jackson.core.JsonProcessingException ex) {
            logger.severe("[" + CODIGO_LOG + "010] ERROR JSON doPost Productos: " + ex.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            sendError(resp, "Error en el formato JSON: " + ex.getMessage());
        } catch (Exception ex) {
            logger.severe("[" + CODIGO_LOG + "011] ERROR doPost Productos: " + ex.getMessage());
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

        logger.info("[" + CODIGO_LOG + "003] doPut Productos - inicio");

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            String body = readBody(req);

            // Parsear JSON para verificar qué campos están presentes
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(body);

            // Deserializar el producto
            Producto prod = JsonUtil.fromJson(body, Producto.class);

            if (prod.getId_producto() == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                sendError(resp, "El campo id_producto es obligatorio para actualizar");
                return;
            }

            // Cargar el producto existente
            Producto existente = productoService.findProductoById(prod.getId_producto());
            if (existente == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                sendError(resp, "Producto no encontrado");
                return;
            }

            // Actualizar solo los campos enviados (si vienen null, mantener los existentes)
            if (jsonNode.has("nombre") && prod.getNombre() != null) {
                existente.setNombre(prod.getNombre());
            }
            if (jsonNode.has("descripcion") && prod.getDescripcion() != null) {
                existente.setDescripcion(prod.getDescripcion());
            }
            if (jsonNode.has("categoria") && prod.getCategoria() != null) {
                existente.setCategoria(prod.getCategoria());
            }
            // Precio: solo actualizar si viene en el JSON y es >= 0
            if (jsonNode.has("precio") && prod.getPrecio() >= 0) {
                existente.setPrecio(prod.getPrecio());
            }
            // Activo: actualizar si viene en el JSON (verificar presencia explícita)
            if (jsonNode.has("activo")) {
                existente.setActivo(prod.isActivo());
            }

            Producto actualizado = productoService.updateProducto(existente);
            sendSuccess(resp, actualizado);

        } catch (com.fasterxml.jackson.core.JsonProcessingException ex) {
            logger.severe("[" + CODIGO_LOG + "012] ERROR JSON doPut Productos: " + ex.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            sendError(resp, "Error en el formato JSON: " + ex.getMessage());
        } catch (Exception ex) {
            logger.severe("[" + CODIGO_LOG + "013] ERROR doPut Productos: " + ex.getMessage());
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

        logger.info("[" + CODIGO_LOG + "004] doDelete Productos - inicio. id=" + req.getParameter("id"));

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

            // Verificar que el producto existe antes de eliminar
            Producto prod = productoService.findProductoById(id);
            if (prod == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                sendError(resp, "Producto no encontrado");
                return;
            }

            productoService.deleteProducto(id);

            // Devolver null en data para indicar éxito sin datos
            sendSuccess(resp, null);

        } catch (NumberFormatException ex) {
            logger.severe("[" + CODIGO_LOG + "014] ERROR doDelete Productos - ID inválido: " + ex.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            sendError(resp, "ID inválido: debe ser un número");
        } catch (Exception ex) {
            logger.severe("[" + CODIGO_LOG + "015] ERROR doDelete Productos: " + ex.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            sendError(resp, ex.getMessage());
        }
    }

    // ============================================================
    // Helpers
    // ============================================================
    private String readBody(HttpServletRequest req) throws IOException {
        logger.fine("[" + CODIGO_LOG + "005] readBody Productos - leyendo cuerpo de la petición");
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = req.getReader()) {
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
        }
        return sb.toString();
    }

    private void sendSuccess(HttpServletResponse resp, Object data) throws IOException {
        logger.fine("[" + CODIGO_LOG + "006] sendSuccess Productos - enviando respuesta de éxito");
        resp.getWriter().write(JsonUtil.toJson(new Response(true, data)));
    }

    private void sendError(HttpServletResponse resp, String msg) throws IOException {
        logger.fine("[" + CODIGO_LOG + "007] sendError Productos - enviando error: " + msg);
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
