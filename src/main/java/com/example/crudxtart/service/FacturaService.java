package com.example.crudxtart.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.crudxtart.models.Factura;
import com.example.crudxtart.models.Pagos;
import com.example.crudxtart.repository.FacturaRepository;
import com.example.crudxtart.repository.PagosRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class FacturaService {

    @Inject
    FacturaRepository facturaRepository;
    
    @Inject
    PagosRepository pagosRepository;

    public List<Factura> findAllFacturas() {
        List<Factura> facturas = facturaRepository.findAllFacturas();
        for (Factura factura : facturas) {
            actualizarEstadoAutomatico(factura);
        }
        return facturas;
    }

    public Factura findFacturaById(Integer id) {
        Factura factura = facturaRepository.findFacturaById(id);
        if (factura != null) {
            actualizarEstadoAutomatico(factura);
        }
        return factura;
    }

    public Factura createFactura(Factura f) {
        if (f.getNum_factura() == null || f.getNum_factura().trim().isEmpty()) {
            f.setNum_factura(generarNumeroFactura());
        }
        validarFactura(f);
        if (f.getEstado() == null || f.getEstado().trim().isEmpty()) {
            f.setEstado(determinarEstado(f.getFecha_emision()));
        }
        return facturaRepository.createFactura(f);
    }

    public Factura updateFactura(Factura f) {
        Factura existente = findFacturaById(f.getId_factura());
        if (existente != null && 
            f.getEstado() != null && 
            !existente.getEstado().equalsIgnoreCase(f.getEstado())) {
            String estadoAnterior = existente.getEstado();
            String estadoNuevo = f.getEstado();
            validarTransicionEstadoFactura(estadoAnterior, estadoNuevo);
        }
        
        validarFactura(f);
        actualizarEstadoAutomatico(f);
        return facturaRepository.updateFactura(f);
    }
    
    private String determinarEstado(LocalDate fechaEmision) {
        LocalDate hoy = LocalDate.now();
        if (fechaEmision.isAfter(hoy)) {
            return "Pendiente";
        } else {
            return "Emitida";
        }
    }
    
    private void actualizarEstadoAutomatico(Factura factura) {
        LocalDate hoy = LocalDate.now();
        LocalDate fechaEmision = factura.getFecha_emision();
        String estadoActual = factura.getEstado() != null ? factura.getEstado().toLowerCase() : "";

        if (estadoActual.equals("pendiente") && !fechaEmision.isAfter(hoy)) {
            factura.setEstado("Emitida");
            estadoActual = "emitida";
        }
        
        if (estadoActual.equals("emitida")) {
            LocalDate finMesFacturacion = fechaEmision.withDayOfMonth(fechaEmision.lengthOfMonth());
            if (hoy.isAfter(finMesFacturacion)) {
                List<Pagos> pagos = pagosRepository.findPagosByFacturaId(factura.getId_factura());
                boolean tienePagosConfirmados = pagos.stream()
                    .anyMatch(p -> p.getEstado() != null && 
                        p.getEstado().equalsIgnoreCase("confirmado"));
                
                if (!tienePagosConfirmados) {
                    factura.setEstado("No pagada");
                }
            }
        }
    }
    
    private void validarTransicionEstadoFactura(String estadoAnterior, String estadoNuevo) {
        Map<String, List<String>> transicionesValidas = new HashMap<>();
        transicionesValidas.put("Pendiente", List.of("Emitida", "Cancelada"));
        transicionesValidas.put("Emitida", List.of("Pagada", "No pagada", "Cancelada"));
        transicionesValidas.put("Pagada", List.of());
        transicionesValidas.put("No pagada", List.of("Pagada", "Cancelada"));
        transicionesValidas.put("Cancelada", List.of());
        
        String estadoAnteriorNorm = normalizarEstadoFactura(estadoAnterior);
        String estadoNuevoNorm = normalizarEstadoFactura(estadoNuevo);
        
        List<String> estadosPermitidos = transicionesValidas.get(estadoAnteriorNorm);
        if (estadosPermitidos != null && !estadosPermitidos.contains(estadoNuevoNorm)) {
            throw new IllegalArgumentException(
                String.format("No se puede cambiar el estado de %s a %s", 
                    estadoAnterior, estadoNuevo)
            );
        }
    }
    
    private String normalizarEstadoFactura(String estado) {
        if (estado == null || estado.trim().isEmpty()) {
            return "";
        }
        String estadoLower = estado.toLowerCase().trim();
        if (estadoLower.equals("pendiente")) {
            return "Pendiente";
        } else if (estadoLower.equals("emitida")) {
            return "Emitida";
        } else if (estadoLower.equals("pagada")) {
            return "Pagada";
        } else if (estadoLower.equals("no pagada") || estadoLower.equals("nopagada")) {
            return "No pagada";
        } else if (estadoLower.equals("cancelada")) {
            return "Cancelada";
        }
        return estado;
    }

    public void deleteFactura(int id) {
        facturaRepository.deletebyid(id);
    }
    
    public List<Factura> findFacturasByPresupuestoId(Integer presupuestoId) {
        return facturaRepository.findFacturasByPresupuestoId(presupuestoId);
    }
    
    public double calcularTotalPendiente(Integer facturaId) {
        Factura factura = findFacturaById(facturaId);
        if (factura == null) {
            throw new IllegalArgumentException("Factura no encontrada");
        }
        
        List<Pagos> pagos = pagosRepository.findPagosByFacturaId(facturaId);
        double totalPagado = pagos.stream()
            .filter(p -> p.getEstado() != null && 
                        p.getEstado().equalsIgnoreCase("confirmado"))
            .mapToDouble(Pagos::getImporte)
            .sum();
        
        return factura.getTotal() - totalPagado;
    }

    private String generarNumeroFactura() {
        LocalDate hoy = LocalDate.now();
        String fechaStr = hoy.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        Long siguienteNumero = facturaRepository.getSiguienteNumeroSecuencia();
        return String.format("FAC-%s-%05d", fechaStr, siguienteNumero);
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
            if (!estado.equals("pendiente") && !estado.equals("emitida") && 
                !estado.equals("pagada") && !estado.equals("no pagada")) {
                throw new IllegalArgumentException("Estado de factura inválido: " + factura.getEstado() + 
                    ". Estados válidos: Pendiente, Emitida, Pagada, No pagada");
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
