package com.example.crudxtart.presentation;

import com.example.crudxtart.test.ProductoTest;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/testproductos")
public class TestServletProducto extends HttpServlet {

    @Inject
    public ProductoTest productoTest;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        productoTest.testProductoRepository();
        resp.getWriter().println("Prueba CRUD PRODUCTOS ejecutada. Revisa la consola de Tomcat.");
    }
}
