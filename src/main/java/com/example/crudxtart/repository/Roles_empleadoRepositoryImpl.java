package com.example.crudxtart.repository;

import java.util.List;

import com.example.crudxtart.models.Roles_empleado;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

@ApplicationScoped
public class Roles_empleadoRepositoryImpl implements Roles_empleadoRepository {

    @Inject
    EntityManager em;

    public Roles_empleadoRepositoryImpl() {}

    @Override
    public List<Roles_empleado> findAllRoles_empleado() {
        java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Roles_empleadoRepositoryImpl.class.getName());
        logger.info("findAllRoles_empleado() - Inicio");
        try {
            List<Roles_empleado> roles = em.createQuery("SELECT r FROM Roles_empleado r", Roles_empleado.class)
                    .getResultList();
            logger.info("findAllRoles_empleado() - Encontrados " + roles.size() + " roles");
            return roles;
        } catch (Exception ex) {
            logger.severe("findAllRoles_empleado() - Error: " + ex.getMessage());
            ex.printStackTrace();
            throw new RuntimeException("Error al buscar roles: " + ex.getMessage(), ex);
        }
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
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            ex.printStackTrace();
            throw new RuntimeException("Error al busacar un rol: " + ex.getMessage(), ex);
        }
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
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            ex.printStackTrace();
            throw new RuntimeException("Error al crear un rol: " + ex.getMessage(), ex);
        }
    }

    @Override
    public void saveRol(Roles_empleado r)
    {
        try
        {
            em.getTransaction().begin();
            em.persist(r);
            em.flush();
            em.getTransaction().commit();
        }catch (Exception ex)
        {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            ex.printStackTrace();
            throw new RuntimeException("Error al guardar un rol : " + ex.getMessage(), ex);
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
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            ex.printStackTrace();
            throw new RuntimeException("Error al borrar un rol : " + ex.getMessage(), ex);
        }

    }

    @Override

    public Roles_empleado updateRol(Roles_empleado r)
    {
        try
        {
            em.getTransaction().begin();
            Roles_empleado actualizado = em.merge(r);
            em.flush();
            em.refresh(actualizado);
            em.getTransaction().commit();
            return r;
        }catch (Exception ex)
        {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            ex.printStackTrace();
            throw new RuntimeException("Error al actualizar un rol : " + ex.getMessage(), ex);
        }
    }
}
