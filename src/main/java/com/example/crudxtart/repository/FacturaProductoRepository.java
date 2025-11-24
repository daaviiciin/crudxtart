package com.example.crudxtart.repository;

import com.example.crudxtart.models.FacturaProducto;

import java.util.List;

public interface FacturaProductoRepository {

    public List<FacturaProducto> findAllFacturaProductos();

    public FacturaProducto findFacturaProductoById(int id);

    public void saveFacturaProducto(FacturaProducto fp);

    public void deletebyid(int id);

    public FacturaProducto updateFacturaProducto(FacturaProducto fp);

    public FacturaProducto createFacturaProducto(FacturaProducto fp);
}
