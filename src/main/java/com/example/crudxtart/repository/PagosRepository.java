package com.example.crudxtart.repository;

import java.util.List;

import com.example.crudxtart.models.Pagos;

public interface PagosRepository
{
    public List<Pagos> findAllPagos();
    public Pagos findPagosById(Integer id);
    public List<Pagos> findPagosByFacturaId(Integer facturaId);
    public void savePagos(Pagos p);
    public void deletebyid(Integer id);
    public Pagos upLocalDatePagos(Pagos p);
    public Pagos createPagos(Pagos p);
}
