package com.example.crudxtart.service;

import com.example.crudxtart.models.Factura;
import com.example.crudxtart.repository.FacturaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class FacturaService {

    @Inject
    FacturaRepository facturaRepository;

    public List<Factura> findAllFacturas() {
        return facturaRepository.findAllFacturas();
    }

    public Factura findFacturaById(int id) {
        return facturaRepository.findFacturaById(id);
    }

    public Factura createFactura(Factura f) {
        validarFactura(f);
        return facturaRepository.createFactura(f);
    }

    public Factura upLocalDateFactura(Factura f) {
        validarFactura(f);
        return facturaRepository.upLocalDateFactura(f);
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
            if (!estado.equals("pendiente") && !estado.equals("pagada") && !estado.equals("cancelada")) {
                throw new IllegalArgumentException("Estado de factura inválido: " + factura.getEstado());
            }
        }

        LocalDate hoy = LocalDate.now();

        if (factura.getFecha_emision() == null) {
            throw new IllegalArgumentException("La fecha de emisión es obligatoria.");
        }

        if (factura.getFecha_emision().isAfter(hoy)) {
            throw new IllegalArgumentException("La fecha de emisión no puede ser futura.");
        }
    }
}
