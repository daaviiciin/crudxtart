package com.example.crudxtart.repository;

import com.example.crudxtart.models.Empleado;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class EmpleadoRepositoryImpl implements EmpleadoRepository {

    @Inject
    EntityManager em;

    public EmpleadoRepositoryImpl() {}

    @Override
    @Transactional
    public List<Empleado> findAllEmpleados() {
        return em.createQuery("SELECT e FROM Empleado e", Empleado.class).getResultList();
    }

    @Override
    public Empleado findEmpleadoById(Integer id) {
        Empleado e = em.find(Empleado.class, id);
        return e;
    }

    @Override
    @Transactional
    public Empleado createEmpleado(Empleado e) {
        em.persist(e);
        return e;
    }

    @Override
    @Transactional
    public void saveEmpleado(Empleado e) {
        em.merge(e);
    }

    @Override
    @Transactional
    public void deletebyid(Integer id) {
        Empleado e = em.find(Empleado.class, id);
        if (e != null) {
            em.remove(e);
        }
        return;
    }

    @Override
    @Transactional
    public Empleado upLocalDateEmpleado(Empleado e) {
        return em.merge(e);
    }
}
