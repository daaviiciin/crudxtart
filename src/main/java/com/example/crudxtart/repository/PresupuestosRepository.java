package com.example.crudxtart.repository;

import com.example.crudxtart.models.Presupuestos;

import java.util.List;

public interface PresupuestosRepository {

    public List<Presupuestos> findAllPresupuestos();

    public Presupuestos findPresupuestoById(Integer id);

    public void savePresupuesto(Presupuestos p);

    public void deletebyid(Integer id);

    public Presupuestos updatePresupuesto(Presupuestos p);

    public Presupuestos createPresupuesto(Presupuestos p);
}
