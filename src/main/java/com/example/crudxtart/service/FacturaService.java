package com.example.crudxtart.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.example.crudxtart.models.Factura;
import com.example.crudxtart.models.Pagos;
import com.example.crudxtart.repository.FacturaRepository;
import com.example.crudxtart.repository.PagosRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class FacturaService {


    private static final Logger logger = Logger.getLogger(FacturaService.class.getName());
    private static final String CODIGO_LOG = "SRVC-FAC-";

    @Inject
    FacturaRepository facturaRepository;

    @Inject
    PagosRepository pagosRepository;

    public List<Factura> findAllFacturas() {
        logger.info("[" + CODIGO_LOG + "001] findAllFacturas() - Inicio"); // CAMBIO LOG
        List<Factura> facturas = facturaRepository.findAllFacturas();
        for (Factura factura : facturas) {
            actualizarEstadoAutomatico(factura);
        }
        return facturas;
    }

    public Factura findFacturaById(Integer id) {
        logger.info("[" + CODIGO_LOG + "002] findFacturaById() - Inicio"); // CAMBIO LOG
        Factura factura = facturaRepository.findFacturaById(id);
        if (factura != null) {
            actualizarEstadoAutomatico(factura);
        }
        return factura;
    }

    public Factura createFactura(Factura f) {
        logger.info("[" + CODIGO_LOG + "003] createFactura() - Inicio"); // CAMBIO LOG
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
        logger.info("[" + CODIGO_LOG + "004] updateFactura() - Inicio"); // CAMBIO LOG
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
            return "PENDIENTE";
        } else {
            return "EMITIDA";
        }
    }

    private void actualizarEstadoAutomatico(Factura factura) {
        LocalDate hoy = LocalDate.now();
        LocalDate fechaEmision = factura.getFecha_emision();
        String estadoActual = factura.getEstado() != null ? factura.getEstado().toUpperCase() : "";

        if (estadoActual.equals("PENDIENTE") && !fechaEmision.isAfter(hoy)) {
            factura.setEstado("EMITIDA");
            estadoActual = "EMITIDA";
        }

        if (estadoActual.equals("EMITIDA")) {
            LocalDate finMesFacturacion = fechaEmision.withDayOfMonth(fechaEmision.lengthOfMonth());
            if (hoy.isAfter(finMesFacturacion)) {
                List<Pagos> pagos = pagosRepository.findPagosByFacturaId(factura.getId_factura());
                boolean tienePagosPagados = pagos.stream()
                        .anyMatch(p -> p.getEstado() != null &&
                                p.getEstado().equalsIgnoreCase("PAGADA"));

                if (!tienePagosPagados) {
                    factura.setEstado("VENCIDA");
                }
            }
        }
    }

    private void validarTransicionEstadoFactura(String estadoAnterior, String estadoNuevo) {
        Map<String, List<String>> transicionesValidas = new HashMap<>();
        transicionesValidas.put("PENDIENTE", List.of("EMITIDA", "VENCIDA"));
        transicionesValidas.put("EMITIDA", List.of("PAGADA", "VENCIDA"));
        transicionesValidas.put("PAGADA", List.of());
        transicionesValidas.put("VENCIDA", List.of("PAGADA"));

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
        String estadoUpper = estado.toUpperCase().trim();
        // Mapear estados antiguos a nuevos
        if (estadoUpper.equals("PENDIENTE")) {
            return "PENDIENTE";
        } else if (estadoUpper.equals("EMITIDA")) {
            return "EMITIDA";
        } else if (estadoUpper.equals("PAGADA")) {
            return "PAGADA";
        } else if (estadoUpper.equals("VENCIDA") || estadoUpper.equals("NO PAGADA") || estadoUpper.equals("NOPAGADA")) {
            return "VENCIDA";
        }
        return estadoUpper;
    }

    public void deleteFactura(int id) {
        logger.info("[" + CODIGO_LOG + "005] deleteFactura() - Inicio"); // CAMBIO LOG
        facturaRepository.deletebyid(id);
    }

    public List<Factura> findFacturasByPresupuestoId(Integer presupuestoId) {
        logger.info("[" + CODIGO_LOG + "006] findFacturasByPresupuestoId() - Inicio"); // CAMBIO LOG
        return facturaRepository.findFacturasByPresupuestoId(presupuestoId);
    }

    public double calcularTotalPendiente(Integer facturaId) {
        logger.info("[" + CODIGO_LOG + "007] calcularTotalPendiente() - Inicio"); // CAMBIO LOG
        Factura factura = findFacturaById(facturaId);
        if (factura == null) {
            throw new IllegalArgumentException("Factura no encontrada");
        }

        List<Pagos> pagos = pagosRepository.findPagosByFacturaId(facturaId);
        double totalPagado = pagos.stream()
                .filter(p -> p.getEstado() != null &&
                        p.getEstado().equalsIgnoreCase("PAGADA"))
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
            String estado = factura.getEstado().toUpperCase().trim();
            if (!estado.equals("PENDIENTE") && !estado.equals("EMITIDA") &&
                    !estado.equals("PAGADA") && !estado.equals("VENCIDA")) {
                throw new IllegalArgumentException("Estado de factura inválido: " + factura.getEstado() +
                        ". Estados válidos: PENDIENTE, EMITIDA, PAGADA, VENCIDA");
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
