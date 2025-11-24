package com.example.crudxtart.presentation;

import com.example.crudxtart.test.PresupuestosTest;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/testpresupuestos")
public class TestServletPresupuestos extends HttpServlet {

    @Inject
    public PresupuestosTest presupuestosTest;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        presupuestosTest.testPresupuestosRepository();
        resp.getWriter().println("Prueba CRUD PRESUPUESTOS ejecutada. Revisa la consola de Tomcat.");
    }
}
