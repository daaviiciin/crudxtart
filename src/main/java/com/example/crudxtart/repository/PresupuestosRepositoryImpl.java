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
    public Presupuestos findPresupuestoById(Integer id)
    {
        try {
            em.getTransaction().begin();
            Presupuestos p = em.find(Presupuestos.class, id);
            em.getTransaction().commit();
            return p;
        }catch (Exception ex)
        {
            em.getTransaction().rollback();
        }
        return null;
    }

    @Override

    public Presupuestos createPresupuesto(Presupuestos p)
    {
        try
        {
            em.getTransaction().begin();
            em.persist(p);
            em.getTransaction().commit();
            return p;
        }catch (Exception ex)
        {
            em.getTransaction().rollback();
        }
        return null;
    }

    @Override

    public void savePresupuesto(Presupuestos p)
    {
        try
        {
            em.getTransaction().begin();
            em.merge(p);
            em.getTransaction().commit();
        }catch (Exception ex)
        {
            em.getTransaction().rollback();
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
            em.getTransaction().rollback();
        }
    }

    @Override
    public Presupuestos updatePresupuesto(Presupuestos p)
    {
        try
        {
            em.getTransaction().begin();
            em.merge(p);
            em.getTransaction().commit();
            return p;
        }catch (Exception ex)
        {
            em.getTransaction().rollback();
        }
        return null;
    }
}
