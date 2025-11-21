package com.example.crudxtart.test;

import com.example.crudxtart.models.Factura;
import com.example.crudxtart.models.Pagos;
import com.example.crudxtart.repository.PagosRepositoryImpl;
import com.example.crudxtart.service.PagosService;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
@ApplicationScoped

public class PagosTest
{
    @Inject
    private PagosService pagosService;

    public PagosTest () {
    }

    public void testPagosRepository()
    {
        List<Pagos> lista = pagosService.findAllPagos();
        lista.forEach(p -> {
            System.out.println(
                    "ID: " + p.getId_pago() +
                            " | Factura: " + p.getFactura().getId_factura()+
                            " | Fecha: " + p.getFecha_pago() +
                            " | Importe: " + p.getImporte() +
                            " | Método: " + p.getMetodo_pago() +
                            " | Estado: " + p.getEstado()
            );
        });

        Pagos pagos = new Pagos();
        Factura factura = new Factura();
        pagos.setFactura(factura);
        pagos.setImporte(15.0);
        pagos.setMetodo_pago("Efectivo");
        pagos.setEstado("Pendiente");

        Pagos creado = pagosService.createPagos(pagos);
        creado.setEstado("Confirmado");
        Pagos actualizado = pagosService.updatePagos(creado);
        pagosService.deletePagos(actualizado.getId_pago());
    }
}