package com.example.crudxtart.test;

import com.example.crudxtart.models.FacturaProducto;
import com.example.crudxtart.service.FacturaProductoService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class FacturaProductoTest {

    @Inject
    private FacturaProductoService facturaProductoService;

    public FacturaProductoTest() {}

    public void testFacturaProductoRepository() {
        List<FacturaProducto> lista = facturaProductoService.findAllFacturaProductos();
        lista.forEach(fp -> {
            System.out.println(
                    "ID: " + fp.getId_factura_producto() +
                            " | Cantidad: " + fp.getCantidad() +
                            " | Precio unitario: " + fp.getOrecio_unitario()
            );
        });
    }
}
