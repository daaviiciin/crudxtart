package com.example.crudxtart.repository;

import com.example.crudxtart.models.Pagos;

import java.util.List;

public interface PagosRepository
{
    public List<Pagos> findAllPagos();
    public Pagos findPagosById(Integer id);
    public void savePagos(Pagos p);
    public void deletebyid(Integer id);
    public Pagos upLocalDatePagos(Pagos p);
    public Pagos createPagos(Pagos p);
}
