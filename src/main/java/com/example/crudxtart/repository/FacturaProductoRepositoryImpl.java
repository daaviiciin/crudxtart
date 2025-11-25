package com.example.crudxtart.repository;

import com.example.crudxtart.models.FacturaProducto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class FacturaProductoRepositoryImpl implements FacturaProductoRepository {

    @Inject
    EntityManager em;

    public FacturaProductoRepositoryImpl() {}

    @Override
    public List<FacturaProducto> findAllFacturaProductos() {
        return em.createQuery("SELECT fp FROM FacturaProducto fp", FacturaProducto.class)
                .getResultList();
    }

    @Override
    public FacturaProducto findFacturaProductoById(Integer id) {
        FacturaProducto fp = em.find(FacturaProducto.class, id);
        return fp;
    }

    @Override
    @Transactional
    public FacturaProducto createFacturaProducto(FacturaProducto fp) {
        em.persist(fp);
        return fp;
    }

    @Override
    @Transactional
    public void saveFacturaProducto(FacturaProducto fp) {
        em.merge(fp);
    }

    @Override
    @Transactional
    public void deletebyid(Integer id) {
        FacturaProducto fp = em.find(FacturaProducto.class, id);
        if (fp != null) {
            em.remove(fp);
        }
        return;
    }

    @Override
    @Transactional
    public FacturaProducto upLocalDateFacturaProducto(FacturaProducto fp) {
        return em.merge(fp);
    }
}
