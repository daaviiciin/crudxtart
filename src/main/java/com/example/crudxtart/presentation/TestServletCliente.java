package com.example.crudxtart.presentation;

import com.example.crudxtart.test.ClienteTest;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/testclientes")
public class TestServletCliente extends HttpServlet {

    @Inject
    public ClienteTest clienteTest;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            clienteTest.testClienteRepository();
        }catch (Exception ex){
            ex.printStackTrace();
        }

        resp.getWriter().println("Prueba CRUD ejecutada. Revisa la consola de Tomcat.");
    }
}

