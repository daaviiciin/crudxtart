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
    public Pagos findPagosById(int id)
    {
        Pagos p =em.find(Pagos.class, id);
        return p;
    }

    @Override
    @Transactional
    public Pagos createPagos(Pagos p)
    {
        em.persist(p);
        return p;
    }

    @Override
    @Transactional
    public void savePagos(Pagos p)
    {
        em.merge(p);
    }

    @Override
    @Transactional
    public void deletebyid(int  id)
    {
        Pagos p =em.find(Pagos.class, id);
        if(p!=null)
        {
            em.remove(p);
        }
        return;
    }

    @Override
    @Transactional
    public Pagos updatePagos(Pagos p)
    {
        return em.merge(p);
    }

}
