package com.example.crudxtart.presentation;

import com.example.crudxtart.test.EmpleadoTest;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/testempleados")
public class TestServletEmpleado extends HttpServlet {

    @Inject
    public EmpleadoTest empleadoTest;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        empleadoTest.testEmpleadoRepository();
        resp.getWriter().println("Prueba CRUD EMPLEADOS ejecutada. Revisa la consola de Tomcat.");
    }
}
