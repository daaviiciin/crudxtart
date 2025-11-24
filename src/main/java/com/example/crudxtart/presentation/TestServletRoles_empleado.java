package com.example.crudxtart.presentation;

import com.example.crudxtart.test.Roles_empleadoTest;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/testroles")
public class TestServletRoles_empleado extends HttpServlet {

    @Inject
    public Roles_empleadoTest roles_empleadoTest;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        roles_empleadoTest.testRoles_empleadoRepository();
        resp.getWriter().println("Prueba CRUD ROLES_EMPLEADO ejecutada. Revisa la consola de Tomcat.");
    }
}
