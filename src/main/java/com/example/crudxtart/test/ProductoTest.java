package com.example.crudxtart.test;

import com.example.crudxtart.models.Presupuestos;
import com.example.crudxtart.models.Producto;
import com.example.crudxtart.service.ProductoService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class ProductoTest {

    @Inject
    private ProductoService productoService;

    public ProductoTest() {}

    public void testProductoRepository() {

        productoService.deleteProducto(8);

    }
}
