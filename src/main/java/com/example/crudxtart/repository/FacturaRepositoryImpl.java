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
    public Factura findFacturaById(Integer id)
    {
       try
       {
           em.getTransaction().begin();
           Factura f = em.find(Factura.class, id);
           em.getTransaction().commit();
           return f;
       }catch(Exception ex)
       {
           em.getTransaction().rollback();
       }
       return null;
    }

    @Override

    public Factura createFactura(Factura f)
    {
        try
        {
            em.getTransaction().begin();
            em.persist(f);
            em.getTransaction().commit();
            return f;
        }catch(Exception ex)
        {
            em.getTransaction().rollback();
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
