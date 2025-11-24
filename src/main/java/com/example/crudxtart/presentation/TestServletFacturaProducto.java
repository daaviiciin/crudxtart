package com.example.crudxtart.presentation;

import com.example.crudxtart.test.FacturaProductoTest;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/testfacturaproductos")
public class TestServletFacturaProducto extends HttpServlet {

    @Inject
    public FacturaProductoTest facturaProductoTest;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        facturaProductoTest.testFacturaProductoRepository();
        resp.getWriter().println("Prueba CRUD FACTURA_PRODUCTOS ejecutada. Revisa la consola de Tomcat.");
    }
}
