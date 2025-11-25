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
    public Empleado findEmpleadoById(Integer id)
    {
       try {
           em.getTransaction().begin();
           Empleado e = em.find(Empleado.class, id);
           em.getTransaction().commit();
           return e;
       }catch (Exception ex)
       {
           em.getTransaction().rollback();
       }
       return null;
    }

    @Override
    @Transactional
    public Empleado createEmpleado(Empleado e)
    {
       try {
           em.getTransaction().begin();
           em.persist(e);
           em.getTransaction().commit();
           return e;
       }catch (Exception ex)
       {
           em.getTransaction().rollback();
       }
       return null;
    }

    @Override
    @Transactional
    public void saveEmpleado(Empleado e) {
       try
       {
           em.getTransaction().begin();
           em.persist(e);
           em.getTransaction().commit();
       }catch (Exception ex)
       {
           em.getTransaction().rollback();
       }

    }

    @Override
    @Transactional
    public void deletebyid(Integer id) {
        try {

            Empleado e = em.find(Empleado.class, id);
            if (e != null)
            {
                em.getTransaction().begin();
                em.remove(e);
                em.getTransaction().commit();
            }
        }catch (Exception ex)
        {
            em.getTransaction().rollback();
        }
    }

    @Override
    @Transactional
    public Empleado updateEmpleado(Empleado e)
    {
        try
        {
            em.getTransaction().begin();
            em.merge(e);
            em.getTransaction().commit();
            return e;
        }catch (Exception ex)
        {
            em.getTransaction().rollback();
        }

        return null;

    }
}
