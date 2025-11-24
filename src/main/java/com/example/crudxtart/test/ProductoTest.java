package com.example.crudxtart.test;

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
        List<Producto> lista = productoService.findAllProductos();
        lista.forEach(p -> {
            System.out.println(
                    "ID: " + p.getId_producto() +
                            " | Nombre: " + p.getNombre() +
                            " | Descripción: " + p.getDescripcion() +
                            " | Categoría: " + p.getCategoria() +
                            " | Precio: " + p.getPrecio() +
                            " | Activo: " + p.isActivo()
            );
        });
    }
}
