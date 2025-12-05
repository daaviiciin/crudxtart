package com.example.crudxtart.repository;

import java.util.List;

import com.example.crudxtart.models.Presupuestos;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class PresupuestosRepositoryImpl implements PresupuestosRepository {

    @Inject
    EntityManager em;

    public PresupuestosRepositoryImpl() {}

    @Override
    public List<Presupuestos> findAllPresupuestos() {
        return em.createQuery(
                "SELECT DISTINCT p FROM Presupuestos p " +
                        "LEFT JOIN FETCH p.empleado " +
                        "LEFT JOIN FETCH p.cliente_pagador " +
                        "LEFT JOIN FETCH p.cliente_beneficiario " +
                        "LEFT JOIN FETCH p.presupuestoProductos pp " +
                        "LEFT JOIN FETCH pp.producto " +
                        "LEFT JOIN FETCH pp.cliente_beneficiario ",
                Presupuestos.class
        ).getResultList();
    }


    @Override
    public Presupuestos findPresupuestoById(Integer id) {
        try {
            return em.createQuery(
                            "SELECT DISTINCT p FROM Presupuestos p " +
                                    "LEFT JOIN FETCH p.empleado " +
                                    "LEFT JOIN FETCH p.cliente_pagador " +
                                    "LEFT JOIN FETCH p.cliente_beneficiario " +
                                    "LEFT JOIN FETCH p.presupuestoProductos pp " +
                                    "LEFT JOIN FETCH pp.producto " +
                                    "LEFT JOIN FETCH pp.cliente_beneficiario " +
                                    "WHERE p.id_Presupuesto = :id",
                            Presupuestos.class
                    )
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }


    @Override
    public Presupuestos createPresupuesto(Presupuestos p)
    {
        try
        {
            em.getTransaction().begin();
            em.persist(p);
            em.flush();
            em.getTransaction().commit();
            return p;
        }catch (Exception ex)
        {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            ex.printStackTrace();
            throw new RuntimeException("Error al crear presupuesto: " + ex.getMessage(), ex);
        }
    }

    @Override
    public void savePresupuesto(Presupuestos p)
    {
        try
        {
            em.getTransaction().begin();
            em.merge(p);
            em.flush();
            em.getTransaction().commit();
        }catch (Exception ex)
        {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            ex.printStackTrace();
            throw new RuntimeException("Error al crear presupuesto: " + ex.getMessage(), ex);
        }
    }

    @Override
    public void deletebyid(Integer id)
    {
        try
        {
            em.getTransaction().begin();
            Presupuestos p = em.find(Presupuestos.class, id);
            if (p != null) {
                em.remove(p);
            }
            em.getTransaction().commit();
        }catch (Exception ex)
        {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            ex.printStackTrace();
            throw new RuntimeException("Error al eliminar presupuesto: " + ex.getMessage(), ex);
        }
    }

    @Override
    public Presupuestos updatePresupuesto(Presupuestos p)
    {
        try
        {
            em.getTransaction().begin();
            Presupuestos actualizado = em.merge(p);
            em.flush(); // Forzar sincronización con la BD
            em.refresh(actualizado); // Refrescar para obtener valores actualizados (como fecha_cierre automática)
            em.getTransaction().commit();
            return actualizado;
        }catch (Exception ex)
        {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            ex.printStackTrace();
            throw new RuntimeException("Error al actualizar presupuesto: " + ex.getMessage(), ex);
        }
    }
}
