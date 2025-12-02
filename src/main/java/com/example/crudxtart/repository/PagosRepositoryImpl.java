package com.example.crudxtart.repository;

import java.util.List;

import com.example.crudxtart.models.Pagos;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class PagosRepositoryImpl implements PagosRepository
{
    @Inject
    EntityManager em;

    public PagosRepositoryImpl() {}

    @Override
    public List<Pagos> findAllPagos()
    {
        return em.createQuery(
                "SELECT p FROM Pagos p " +
                        "LEFT JOIN FETCH p.factura f " +
                        "LEFT JOIN FETCH f.cliente_pagador",
                Pagos.class
        ).getResultList();
    }

    @Override
    public Pagos findPagosById(Integer id)
    {
        try {
            return em.createQuery(
                            "SELECT p FROM Pagos p " +
                                    "LEFT JOIN FETCH p.factura f " +
                                    "LEFT JOIN FETCH f.cliente_pagador " +
                                    "WHERE p.id_pago = :id",
                            Pagos.class
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
    public List<Pagos> findPagosByFacturaId(Integer facturaId)
    {
        try {
            return em.createQuery(
                            "SELECT p FROM Pagos p " +
                                    "LEFT JOIN FETCH p.factura f " +
                                    "WHERE f.id_factura = :facturaId",
                            Pagos.class
                    )
                    .setParameter("facturaId", facturaId)
                    .getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
            return new java.util.ArrayList<>();
        }
    }

    @Override
    public Pagos createPagos(Pagos p)
    {
        try
        {
            em.getTransaction().begin();
            em.persist(p);
            em.flush(); // Forzar la generación del ID
            em.getTransaction().commit();

        }catch(Exception ex)
        {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            ex.printStackTrace();
        }
        return p;
    }

    @Override
    public void savePagos(Pagos p)
    {
        try {
            em.getTransaction().begin();
            em.persist(p);
            em.getTransaction().commit();
        }catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }

    @Override
    @Transactional
    public void deletebyid( Integer  id)
    {
        try
        {
            em.getTransaction().begin();
            Pagos p =em.find(Pagos.class, id);
            if(p!=null)
            {
                em.remove(p);
            }
            em.getTransaction().commit();
            return;
        }catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }

    @Override
    @Transactional
    public Pagos upLocalDatePagos(Pagos p)
    {
        try
        {
            em.getTransaction().begin();
            em.merge(p);
            em.getTransaction().commit();

        }catch(Exception ex)

        {
            ex.printStackTrace();
        }
        return p;
    }

}
