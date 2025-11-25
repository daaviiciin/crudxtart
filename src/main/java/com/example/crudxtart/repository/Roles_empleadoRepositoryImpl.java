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
    public Roles_empleado findRolById(Integer id)
    {
        try
        {
            em.getTransaction().begin();
            Roles_empleado r = em.find(Roles_empleado.class, id);
            em.getTransaction().commit();
            return r;
        }catch (Exception ex)
        {
            em.getTransaction().rollback();
        }
        return null;
    }

    @Override
    public Roles_empleado createRol(Roles_empleado r)
    {
        try
        {
            em.getTransaction().begin();
            em.persist(r);
            em.getTransaction().commit();
            return r;
        }catch (Exception ex)
        {
            em.getTransaction().rollback();
        }
        return null;
    }

    @Override
    public void saveRol(Roles_empleado r)
    {
        try
        {
            em.getTransaction().begin();
            em.persist(r);
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
            Roles_empleado r = em.find(Roles_empleado.class, id);
            if (r != null) {
                em.remove(r);
            }
            em.getTransaction().commit();
        }catch (Exception ex)
        {
            em.getTransaction().rollback();
        }

    }

    @Override

    public Roles_empleado updateRol(Roles_empleado r)
    {
        try
        {
            em.getTransaction().begin();
            em.merge(r);
            em.getTransaction().commit();
            return r;
        }catch (Exception ex)
        {
            em.getTransaction().rollback();
        }
        return null;
    }
}
