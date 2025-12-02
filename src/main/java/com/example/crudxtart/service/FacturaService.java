package com.example.crudxtart.service;

import java.time.LocalDate;
import java.util.List;

import com.example.crudxtart.models.Factura;
import com.example.crudxtart.repository.FacturaRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class FacturaService {

    @Inject
    FacturaRepository facturaRepository;

    public List<Factura> findAllFacturas() {
        return facturaRepository.findAllFacturas();
    }

    public Factura findFacturaById(Integer id) {
        return facturaRepository.findFacturaById(id);
    }

    public Factura createFactura(Factura f) {
        validarFactura(f);
        return facturaRepository.createFactura(f);
    }

    public Factura updateFactura(Factura f) {
        validarFactura(f);
        return facturaRepository.updateFactura(f);
    }

    public void deleteFactura(int id) {
        facturaRepository.deletebyid(id);
    }

    private void validarFactura(Factura factura) {

        if (factura.getNum_factura() == null || factura.getNum_factura().trim().isEmpty()) {
            throw new IllegalArgumentException("El número de factura es obligatorio.");
        }

        if (factura.getCliente_pagador() == null) {
            throw new IllegalArgumentException("La factura debe tener un cliente pagador asociado.");
        }

        if (factura.getEmpleado() == null) {
            throw new IllegalArgumentException("La factura debe estar asociada a un empleado.");
        }

        if (factura.getTotal() <= 0) {
            throw new IllegalArgumentException("El total de la factura debe ser mayor que 0.");
        }

        if (factura.getEstado() != null) {
            String estado = factura.getEstado().toLowerCase();
            if (!estado.equals("emitida") && !estado.equals("pagada") && !estado.equals("cancelada")) {
                throw new IllegalArgumentException("Estado de factura inválido: " + factura.getEstado());
            }
        }

        LocalDate hoy = LocalDate.now();

        if (factura.getFecha_emision() == null) {
            throw new IllegalArgumentException("La fecha de emisión es obligatoria.");
        }

        LocalDate fechaMaxima = hoy.plusYears(1);

        if (factura.getFecha_emision().isAfter(fechaMaxima)) {
            throw new IllegalArgumentException("La fecha de emisión no puede ser más de 1 año en el futuro.");
        }
    }
}
