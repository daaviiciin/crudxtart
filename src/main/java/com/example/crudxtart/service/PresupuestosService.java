package com.example.crudxtart.service;

import com.example.crudxtart.models.Presupuestos;
import com.example.crudxtart.repository.PresupuestosRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class PresupuestosService {

    @Inject
    PresupuestosRepository presupuestosRepository;

    public List<Presupuestos> findAllPresupuestos() {
        return presupuestosRepository.findAllPresupuestos();
    }

    public Presupuestos findPresupuestoById(Integer id) {
        return presupuestosRepository.findPresupuestoById(id);
    }

    public Presupuestos createPresupuesto(Presupuestos p) {
        validarPresupuesto(p);
        return presupuestosRepository.createPresupuesto(p);
    }

    public Presupuestos updatePresupuesto(Presupuestos p) {
        validarPresupuesto(p);
        return presupuestosRepository.updatePresupuesto(p);
    }

    public void deletePresupuesto(Integer id) {
        presupuestosRepository.deletebyid(id);
    }

    private void validarPresupuesto(Presupuestos presupuesto) {

        if (presupuesto.getPresupuesto() <= 0) {
            throw new IllegalArgumentException("El importe del presupuesto debe ser mayor que 0.");
        }

        if (presupuesto.getEstado() == null || presupuesto.getEstado().trim().isEmpty()) {
            throw new IllegalArgumentException("El estado del presupuesto es obligatorio.");
        }
    }
}
