package com.example.crudxtart.repository;

import java.util.List;

import com.example.crudxtart.models.Factura;

public interface FacturaRepository {

    public List<Factura> findAllFacturas();

    public Factura findFacturaById(Integer id);

    public void saveFactura(Factura f);

    public void deletebyid(Integer id);

    public Factura updateFactura(Factura f);

    public Factura createFactura(Factura f);
    
    public Long getSiguienteNumeroSecuencia();
    
    public List<Factura> findFacturasByPresupuestoId(Integer presupuestoId);
}
