package com.example.crudxtart.test;

import com.example.crudxtart.models.Pagos;
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
        System.out.println("LISTAR PAGOS INICIALES");
        List<Pagos> lista = pagosService.findAllPagos();
        lista.forEach(this::imprimirPago);

        System.out.println("CREAR PAGO");
        Pagos nuevo = new Pagos();
        nuevo.setImporte(100.0);
        nuevo.setMetodo_pago("transferencia");
        nuevo.setEstado("pendiente");
        nuevo.setFecha_pago(new Date());
        Pagos creado = pagosService.createPagos(nuevo);
        imprimirPago(creado);

        System.out.println("ACTUALIZAR PAGO");
        creado.setEstado("confirmado");
        Pagos actualizado = pagosService.updatePagos(creado);
        imprimirPago(actualizado);

        System.out.println("BORRAR PAGO");
        pagosService.deletePagos(actualizado.getId_pago());

        System.out.println("LISTAR DESPUÉS DEL BORRADO");
        pagosService.findAllPagos().forEach(this::imprimirPago);
    }

    private void imprimirPago(Pagos p) {
        System.out.println(
                "ID: " + p.getId_pago() +
                        " | Factura: " + p.getFactura().getId_factura() +
                        " | Fecha: " + p.getFecha_pago() +
                        " | Importe: " + p.getImporte() +
                        " | Método: " + p.getMetodo_pago() +
                        " | Estado: " + p.getEstado()
        );
    }
}
