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
            em.flush(); // Forzar la generación del ID
            em.getTransaction().commit();
            return f;
        }catch(Exception ex)
        {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            ex.printStackTrace();
        }
        return null;
    }

    @Override

    public void saveFactura(Factura f)
    {
        try
        {
            em.getTransaction().begin();
            em.persist(f);
            em.getTransaction().commit();
        }catch (Exception ex)
        {
            em.getTransaction().rollback();
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
            em.getTransaction().rollback();
        }
    }

    @Override
    public Factura updateFactura(Factura f)
    {
        try
        {
            em.getTransaction().begin();
            em.merge(f);
            em.getTransaction().commit();
            return f;
        }catch(Exception ex)
        {
            em.getTransaction().rollback();
        }
        return null;
    }
}
