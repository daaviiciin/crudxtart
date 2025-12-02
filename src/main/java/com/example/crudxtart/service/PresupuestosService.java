package com.example.crudxtart.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.crudxtart.models.Factura;
import com.example.crudxtart.models.PresupuestoProducto;
import com.example.crudxtart.models.Presupuestos;
import com.example.crudxtart.repository.PresupuestosRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class PresupuestosService {

    @Inject
    PresupuestosRepository presupuestosRepository;
    
    @Inject
    FacturaService facturaService;

    public List<Presupuestos> findAllPresupuestos() {
        return presupuestosRepository.findAllPresupuestos();
    }

    public Presupuestos findPresupuestoById(Integer id) {
        return presupuestosRepository.findPresupuestoById(id);
    }

    public Presupuestos createPresupuesto(Presupuestos p) {
        if (p.getFecha_apertura() == null) {
            p.setFecha_apertura(LocalDate.now());
        }
        
        if (p.getEstado() == null || p.getEstado().trim().isEmpty()) {
            p.setEstado("PENDIENTE");
        }
        
        if (p.getPresupuesto() <= 0 && p.getPresupuestoProductos() != null && !p.getPresupuestoProductos().isEmpty()) {
            double total = p.getPresupuestoProductos().stream()
                .mapToDouble(pp -> pp.getSubtotal() > 0 ? pp.getSubtotal() : pp.getPrecio_unitario() * pp.getCantidad())
                .sum();
            p.setPresupuesto(total);
        }
        
        validarPresupuesto(p);
        
        Presupuestos creado = presupuestosRepository.createPresupuesto(p);
        
        if (creado.getPresupuestoProductos() != null) {
            for (PresupuestoProducto pp : creado.getPresupuestoProductos()) {
                pp.setPresupuesto(creado);
                if (pp.getSubtotal() <= 0) {
                    pp.setSubtotal(pp.getPrecio_unitario() * pp.getCantidad());
                }
            }
        }
        
        return presupuestosRepository.updatePresupuesto(creado);
    }

    public Presupuestos updatePresupuesto(Presupuestos p) {
        Presupuestos existente = findPresupuestoById(p.getId_Presupuesto());
        if (existente != null && 
            p.getEstado() != null && 
            !existente.getEstado().equalsIgnoreCase(p.getEstado())) {
            validarTransicionEstado(existente.getEstado(), p.getEstado());
        }
        
        validarPresupuesto(p);
        
        if (existente != null &&
            p.getEstado() != null && 
            p.getEstado().equalsIgnoreCase("APROBADO") && 
            p.getFecha_cierre() == null &&
            !existente.getEstado().equalsIgnoreCase("APROBADO")) {
            p.setFecha_cierre(LocalDate.now());
        }
        
        if (p.getPresupuestoProductos() != null) {
            for (PresupuestoProducto pp : p.getPresupuestoProductos()) {
                pp.setPresupuesto(p);
                if (pp.getSubtotal() <= 0) {
                    pp.setSubtotal(pp.getPrecio_unitario() * pp.getCantidad());
                }
            }
        }
        
        if (p.getPresupuesto() <= 0 && p.getPresupuestoProductos() != null && !p.getPresupuestoProductos().isEmpty()) {
            double total = p.getPresupuestoProductos().stream()
                .mapToDouble(pp -> pp.getSubtotal() > 0 ? pp.getSubtotal() : pp.getPrecio_unitario() * pp.getCantidad())
                .sum();
            p.setPresupuesto(total);
        }
        
        return presupuestosRepository.updatePresupuesto(p);
    }

    public void deletePresupuesto(Integer id) {
        presupuestosRepository.deletebyid(id);
    }

    public List<Factura> generarFacturasDesdePresupuesto(Integer presupuestoId, Integer numPlazos) {
        Presupuestos presupuesto = findPresupuestoById(presupuestoId);
        
        if (presupuesto == null) {
            throw new IllegalArgumentException("Presupuesto no encontrado con ID: " + presupuestoId);
        }
        
        if (presupuesto.getEstado() == null ||
            !presupuesto.getEstado().equalsIgnoreCase("APROBADO")) {
            throw new IllegalArgumentException("Solo se pueden generar facturas de presupuestos aprobados");
        }
        
        if (numPlazos == null || numPlazos <= 0) {
            throw new IllegalArgumentException("El número de plazos debe ser mayor que 0");
        }
        
        // Verificar si el presupuesto ya tiene facturas asociadas
        List<Factura> facturasExistentes = facturaService.findFacturasByPresupuestoId(presupuestoId);
        if (facturasExistentes != null && !facturasExistentes.isEmpty()) {
            throw new IllegalArgumentException(
                "El presupuesto #" + presupuestoId + " ya tiene facturas asociadas. " +
                "No se pueden generar facturas adicionales desde este presupuesto."
            );
        }
        
        List<LocalDate> fechas = calcularFechasPlazos(numPlazos);
        double precioPorPlazo = presupuesto.getPresupuesto() / numPlazos;
        
        List<Factura> facturas = new ArrayList<>();
        for (int i = 0; i < numPlazos; i++) {
            Factura factura = new Factura();
            factura.setNum_factura(generarNumeroFactura(presupuestoId, i + 1));
            factura.setCliente_pagador(presupuesto.getCliente_pagador());
            factura.setEmpleado(presupuesto.getEmpleado());
            factura.setTotal(precioPorPlazo);
            factura.setFecha_emision(fechas.get(i));
            factura.setEstado(determinarEstadoFactura(fechas.get(i)));
            factura.setNotas("Plazo " + (i + 1) + "/" + numPlazos + 
                            " - Generada desde presupuesto #" + presupuestoId);
            
            facturas.add(facturaService.createFactura(factura));
        }
        
        return facturas;
    }

    private List<LocalDate> calcularFechasPlazos(int numPlazos) {
        LocalDate hoy = LocalDate.now();
        LocalDate primeraFecha = hoy.plusMonths(1).withDayOfMonth(1);
        
        List<LocalDate> fechas = new ArrayList<>();
        for (int i = 0; i < numPlazos; i++) {
            fechas.add(primeraFecha.plusMonths(i));
        }
        return fechas;
    }

    private String generarNumeroFactura(Integer presupuestoId, int plazo) {
        LocalDate hoy = LocalDate.now();
        String fechaStr = hoy.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String numFactura = String.format("FAC-%s-%d-%d", fechaStr, presupuestoId, plazo);
        
        if (numFactura.length() > 50) {
            numFactura = String.format("FAC-%s-P%d-%d", fechaStr, presupuestoId, plazo);
        }
        
        return numFactura;
    }

    private String determinarEstadoFactura(LocalDate fechaEmision) {
        LocalDate hoy = LocalDate.now();
        if (fechaEmision.isAfter(hoy)) {
            return "Pendiente";
        } else {
            return "Emitida";
        }
    }

    private void validarPresupuesto(Presupuestos presupuesto) {
        if (presupuesto.getCliente_pagador() == null) {
            throw new IllegalArgumentException("El presupuesto debe tener un cliente pagador");
        }
        
        if (presupuesto.getCliente_beneficiario() == null) {
            throw new IllegalArgumentException("El presupuesto debe tener un cliente beneficiario");
        }
        
        if (presupuesto.getEmpleado() == null) {
            throw new IllegalArgumentException("El presupuesto debe estar asociado a un empleado");
        }
        
        if (presupuesto.getPresupuestoProductos() == null || presupuesto.getPresupuestoProductos().isEmpty()) {
            throw new IllegalArgumentException("El presupuesto debe tener al menos un producto");
        }
        
        if (presupuesto.getPresupuesto() <= 0) {
            throw new IllegalArgumentException("El importe del presupuesto debe ser mayor que 0.");
        }

        if (presupuesto.getEstado() == null || presupuesto.getEstado().trim().isEmpty()) {
            throw new IllegalArgumentException("El estado del presupuesto es obligatorio.");
        }
        
        String estado = presupuesto.getEstado().trim().toUpperCase();
        if (!estado.equals("APROBADO") && !estado.equals("RECHAZADO") && !estado.equals("PENDIENTE")) {
            throw new IllegalArgumentException(
                "Estado inválido: " + presupuesto.getEstado() + 
                ". Estados válidos: APROBADO, RECHAZADO, PENDIENTE"
            );
        }
        
        presupuesto.setEstado(estado);
        
        if (presupuesto.getFecha_apertura() != null && 
            presupuesto.getFecha_cierre() != null &&
            presupuesto.getFecha_cierre().isBefore(presupuesto.getFecha_apertura())) {
            throw new IllegalArgumentException(
                "La fecha de cierre no puede ser anterior a la fecha de apertura"
            );
        }
        
        if (presupuesto.getEstado().equals("APROBADO") &&
            presupuesto.getFecha_cierre() == null) {
            throw new IllegalArgumentException(
                "Un presupuesto aprobado debe tener fecha de cierre"
            );
        }
    }
    
    private void validarTransicionEstado(String estadoAnterior, String estadoNuevo) {
        Map<String, List<String>> transicionesValidas = new HashMap<>();
        transicionesValidas.put("PENDIENTE", List.of("APROBADO", "RECHAZADO"));
        transicionesValidas.put("APROBADO", List.of("RECHAZADO"));
        transicionesValidas.put("RECHAZADO", List.of());
        
        String estadoAnteriorUpper = estadoAnterior != null ? estadoAnterior.toUpperCase() : "PENDIENTE";
        String estadoNuevoUpper = estadoNuevo != null ? estadoNuevo.toUpperCase() : "";
        
        List<String> estadosPermitidos = transicionesValidas.get(estadoAnteriorUpper);
        if (estadosPermitidos != null && !estadosPermitidos.contains(estadoNuevoUpper)) {
            throw new IllegalArgumentException(
                String.format("No se puede cambiar el estado de %s a %s", 
                    estadoAnterior, estadoNuevo)
            );
        }
    }
}
