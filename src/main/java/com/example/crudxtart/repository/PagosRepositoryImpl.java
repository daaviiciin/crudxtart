package com.example.crudxtart.repository;

import com.example.crudxtart.models.Pagos;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class PagosRepositoryImpl implements PagosRepository
{
    @Inject
    EntityManager em;

    public PagosRepositoryImpl() {}

    @Override
    public List<Pagos> findAllPagos()
    {
        return em.createQuery("SELECT p FROM Pagos p", Pagos.class).getResultList();
    }

    @Override
    public Pagos findPagosById(Integer id)
    {
        Pagos p =em.find(Pagos.class, id);
        return p;
    }

    @Override
    public Pagos createPagos(Pagos p)
    {
        try
        {
            em.getTransaction().begin();
            em.persist(p);
            em.getTransaction().commit();

        }catch(Exception ex)
        {
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
