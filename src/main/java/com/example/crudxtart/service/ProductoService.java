package com.example.crudxtart.service;

import com.example.crudxtart.models.Producto;
import com.example.crudxtart.repository.ProductoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class ProductoService {

    @Inject
    ProductoRepository productoRepository;

    public List<Producto> findAllProductos() {
        return productoRepository.findAllProductos();
    }

    public Producto findProductoById(Integer id) {
        return productoRepository.findProductoById(id);
    }

    public Producto createProducto(Producto p) {
        validarProducto(p);
        return productoRepository.createProducto(p);
    }

    public Producto updateProducto(Producto p) {
        validarProducto(p);
        return productoRepository.updateProducto(p);
    }

    public void deleteProducto(Integer id) {
        productoRepository.deletebyid(id);
    }

    private void validarProducto(Producto producto) {

        if (producto.getNombre() == null || producto.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del producto es obligatorio.");
        }

        if (producto.getPrecio() < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo.");
        }

        // Normalizar y validar categoria
        if (producto.getCategoria() != null && !producto.getCategoria().trim().isEmpty()) {
            String categoria = producto.getCategoria().trim().toUpperCase();
            // Normalizar valores
            if (categoria.equals("CICLO FORMATIVO") || categoria.equals("CICLO_FORMATIVO")) {
                producto.setCategoria("CICLO FORMATIVO");
            } else if (categoria.equals("FORMACION COMPLEMENTARIA") || categoria.equals("FORMACIÓN COMPLEMENTARIA") || 
                       categoria.equals("FORMACION_COMPLEMENTARIA") || categoria.equals("FORMACIÓN_COMPLEMENTARIA")) {
                producto.setCategoria("FORMACION COMPLEMENTARIA");
            } else if (!categoria.equals("CICLO FORMATIVO") && !categoria.equals("FORMACION COMPLEMENTARIA")) {
                throw new IllegalArgumentException("Categoría inválida: " + producto.getCategoria() + 
                    ". Valores válidos: CICLO FORMATIVO, FORMACION COMPLEMENTARIA");
            } else {
                producto.setCategoria(categoria);
            }
        }
    }
}
