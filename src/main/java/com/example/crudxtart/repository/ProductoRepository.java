package com.example.crudxtart.repository;

import com.example.crudxtart.models.Producto;

import java.util.List;

public interface ProductoRepository {

    public List<Producto> findAllProductos();

    public Producto findProductoById(Integer id);

    public void saveProducto(Producto p);

    public void deletebyid(Integer id);

    public Producto upLocalDateProducto(Producto p);

    public Producto createProducto(Producto p);
}
