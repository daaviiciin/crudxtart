package com.example.crudxtart.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;
import java.util.Map;

import com.example.crudxtart.models.Factura;
import com.example.crudxtart.models.FacturaProducto;
import com.example.crudxtart.models.PresupuestoProducto;
import com.example.crudxtart.models.Presupuestos;
import com.example.crudxtart.repository.PresupuestosRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class PresupuestosService {


    private static final Logger logger = Logger.getLogger(PresupuestosService.class.getName());
    private static final String CODIGO_LOG = "SRV-PRE-";

    @Inject
    PresupuestosRepository presupuestosRepository;

    @Inject
    FacturaService facturaService;

    @Inject
    FacturaProductoService facturaProductoService;

    public List<Presupuestos> findAllPresupuestos() {
        logger.info("[" + CODIGO_LOG + "001] findAllPresupuestos() - Inicio"); // CAMBIO LOG
        return presupuestosRepository.findAllPresupuestos();
    }

    public Presupuestos findPresupuestoById(Integer id) {
        logger.info("[" + CODIGO_LOG + "001] findPresupuestoById() - Inicio"); // CAMBIO LOG
        return presupuestosRepository.findPresupuestoById(id);
    }

    public Presupuestos createPresupuesto(Presupuestos p) {
        logger.info("[" + CODIGO_LOG + "001] createPresupuesto() - Inicio"); // CAMBIO LOG
        if (p.getFecha_apertura() == null) {
            p.setFecha_apertura(LocalDate.now());
        }

        if (p.getEstado() == null || p.getEstado().trim().isEmpty()) {
            p.setEstado("PENDIENTE");
        }

        // Si el estado es APROBADO y no tiene fecha_cierre, asignarla automáticamente ANTES de validar
        if (p.getEstado() != null &&
                p.getEstado().equalsIgnoreCase("APROBADO") &&
                p.getFecha_cierre() == null) {
            p.setFecha_cierre(LocalDate.now());
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
        logger.info("[" + CODIGO_LOG + "001] updatePresupuesto() - Inicio"); // CAMBIO LOG
// Obtener el estado original de la BD antes de cualquier modificación
        Presupuestos existente = findPresupuestoById(p.getId_Presupuesto());
        String estadoOriginal = existente != null ? existente.getEstado() : null;

        // Mantener fecha_cierre del objeto si no viene en el objeto p
        // (necesario porque el servlet puede no enviar todos los campos)
        if (existente != null && p.getFecha_cierre() == null) {
            p.setFecha_cierre(existente.getFecha_cierre());
        }

        // Validar transición de estado si cambió
        if (existente != null &&
                p.getEstado() != null &&
                estadoOriginal != null &&
                !estadoOriginal.equalsIgnoreCase(p.getEstado())) {
            validarTransicionEstado(estadoOriginal, p.getEstado());
        }

        // Si el estado es APROBADO y no tiene fecha_cierre, asignarla automáticamente ANTES de validar
        if (p.getEstado() != null &&
                p.getEstado().equalsIgnoreCase("APROBADO") &&
                p.getFecha_cierre() == null) {
            p.setFecha_cierre(LocalDate.now());
        }

        // Si el estado cambia de APROBADO a otro, limpiar fecha_cierre
        if (estadoOriginal != null &&
                estadoOriginal.equalsIgnoreCase("APROBADO") &&
                p.getEstado() != null &&
                !p.getEstado().equalsIgnoreCase("APROBADO")) {
            p.setFecha_cierre(null);
        }

        // Validar después de asignar fecha_cierre automáticamente
        validarPresupuesto(p);

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
        logger.info("[" + CODIGO_LOG + "001] deletePresupuesto() - Inicio"); // CAMBIO LOG
        presupuestosRepository.deletebyid(id);
    }

    public List<Factura> generarFacturasDesdePresupuesto(Integer presupuestoId, Integer numPlazos) {
        logger.info("[" + CODIGO_LOG + "001] generarFacturasDesdePresupuesto() - Inicio"); // CAMBIO LOG
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

        // Obtener productos del presupuesto
        List<PresupuestoProducto> productosPresupuesto = presupuesto.getPresupuestoProductos();
        if (productosPresupuesto == null || productosPresupuesto.isEmpty()) {
            throw new IllegalArgumentException("El presupuesto no tiene productos asociados");
        }

        // Calcular subtotal por producto por plazo (distribución proporcional)
        double totalPresupuesto = presupuesto.getPresupuesto();

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

            // Crear la factura primero para obtener el ID
            Factura facturaCreada = facturaService.createFactura(factura);

            // Copiar productos del presupuesto a la factura
            double subtotalAcumulado = 0.0;
            for (int j = 0; j < productosPresupuesto.size(); j++) {
                PresupuestoProducto pp = productosPresupuesto.get(j);
                FacturaProducto fp = new FacturaProducto();
                fp.setFactura(facturaCreada);
                fp.setProducto(pp.getProducto());
                fp.setCliente_beneficiario(pp.getCliente_beneficiario());
                fp.setCantidad(pp.getCantidad());
                fp.setPrecio_unitario(pp.getPrecio_unitario());

                // Distribuir el subtotal proporcionalmente entre plazos
                // Calcular qué porcentaje del total del presupuesto representa este producto
                double porcentajeProducto = pp.getSubtotal() / totalPresupuesto;
                double subtotalProductoEnEstePlazo = precioPorPlazo * porcentajeProducto;

                // Si es el último producto del último plazo, ajustar para que sume exactamente
                if (i == numPlazos - 1 && j == productosPresupuesto.size() - 1) {
                    // Asegurar que el total de la factura sea exactamente precioPorPlazo
                    fp.setSubtotal(precioPorPlazo - subtotalAcumulado);
                } else {
                    fp.setSubtotal(subtotalProductoEnEstePlazo);
                    subtotalAcumulado += subtotalProductoEnEstePlazo;
                }

                // Guardar el producto de factura
                facturaProductoService.createFacturaProducto(fp);
            }

            facturas.add(facturaCreada);
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
            return "PENDIENTE";
        } else {
            return "EMITIDA";
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
