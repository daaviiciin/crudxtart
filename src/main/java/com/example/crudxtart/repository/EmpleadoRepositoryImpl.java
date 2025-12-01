package com.example.crudxtart.repository;

import com.example.crudxtart.models.Empleado;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class EmpleadoRepositoryImpl implements EmpleadoRepository {

    @Inject
    EntityManager em;

    public EmpleadoRepositoryImpl() {}

    // ============================================================
    // FIND ALL
    // ============================================================
    @Override
    @Transactional
    public List<Empleado> findAllEmpleados() {
        return em.createQuery(
                "SELECT e FROM Empleado e LEFT JOIN FETCH e.id_rol",
                Empleado.class
        ).getResultList();
    }

    // ============================================================
    // FIND BY ID
    // ============================================================
    @Override
    @Transactional
    public Empleado findEmpleadoById(Integer id) {
        try {
            return em.createQuery(
                            "SELECT e FROM Empleado e LEFT JOIN FETCH e.id_rol WHERE e.id_empleado = :id",
                            Empleado.class
                    )
                    .setParameter("id", id)
                    .getSingleResult();

        } catch (NoResultException ex) {
            return null;
        }
    }

    // ============================================================
    // FIND BY EMAIL
    // ============================================================
    @Override
    @Transactional
    public Empleado findEmpleadoByEmail(String email) {
        try {
            return em.createQuery(
                            "SELECT e FROM Empleado e LEFT JOIN FETCH e.id_rol WHERE e.email = :email",
                            Empleado.class
                    )
                    .setParameter("email", email)
                    .getSingleResult();

        } catch (NoResultException ex) {
            return null;
        }
    }

//    @Override
//    public List<Empleado> findCEmpleadoByFilters(String nombre, String email, String telefono) {
//        return List.of();
//    }

    // ============================================================
    // CREATE
    // ============================================================
    @Override
    public Empleado createEmpleado(Empleado e) {
        try {
            em.getTransaction().begin();
            em.persist(e);
            em.flush(); // Forzar flush para obtener el ID generado
            em.getTransaction().commit();
            return e;
        } catch (Exception ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            ex.printStackTrace();
            throw new RuntimeException("Error al crear empleado: " + ex.getMessage(), ex);
        }
    }

    // ============================================================
    // SAVE (alias persist)
    // ============================================================
    @Override
    @Transactional
    public void saveEmpleado(Empleado e) {
        try {
            em.persist(e);
        } catch (Exception ignored) {}
    }

    // ============================================================
    // DELETE
    // ============================================================
    @Override
    public void deletebyid(Integer id) {
        try {
            em.getTransaction().begin();
            Empleado e = em.find(Empleado.class, id);
            if (e != null) {
                em.remove(e);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            ex.printStackTrace();
            throw new RuntimeException("Error al eliminar empleado: " + ex.getMessage(), ex);
        }
    }

    // ============================================================
    // UPDATE
    // ============================================================
    @Override
    public Empleado updateEmpleado(Empleado e) {
        try {
            em.getTransaction().begin();
            Empleado actualizado = em.merge(e);
            em.getTransaction().commit();
            return actualizado;
        } catch (Exception ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            ex.printStackTrace();
            throw new RuntimeException("Error al actualizar empleado: " + ex.getMessage(), ex);
        }
    }
}
