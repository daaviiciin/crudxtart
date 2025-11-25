package com.example.crudxtart.repository;

import com.example.crudxtart.models.Factura;

import java.util.List;

public interface FacturaRepository {

    public List<Factura> findAllFacturas();

    public Factura findFacturaById(Integer id);

    public void saveFactura(Factura f);

    public void deletebyid(Integer id);

    public Factura updateFactura(Factura f);

    public Factura createFactura(Factura f);
}
