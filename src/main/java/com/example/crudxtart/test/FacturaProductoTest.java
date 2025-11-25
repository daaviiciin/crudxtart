package com.example.crudxtart.test;

import com.example.crudxtart.models.Cliente;
import com.example.crudxtart.models.Factura;
import com.example.crudxtart.models.FacturaProducto;
import com.example.crudxtart.models.Producto;
import com.example.crudxtart.repository.FacturaProductoRepository;
import com.example.crudxtart.service.ClienteService;
import com.example.crudxtart.service.FacturaProductoService;
import com.example.crudxtart.service.FacturaService;
import com.example.crudxtart.service.ProductoService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class FacturaProductoTest {

    @Inject
    private FacturaProductoService facturaProductoService;

    @Inject
    private ProductoService productoService;

    @Inject
    private FacturaService facturaService;

    @Inject
    private ClienteService clienteService;

    public FacturaProductoTest() {}

    public void testFacturaProductoRepository()
    {




    }
}
