package com.example.crudxtart.service;

import com.example.crudxtart.models.FacturaProducto;
import com.example.crudxtart.repository.FacturaProductoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.logging.Logger;

@ApplicationScoped
public class FacturaProductoService {


    private static final Logger logger = Logger.getLogger(FacturaProductoService.class.getName());
    private static final String CODIGO_LOG = "SRVC-FPR-";

    @Inject
    FacturaProductoRepository facturaProductoRepository;

    public List<FacturaProducto> findAllFacturaProductos() {
        logger.info("[" + CODIGO_LOG + "001] findAllFacturaProductos() - Inicio"); // CAMBIO LOG
        return facturaProductoRepository.findAllFacturaProductos();
    }

    public FacturaProducto findFacturaProductoById(Integer id) {
        logger.info("[" + CODIGO_LOG + "002] findFacturaProductoById() - Inicio"); // CAMBIO LOG
        return facturaProductoRepository.findFacturaProductoById(id);
    }

    public FacturaProducto createFacturaProducto(FacturaProducto fp) {
        logger.info("[" + CODIGO_LOG + "003] createFacturaProducto() - Inicio"); // CAMBIO LOG
        validarFacturaProducto(fp);
        return facturaProductoRepository.createFacturaProducto(fp);
    }

    public FacturaProducto upLocalDateFacturaProducto(FacturaProducto fp) {
        logger.info("[" + CODIGO_LOG + "004] upLocalDateFacturaProducto() - Inicio"); // CAMBIO LOG
        validarFacturaProducto(fp);
        return facturaProductoRepository.updateFacturaProducto(fp);
    }

    public void deleteFacturaProducto(Integer id) {
        logger.info("[" + CODIGO_LOG + "005] deleteFacturaProducto() - Inicio"); // CAMBIO LOG
        facturaProductoRepository.deletebyid(id);
    }

    private void validarFacturaProducto(FacturaProducto fp) {

        // relaciones obligatorias
        if (fp.getCantidad() <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que 0.");
        }

        if (fp.getprecio_unitario() < 0) {
            throw new IllegalArgumentException("El precio unitario no puede ser negativo.");
        }
    }
}
