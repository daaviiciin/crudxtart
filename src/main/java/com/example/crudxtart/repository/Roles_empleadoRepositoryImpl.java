package com.example.crudxtart.repository;

import com.example.crudxtart.models.Roles_empleado;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class Roles_empleadoRepositoryImpl implements Roles_empleadoRepository {

    @Inject
    EntityManager em;

    public Roles_empleadoRepositoryImpl() {}

    @Override
    public List<Roles_empleado> findAllRoles_empleado() {
        return em.createQuery("SELECT r FROM Roles_empleado r", Roles_empleado.class)
                .getResultList();
    }

    @Override
    public Roles_empleado findRolById(int id) {
        Roles_empleado r = em.find(Roles_empleado.class, id);
        return r;
    }

    @Override
    @Transactional
    public Roles_empleado createRol(Roles_empleado r) {
        em.persist(r);
        return r;
    }

    @Override
    @Transactional
    public void saveRol(Roles_empleado r) {
        em.merge(r);
    }

    @Override
    @Transactional
    public void deletebyid(int id) {
        Roles_empleado r = em.find(Roles_empleado.class, id);
        if (r != null) {
            em.remove(r);
        }
        return;
    }

    @Override
    @Transactional
    public Roles_empleado updateRol(Roles_empleado r) {
        return em.merge(r);
    }
}
