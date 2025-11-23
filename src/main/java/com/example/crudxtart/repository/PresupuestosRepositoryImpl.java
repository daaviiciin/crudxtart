package com.example.crudxtart.repository;

import com.example.crudxtart.models.Presupuestos;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class PresupuestosRepositoryImpl implements PresupuestosRepository {

    @Inject
    EntityManager em;

    public PresupuestosRepositoryImpl() {}

    @Override
    public List<Presupuestos> findAllPresupuestos() {
        return em.createQuery("SELECT p FROM Presupuestos p", Presupuestos.class)
                .getResultList();
    }

    @Override
    public Presupuestos findPresupuestoById(int id) {
        Presupuestos p = em.find(Presupuestos.class, id);
        return p;
    }

    @Override
    @Transactional
    public Presupuestos createPresupuesto(Presupuestos p) {
        em.persist(p);
        return p;
    }

    @Override
    @Transactional
    public void savePresupuesto(Presupuestos p) {
        em.merge(p);
    }

    @Override
    @Transactional
    public void deletebyid(int id) {
        Presupuestos p = em.find(Presupuestos.class, id);
        if (p != null) {
            em.remove(p);
        }
        return;
    }

    @Override
    @Transactional
    public Presupuestos updatePresupuesto(Presupuestos p) {
        return em.merge(p);
    }
}
