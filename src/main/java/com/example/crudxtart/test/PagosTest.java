package com.example.crudxtart.test;

import com.example.crudxtart.models.Factura;
import com.example.crudxtart.models.Pagos;
import com.example.crudxtart.repository.FacturaRepository;
import com.example.crudxtart.repository.PagosRepositoryImpl;
import com.example.crudxtart.service.PagosService;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;


import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class PagosTest
{
    @Inject
    private PagosService pagosService;

    @Inject
    private FacturaRepository facturaRepository;

    public PagosTest () {}


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

        Factura factura = new Factura();
        factura.setId_factura(1);

        Pagos nuevoPago = new Pagos();
        nuevoPago.setFactura(factura);
        nuevoPago.setFecha_pago(LocalDate.of(2023,8,15));
        nuevoPago.setImporte(123.45);
        nuevoPago.setMetodo_pago("tarjeta");
        nuevoPago.setEstado("pendiente");

        Pagos pagoGuardado = pagosService.createPagos(nuevoPago);
        System.out.println("==== NUEVO PAGO CREADO ====");
        System.out.println("Nuevo ID: " + pagoGuardado.getId_pago());


        System.out.println("==== LISTA DE PAGOS DESPUÉS DE INSERTAR ====");
        List<Pagos> listaFinal = pagosService.findAllPagos();
        listaFinal.forEach(p -> {
            System.out.println(
                    "ID: " + p.getId_pago() +
                            " | Factura: " + p.getFactura().getId_factura() +
                            " | Fecha: " + p.getFecha_pago() +
                            " | Importe: " + p.getImporte() +
                            " | Método: " + p.getMetodo_pago() +
                            " | Estado: " + p.getEstado()
            );
   });

        Factura factura2 = new Factura();
        factura2.setId_factura(1);

        Pagos nuevoPago1 = new Pagos();
        nuevoPago1.setFactura(factura2);
        nuevoPago1.setFecha_pago(LocalDate.of(2022,4,04));
        nuevoPago1.setImporte(123.45);
        nuevoPago1.setMetodo_pago("tarjeta");
        nuevoPago1.setEstado("pendiente");

        Pagos pagoGuardado1 = pagosService.createPagos(nuevoPago1);
        System.out.println("==== NUEVO PAGO CREADO ====");
        System.out.println("Nuevo ID: " + pagoGuardado1.getId_pago());

        pagosService.deletePagos(9);




    }
}
