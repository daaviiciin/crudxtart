package com.example.crudxtart.service;

import java.time.LocalDate;
import java.util.List;

import com.example.crudxtart.models.Factura;
import com.example.crudxtart.models.Pagos;
import com.example.crudxtart.repository.PagosRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class PagosService
{
    @Inject
    PagosRepository pagosRepository;
    
    @Inject
    FacturaService facturaService;

    public List<Pagos> findAllPagos()
    {
        return pagosRepository.findAllPagos();
    }

    public Pagos findPagosById(Integer id)
    {
        return pagosRepository.findPagosById(id);
    }


    @Transactional
    public Pagos createPagos(Pagos p)
    {
        validarpagos(p);
        Pagos pagoCreado = pagosRepository.createPagos(p);
        
        if (pagoCreado.getEstado() != null && 
            pagoCreado.getEstado().equalsIgnoreCase("PAGADA")) {
            
            Factura factura = pagoCreado.getFactura();
            if (factura != null && !factura.getEstado().equalsIgnoreCase("PAGADA")) {
                factura.setEstado("PAGADA");
                facturaService.updateFactura(factura);
            }
        }
        
        return pagoCreado;
    }

    public Pagos upLocalDatePagos(Pagos p)
    {
        validarpagos(p);
        return  pagosRepository.upLocalDatePagos(p);
    }

    public void deletePagos(int id)
    {
        pagosRepository.deletebyid(id);
    }

    private void validarpagos(Pagos pago) {
        if (pago.getFactura() == null) {
            throw new IllegalArgumentException("El pago debe estar asociado a una factura.");
        }

        if (pago.getImporte() <= 0) {
            throw new IllegalArgumentException("El importe del pago debe ser mayor que 0.");
        }

        Factura factura = pago.getFactura();
        if (factura != null) {
            List<Pagos> pagosExistentes = pagosRepository.findPagosByFacturaId(factura.getId_factura());
            Integer pagoIdActual = pago.getId_pago();
            double totalPagado = pagosExistentes.stream()
                .filter(p -> p.getEstado() != null && 
                            p.getEstado().equalsIgnoreCase("PAGADA") &&
                            (pagoIdActual == null || !p.getId_pago().equals(pagoIdActual)))
                .mapToDouble(Pagos::getImporte)
                .sum();
            
            double totalPendiente = factura.getTotal() - totalPagado;
            
            if (pago.getImporte() > totalPendiente) {
                throw new IllegalArgumentException(
                    String.format("El importe del pago (%.2f) excede el total pendiente de la factura (%.2f)", 
                        pago.getImporte(), totalPendiente)
                );
            }
        }

        if (pago.getMetodo_pago() != null) {
            String metodo = pago.getMetodo_pago().toLowerCase();
            if (!metodo.equals("transferencia") && !metodo.equals("tarjeta") && !metodo.equals("efectivo")) {
                throw new IllegalArgumentException("Método de pago inválido: " + pago.getMetodo_pago());
            }
        }

        if (pago.getEstado() != null) {
            String estado = pago.getEstado().toUpperCase().trim();
            if (!estado.equals("PENDIENTE") && !estado.equals("PAGADA") && !estado.equals("CANCELADA")) {
                throw new IllegalArgumentException("Estado inválido: " + pago.getEstado() + 
                    ". Estados válidos: PENDIENTE, PAGADA, CANCELADA");
            }
        }

        LocalDate hoy = LocalDate.now();
        if (pago.getFecha_pago().isAfter(hoy)) {
            throw new IllegalArgumentException("La fecha de pago no puede ser futura.");
        }
    }
}
