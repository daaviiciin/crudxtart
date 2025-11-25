package com.example.crudxtart.test;

import com.example.crudxtart.models.Cliente;
import com.example.crudxtart.models.Empleado;
import com.example.crudxtart.models.Factura;
import com.example.crudxtart.service.ClienteService;
import com.example.crudxtart.service.EmpleadoService;
import com.example.crudxtart.service.FacturaService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class FacturaTest {

    @Inject
    private FacturaService facturaService;

    @Inject
    private ClienteService clienteService;

    @Inject
    private EmpleadoService empleadoService;


    public FacturaTest() {}

    public void testFacturaRepository() {

       ;
    }
}
