package com.example.crudxtart.repository;

import com.example.crudxtart.models.Factura;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class FacturaRepositoryImpl implements FacturaRepository {

    @Inject
    EntityManager em;

    public FacturaRepositoryImpl() {}

    @Override
    public List<Factura> findAllFacturas() {
        return em.createQuery("SELECT f FROM Factura f", Factura.class).getResultList();
    }

    @Override
    public Factura findFacturaById(int id) {
        Factura f = em.find(Factura.class, id);
        return f;
    }

    @Override
    @Transactional
    public Factura createFactura(Factura f) {
        em.persist(f);
        return f;
    }

    @Override
    @Transactional
    public void saveFactura(Factura f) {
        em.merge(f);
    }

    @Override
    @Transactional
    public void deletebyid(int id) {
        Factura f = em.find(Factura.class, id);
        if (f != null) {
            em.remove(f);
        }
        return;
    }

    @Override
    @Transactional
    public Factura updateFactura(Factura f) {
        return em.merge(f);
    }
}
