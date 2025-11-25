package com.example.crudxtart.test;

import com.example.crudxtart.models.Cliente;
import com.example.crudxtart.models.Empleado;
import com.example.crudxtart.models.Presupuestos;
import com.example.crudxtart.models.Producto;
import com.example.crudxtart.service.ClienteService;
import com.example.crudxtart.service.EmpleadoService;
import com.example.crudxtart.service.PresupuestosService;
import com.example.crudxtart.service.ProductoService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class PresupuestosTest {

    @Inject
    private PresupuestosService presupuestosService;

    @Inject
    private EmpleadoService empleadoService;

    @Inject
    private ClienteService clienteService;

    @Inject
    private ProductoService productoService;

    public PresupuestosTest() {}

    public void testPresupuestosRepository()
    {

        presupuestosService.deletePresupuesto(10);

    }
}
