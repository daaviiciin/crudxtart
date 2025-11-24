package com.example.crudxtart.presentation;

import com.example.crudxtart.test.FacturaTest;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/testfacturas")
public class TestServletFactura extends HttpServlet {

    @Inject
    public FacturaTest facturaTest;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        facturaTest.testFacturaRepository();
        resp.getWriter().println("Prueba CRUD FACTURAS ejecutada. Revisa la consola de Tomcat.");
    }
}
