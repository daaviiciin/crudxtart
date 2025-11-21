package com.example.crudxtart.service;

import com.example.crudxtart.models.Pagos;
import com.example.crudxtart.repository.PagosRepository;
import jakarta.inject.Inject;

import java.util.Date;
import java.util.List;

public class PagosService
{
    @Inject
    PagosRepository pagosRepository;

    public List<Pagos> findAllPagos()
    {
        return pagosRepository.findAllPagos();
    }

    public Pagos findPagosById(int id)
    {
        return pagosRepository.findPagosById(id);
    }

    public Pagos createPagos(Pagos p)
    {
        validarpagos(p);
        return  pagosRepository.createPagos(p);
    }

    public Pagos updatePagos(Pagos p)
    {
        validarpagos(p);
        return  pagosRepository.updatePagos(p);
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

        if (pago.getMetodo_pago() != null) {
            String metodo = pago.getMetodo_pago().toLowerCase();
            if (!metodo.equals("transferencia") && !metodo.equals("tarjeta") && !metodo.equals("efectivo")) {
                throw new IllegalArgumentException("Método de pago inválido: " + pago.getMetodo_pago());
            }
        }

        if (pago.getEstado() != null) {
            String estado = pago.getEstado().toLowerCase();
            if (!estado.equals("pendiente") && !estado.equals("confirmado") && !estado.equals("fallido")) {
                throw new IllegalArgumentException("Estado inválido: " + pago.getEstado());
            }
        }

        Date hoy = new Date();
        if (pago.getFecha_pago() != null && !hoy.before(pago.getFecha_pago())) {
            throw new IllegalArgumentException("La fecha de pago no puede ser futura.");
        }
    }
}
