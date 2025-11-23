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

    public Producto findProductoById(int id) {
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

    public void deleteProducto(int id) {
        productoRepository.deletebyid(id);
    }

    private void validarProducto(Producto producto) {

        if (producto.getNombre() == null || producto.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del producto es obligatorio.");
        }

        if (producto.getPrecio() < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo.");
        }

        if (producto.getCategoria() != null && producto.getCategoria().trim().isEmpty()) {
            throw new IllegalArgumentException("La categoría no puede ser solo espacios en blanco.");
        }
    }
}
