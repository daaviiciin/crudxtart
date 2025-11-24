package com.example.crudxtart.test;

import com.example.crudxtart.models.Factura;
import com.example.crudxtart.service.FacturaService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class FacturaTest {

    @Inject
    private FacturaService facturaService;

    public FacturaTest() {}

    public void testFacturaRepository() {
        List<Factura> lista = facturaService.findAllFacturas();
        lista.forEach(f -> {
            System.out.println(
                    "ID: " + f.getId_factura() +
                            " | Num factura: " + f.getNum_factura() +
                            " | Cliente pagador: " +
                            (f.getCliente_pagador() != null ? f.getCliente_pagador().getNombre() : "null") +
                            " | Empleado: " +
                            (f.getEmpleado() != null ? f.getEmpleado().getNombre() : "null") +
                            " | Fecha emisión: " + f.getFecha_emision() +
                            " | Total: " + f.getTotal() +
                            " | Estado: " + f.getEstado()
            );
        });
    }
}
