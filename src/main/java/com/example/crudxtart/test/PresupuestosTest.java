package com.example.crudxtart.test;

import com.example.crudxtart.models.Presupuestos;
import com.example.crudxtart.service.PresupuestosService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class PresupuestosTest {

    @Inject
    private PresupuestosService presupuestosService;

    public PresupuestosTest() {}

    public void testPresupuestosRepository() {
        List<Presupuestos> lista = presupuestosService.findAllPresupuestos();
        lista.forEach(p -> {
            System.out.println(
                    "ID: " + p.getId_Presupuesto() +
                            " | Presupuesto: " + p.getPresupuesto() +
                            " | Estado: " + p.getEstado()
            );
        });
    }
}
