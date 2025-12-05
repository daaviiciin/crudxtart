package com.example.crudxtart.controller;

import com.example.crudxtart.models.FacturaProducto;
import com.example.crudxtart.service.FacturaProductoService;
import com.example.crudxtart.utils.JsonUtil;
import jakarta.inject.Inject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Logger; // CAMBIO LOG
import java.util.List;

@WebServlet("/factura_productos")
public class FacturaProductoServlet extends HttpServlet {


    // 1º Cambio para el log de errores
    private static final Logger logger = Logger.getLogger(FacturaProductoServlet.class.getName());
    private static final String CODIGO_LOG = "CTL-FPR-";

    @Inject
    private FacturaProductoService facturaProductoService;


    // ============================================================
    // GET (todos o por id)
    // ============================================================
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        logger.info("[" + CODIGO_LOG + "001] doGet - inicio"); // CAMBIO LOG
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            String idParam = req.getParameter("id");

            // GET /factura_productos?id=x
            if (idParam != null) {
                Integer id = Integer.parseInt(idParam);
                FacturaProducto fp = facturaProductoService.findFacturaProductoById(id);

                if (fp == null) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    sendError(resp, "FacturaProducto no encontrado");
                    return;
                }

                sendSuccess(resp, fp);
                return;
            }

            // GET /factura_productos
            List<FacturaProducto> lista = facturaProductoService.findAllFacturaProductos();
            sendSuccess(resp, lista);

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

        logger.info("[" + CODIGO_LOG + "002] doPost - inicio"); // CAMBIO LOG
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            FacturaProducto fp = JsonUtil.fromJson(readBody(req), FacturaProducto.class);

            FacturaProducto creado = facturaProductoService.createFacturaProducto(fp);
            sendSuccess(resp, creado);

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

        logger.info("[" + CODIGO_LOG + "003] doPut - inicio"); // CAMBIO LOG
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            FacturaProducto fp = JsonUtil.fromJson(readBody(req), FacturaProducto.class);

            if (fp.getId_factura_producto() == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                sendError(resp, "El campo id_factura_producto es obligatorio para actualizar");
                return;
            }

            FacturaProducto actualizado = facturaProductoService.upLocalDateFacturaProducto(fp);
            sendSuccess(resp, actualizado);

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

        logger.info("[" + CODIGO_LOG + "004] doDelete - inicio"); // CAMBIO LOG
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
            facturaProductoService.deleteFacturaProducto(id);

            sendSuccess(resp, "FacturaProducto eliminado correctamente");

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

    private void sendSuccess(HttpServletResponse resp, Object data) throws IOException {
        resp.getWriter().write(JsonUtil.toJson(new Response(true, data)));
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
