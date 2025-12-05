package com.example.crudxtart.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.List;

import com.example.crudxtart.models.FacturaProducto;
import com.example.crudxtart.models.Factura;
import com.example.crudxtart.models.Producto;
import com.example.crudxtart.models.Cliente;
import com.example.crudxtart.service.FacturaProductoService;
import com.example.crudxtart.service.FacturaService;
import com.example.crudxtart.service.ProductoService;
import com.example.crudxtart.service.ClienteService;
import com.example.crudxtart.utils.JsonUtil;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.inject.Inject;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/facturas-productos")
public class FacturaProductoServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(FacturaProductoServlet.class.getName());
    private static final String CODIGO_LOG = "CTL-FP-";

    @Inject
    private FacturaProductoService facturaProductoService;

    @Inject
    private FacturaService facturaService;

    @Inject
    private ProductoService productoService;

    @Inject
    private ClienteService clienteService;


    // ========================================================
    // GET
    // ========================================================
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        logger.info("[" + CODIGO_LOG + "001] doGet - inicio");

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            String idParam = req.getParameter("id");

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

            List<FacturaProducto> lista = facturaProductoService.findAllFacturaProductos();
            sendSuccess(resp, lista);

        } catch (Exception ex) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            sendError(resp, ex.getMessage());
        }
    }


    // ========================================================
    // POST
    // ========================================================
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        logger.info("[" + CODIGO_LOG + "002] doPost - inicio");

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            String body = readBody(req);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(body);

            // Crear FacturaProducto
            FacturaProducto fp = new FacturaProducto();
            fp.setId_factura_producto(null);

            // Validar id_factura
            if (!jsonNode.has("id_factura") || !jsonNode.get("id_factura").isNumber()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                sendError(resp, "Campo 'id_factura' obligatorio");
                return;
            }
            Integer idFactura = jsonNode.get("id_factura").asInt();
            Factura fac = facturaService.findFacturaById(idFactura);
            if (fac == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                sendError(resp, "Factura no encontrada");
                return;
            }
            fp.setFactura(fac);

            // Validar id_producto
            if (!jsonNode.has("id_producto") || !jsonNode.get("id_producto").isNumber()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                sendError(resp, "Campo 'id_producto' obligatorio");
                return;
            }
            Integer idProducto = jsonNode.get("id_producto").asInt();
            Producto prod = productoService.findProductoById(idProducto);
            if (prod == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                sendError(resp, "Producto no encontrado");
                return;
            }
            fp.setProducto(prod);

            // Validar cliente beneficiario
            Cliente cli = null;
            if (jsonNode.has("id_cliente_beneficiario") && jsonNode.get("id_cliente_beneficiario").isNumber()) {
                Integer idCliente = jsonNode.get("id_cliente_beneficiario").asInt();
                cli = clienteService.findClienteById(idCliente);
            }
            if (cli == null) cli = fac.getCliente_pagador();
            fp.setCliente_beneficiario(cli);

            // Cantidad
            int cantidad = jsonNode.has("cantidad") && jsonNode.get("cantidad").isNumber()
                    ? jsonNode.get("cantidad").asInt()
                    : 1;
            fp.setCantidad(cantidad);

            // Precio unitario
            double precioUnitario = jsonNode.has("precio_unitario") && jsonNode.get("precio_unitario").isNumber()
                    ? jsonNode.get("precio_unitario").asDouble()
                    : prod.getPrecio();
            fp.setPrecio_unitario(precioUnitario);

            // Subtotal
            double subtotal = jsonNode.has("subtotal") && jsonNode.get("subtotal").isNumber()
                    ? jsonNode.get("subtotal").asDouble()
                    : cantidad * precioUnitario;
            fp.setSubtotal(subtotal);

            FacturaProducto creado = facturaProductoService.createFacturaProducto(fp);
            sendSuccess(resp, creado);

        } catch (Exception ex) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            sendError(resp, ex.getMessage());
        }
    }


    // ========================================================
    // DELETE
    // ========================================================
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        logger.info("[" + CODIGO_LOG + "003] doDelete - inicio");

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            String idParam = req.getParameter("id");

            if (idParam == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                sendError(resp, "Debe proporcionar ?id=");
                return;
            }

            Integer id = Integer.parseInt(idParam);
            facturaProductoService.deleteFacturaProducto(id);

            sendSuccess(resp, null);

        } catch (Exception ex) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            sendError(resp, ex.getMessage());
        }
    }


    // ========================================================
    // Helpers
    // ========================================================
    private String readBody(HttpServletRequest req) throws IOException {
        logger.fine("[" + CODIGO_LOG + "005] readBody - leyendo cuerpo");
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = req.getReader()) {
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
        }
        return sb.toString();
    }

    private void sendSuccess(HttpServletResponse resp, Object data) throws IOException {
        logger.fine("[" + CODIGO_LOG + "006] sendSuccess");
        resp.getWriter().write(JsonUtil.toJson(new Response(true, data)));
    }

    private void sendError(HttpServletResponse resp, String msg) throws IOException {
        logger.fine("[" + CODIGO_LOG + "007] sendError - " + msg);
        resp.getWriter().write(JsonUtil.toJson(new Response(false, new ErrorMsg(msg))));
    }

    private static class Response {
        final boolean success;
        final Object data;

        Response(boolean success, Object data) {
            this.success = success;
            this.data = data;
        }
    }

    private static class ErrorMsg {
        final String error;
        ErrorMsg(String error) { this.error = error; }
    }
}
