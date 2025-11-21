package com.example.crudxtart.repository;

import com.example.crudxtart.models.Pagos;

import java.util.List;

public interface PagosRepository
{
    public List<Pagos> findAllPagos();
    public Pagos findPagosById(int id);
    public void savePagos(Pagos p);
    public void deletebyid(int id);
    public Pagos updatePagos(Pagos p);
    public Pagos createPagos(Pagos p);
}
