package com.example.crudxtart.repository;

import java.util.List;

import com.example.crudxtart.models.Factura;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

@ApplicationScoped
public class FacturaRepositoryImpl implements FacturaRepository {

    @Inject
    EntityManager em;

    public FacturaRepositoryImpl() {}

    @Override
    public List<Factura> findAllFacturas() {
        return em.createQuery(
                "SELECT f FROM Factura f " +
                        "LEFT JOIN FETCH f.cliente_pagador " +
                        "LEFT JOIN FETCH f.empleado",
                Factura.class
        ).getResultList();
    }

    @Override
    public Factura findFacturaById(Integer id)
    {
        try
        {
            return em.createQuery(
                            "SELECT f FROM Factura f " +
                                    "LEFT JOIN FETCH f.cliente_pagador " +
                                    "LEFT JOIN FETCH f.empleado " +
                                    "WHERE f.id_factura = :id",
                            Factura.class
                    )
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override

    public Factura createFactura(Factura f)
    {
        try
        {
            em.getTransaction().begin();
            em.persist(f);
            em.flush();
            em.getTransaction().commit();
            return f;
        }catch(Exception ex)
        {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            ex.printStackTrace();
            throw new RuntimeException("Error al crear la factura: " + ex.getMessage(), ex);
        }
    }

    @Override

    public void saveFactura(Factura f)
    {
        try
        {
            em.getTransaction().begin();
            em.persist(f);
            em.flush();
            em.getTransaction().commit();
        }catch (Exception ex)
        {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            ex.printStackTrace();
            throw new RuntimeException("Error al guardar la factura: " + ex.getMessage(), ex);
        }
    }

    @Override
    public void deletebyid(Integer id) {

        try
        {
            em.getTransaction().begin();
            Factura f = em.find(Factura.class, id);
            if (f != null) {
                em.remove(f);
            }
            em.getTransaction().commit();

        }catch (Exception ex)
        {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            ex.printStackTrace();
            throw new RuntimeException("Error al borrar la factura: " + ex.getMessage(), ex);
        }
    }

    @Override
    public Factura updateFactura(Factura f)
    {
        try
        {
            em.getTransaction().begin();
            Factura actualizado = em.merge(f);
            em.flush();
            em.refresh(actualizado);
            em.getTransaction().commit();
            return f;
        }catch(Exception ex)
        {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            ex.printStackTrace();
            throw new RuntimeException("Error al actualizar la factura: " + ex.getMessage(), ex);
        }

    }
    
    @Override
    public Long getSiguienteNumeroSecuencia() {
        try {
            Long count = em.createQuery(
                "SELECT COUNT(f) FROM Factura f",
                Long.class
            ).getSingleResult();
            return count + 1;
        } catch (Exception ex) {
            ex.printStackTrace();
            return 1L;
        }
    }
    
    @Override
    public List<Factura> findFacturasByPresupuestoId(Integer presupuestoId) {
        try {
            return em.createQuery(
                "SELECT f FROM Factura f WHERE f.notas LIKE :pattern",
                Factura.class
            )
            .setParameter("pattern", "%presupuesto #" + presupuestoId + "%")
            .getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
            return new java.util.ArrayList<>();
        }
    }
}
