package com.example.crudxtart.repository;

import com.example.crudxtart.models.FacturaProducto;
import jakarta.persistence.criteria.CriteriaBuilder;

import java.util.List;

public interface FacturaProductoRepository {

    public List<FacturaProducto> findAllFacturaProductos();

    public FacturaProducto findFacturaProductoById(Integer id);

    public void saveFacturaProducto(FacturaProducto fp);

    public void deletebyid(Integer id);

    public FacturaProducto updateFacturaProducto(FacturaProducto fp);

    public FacturaProducto createFacturaProducto(FacturaProducto fp);
}
