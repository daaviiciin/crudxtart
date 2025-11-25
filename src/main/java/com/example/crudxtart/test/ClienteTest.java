package com.example.crudxtart.test;

import com.example.crudxtart.models.Cliente;
import com.example.crudxtart.models.Empleado;
import com.example.crudxtart.service.ClienteService;
import com.example.crudxtart.service.EmpleadoService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class ClienteTest {

    @Inject
    private ClienteService clienteService;

    @Inject
    private EmpleadoService empleadoService;

    public ClienteTest() {}

    public void testClienteRepository() {



    }
}

