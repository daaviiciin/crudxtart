package com.example.crudxtart.service;

import com.example.crudxtart.models.FacturaProducto;
import com.example.crudxtart.repository.FacturaProductoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class FacturaProductoService {

    @Inject
    FacturaProductoRepository facturaProductoRepository;

    public List<FacturaProducto> findAllFacturaProductos() {
        return facturaProductoRepository.findAllFacturaProductos();
    }

    public FacturaProducto findFacturaProductoById(int id) {
        return facturaProductoRepository.findFacturaProductoById(id);
    }

    public FacturaProducto createFacturaProducto(FacturaProducto fp) {
        validarFacturaProducto(fp);
        return facturaProductoRepository.createFacturaProducto(fp);
    }

    public FacturaProducto updateFacturaProducto(FacturaProducto fp) {
        validarFacturaProducto(fp);
        return facturaProductoRepository.updateFacturaProducto(fp);
    }

    public void deleteFacturaProducto(int id) {
        facturaProductoRepository.deletebyid(id);
    }

    private void validarFacturaProducto(FacturaProducto fp) {

        // relaciones obligatorias
        if (fp.getCantidad() <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que 0.");
        }

        if (fp.getOrecio_unitario() < 0) {
            throw new IllegalArgumentException("El precio unitario no puede ser negativo.");
        }
    }
}
