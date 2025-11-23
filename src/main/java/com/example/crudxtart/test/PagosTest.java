package com.example.crudxtart.test;

import com.example.crudxtart.models.Pagos;
import com.example.crudxtart.models.Factura;
import com.example.crudxtart.service.PagosService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Date;
import java.util.List;

@ApplicationScoped
public class PagosTest {

    @Inject
    private PagosService pagosService;

    public PagosTest() {
    }

    public void testPagosRepository() {

        // 1. LISTA INICIAL
        System.out.println("===== LISTA INICIAL DE PAGOS =====");
        List<Pagos> lista = pagosService.findAllPagos();
        lista.forEach(p -> {
            System.out.println(
                    "ID: " + p.getId_pago() +
                            " | Factura: " + (p.getFactura() != null ? p.getFactura().getId_factura() : "null") +
                            " | Fecha: " + p.getFecha_pago() +
                            " | Importe: " + p.getImporte() +
                            " | Método: " + p.getMetodo_pago() +
                            " | Estado: " + p.getEstado()
            );
        });

        System.out.println("===== CREAR NUEVO PAGO =====");
        Pagos nuevo = new Pagos();

        Factura factura = new Factura();
        factura.setId_factura(1);
        nuevo.setFactura(factura);

        nuevo.setFecha_pago(new Date());
        nuevo.setImporte(123.45);
        nuevo.setMetodo_pago("transferencia");
        nuevo.setEstado("pendiente");

        nuevo = pagosService.createPagos(nuevo);
        System.out.println(">>> Creado pago con ID: " + nuevo.getId_pago());

        System.out.println("===== BORRAR PAGO CREADO =====");
        int idBorrar = nuevo.getId_pago();
        pagosService.deletePagos(idBorrar);
        System.out.println(">>> Borrado pago con ID: " + idBorrar);

        System.out.println("===== LISTA FINAL DE PAGOS =====");
        List<Pagos> listaFinal = pagosService.findAllPagos();
        listaFinal.forEach(p -> {
            System.out.println(
                    "ID: " + p.getId_pago() +
                            " | Factura: " + (p.getFactura() != null ? p.getFactura().getId_factura() : "null") +
                            " | Fecha: " + p.getFecha_pago() +
                            " | Importe: " + p.getImporte() +
                            " | Método: " + p.getMetodo_pago() +
                            " | Estado: " + p.getEstado()
            );
        });
    }
}
