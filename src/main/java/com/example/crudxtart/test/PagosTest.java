package com.example.crudxtart.test;

import com.example.crudxtart.models.Pagos;
import com.example.crudxtart.models.Factura;
import com.example.crudxtart.service.PagosService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Date;
import java.util.List;

@ApplicationScoped
public class PagosTest
{
    @Inject
    private PagosService pagosService;

    public PagosTest () {}

    public void testPagosRepository()
    {
        // LISTAR PAGOS EXISTENTES
        System.out.println("========== LISTA INICIAL DE PAGOS ==========");
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

        // CREAR NUEVO PAGO (SE QUEDA EN BD, SIN DELETE)
        System.out.println("========== CREANDO NUEVO PAGO ==========");
        Pagos pagos = new Pagos();

        // Necesitas una factura que exista en la BD (por ejemplo, id 1)
        Factura factura = new Factura();

        pagos.setFactura(factura);
        pagos.setImporte(50.0);
        pagos.setMetodo_pago("efectivo");   // válido según tu validación
        pagos.setEstado("pendiente");       // válido según tu validación
        pagos.setFecha_pago(new Date());    // hoy

        Pagos creado = pagosService.createPagos(pagos);
        System.out.println("Pago creado -> ID: " + creado.getId_pago()
                + " | Factura: " + creado.getFactura().getId_factura()
                + " | Importe: " + creado.getImporte()
                + " | Estado: " + creado.getEstado());
    }
}
