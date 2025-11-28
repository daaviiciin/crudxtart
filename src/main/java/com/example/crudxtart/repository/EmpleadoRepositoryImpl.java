package com.example.crudxtart.repository;

import java.util.List;
import com.example.crudxtart.models.Empleado;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class EmpleadoRepositoryImpl implements EmpleadoRepository {

    @Inject
    EntityManager em;

    public EmpleadoRepositoryImpl() {}

    @Override
    @Transactional
    public List<Empleado> findAllEmpleados() {
        // Usar JOIN FETCH para cargar la relación id_rol y evitar proxies lazy
        return em.createQuery(
                "SELECT e FROM Empleado e LEFT JOIN FETCH e.id_rol",
                        Empleado.class)
                .getResultList();
    }

    @Override
    public Empleado findEmpleadoById(Integer id)
    {
        try {
            em.getTransaction().begin();
           // Usar JOIN FETCH para cargar la relación id_rol
            Empleado e = em.createQuery(
                    "SELECT e FROM Empleado e LEFT JOIN FETCH e.id_rol WHERE e.id_empleado = :id",
                    Empleado.class)
                    .setParameter("id", id)
                    .getSingleResult();
            em.getTransaction().commit();
            return e;
        } catch (jakarta.persistence.NoResultException ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            return null;
        }catch (Exception ex)
        {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        }
        return null;
    }

    @Override
    public Empleado findEmpleadoByEmail(String email)
    {
        try {
            // Usar JOIN FETCH para cargar la relación id_rol
            return em.createQuery(
                    "SELECT e FROM Empleado e LEFT JOIN FETCH e.id_rol WHERE e.email = :email", 
                    Empleado.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (Exception ex) {
            return null;
        }
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
