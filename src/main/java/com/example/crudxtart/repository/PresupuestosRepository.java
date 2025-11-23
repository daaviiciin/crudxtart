package com.example.crudxtart.repository;

import com.example.crudxtart.models.Presupuestos;

import java.util.List;

public interface PresupuestosRepository {

    public List<Presupuestos> findAllPresupuestos();

    public Presupuestos findPresupuestoById(int id);

    public void savePresupuesto(Presupuestos p);

    public void deletebyid(int id);

    public Presupuestos updatePresupuesto(Presupuestos p);

    public Presupuestos createPresupuesto(Presupuestos p);
}
