package com.example.crudxtart.repository;

import com.example.crudxtart.models.Cliente;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class ClienteRepositoryImpl implements ClienteRepository {

    @Inject
    EntityManager em;

    public ClienteRepositoryImpl() {}

    // ============================================================
    // FIND ALL
    // ============================================================
    @Override
    public List<Cliente> findAllClientes() {
        return em.createQuery(
                "SELECT c FROM Cliente c LEFT JOIN FETCH c.empleado_responsable",
                Cliente.class
        ).getResultList();
    }


    // ============================================================
    // FIND BY ID
    // ============================================================
    @Override
    public Cliente findClienteById(Integer id) {
        try {
            return em.createQuery(
                            "SELECT c FROM Cliente c LEFT JOIN FETCH c.empleado_responsable WHERE c.id_cliente = :id",
                            Cliente.class
                    )
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }


    // ============================================================
    // FIND BY EMAIL
    // ============================================================
    @Override
    public Cliente findClienteByEmail(String email) {
        try {
            return em.createQuery(
                            "SELECT c FROM Cliente c LEFT JOIN FETCH c.empleado_responsable WHERE c.email = :email",
                            Cliente.class
                    )
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }


    // ============================================================
    // FIND WITH FILTERS
    // ============================================================
    @Override
    public List<Cliente> findClientesByFilters(String nombre, String email, String telefono) {
        try {
            StringBuilder sb = new StringBuilder(
                    "SELECT c FROM Cliente c LEFT JOIN FETCH c.empleado_responsable WHERE 1=1"
            );

            if (nombre != null && !nombre.trim().isEmpty()) {
                sb.append(" AND LOWER(c.nombre) LIKE LOWER(:nombre)");
            }
            if (email != null && !email.trim().isEmpty()) {
                sb.append(" AND LOWER(c.email) LIKE LOWER(:email)");
            }
            if (telefono != null && !telefono.trim().isEmpty()) {
                sb.append(" AND c.telefono LIKE :telefono");
            }

            TypedQuery<Cliente> q = em.createQuery(sb.toString(), Cliente.class);

            if (nombre != null && !nombre.trim().isEmpty()) {
                q.setParameter("nombre", "%" + nombre + "%");
            }
            if (email != null && !email.trim().isEmpty()) {
                q.setParameter("email", "%" + email + "%");
            }
            if (telefono != null && !telefono.trim().isEmpty()) {
                q.setParameter("telefono", "%" + telefono + "%");
            }

            return q.getResultList();

        } catch (Exception ex) {
            ex.printStackTrace();
            return new ArrayList<>();
        }
    }


    // ============================================================
    // CREATE
    // ============================================================
    @Override
    public Cliente createCliente(Cliente c) {
        try {
            em.getTransaction().begin();
            em.persist(c);
            em.flush();  // Forzar flush para obtener el ID generado
            em.getTransaction().commit();
            return c;
        } catch (Exception ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            ex.printStackTrace();
            throw new RuntimeException("Error al crear cliente: " + ex.getMessage(), ex);
        }
    }

    // ============================================================
    // SAVE
    // ============================================================
    @Override
    public void saveCliente(Cliente c) {
        try {
            em.getTransaction().begin();
            em.persist(c);
            em.flush();
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            ex.printStackTrace();
            throw new RuntimeException("Error al actualizar empleado: " + ex.getMessage(), ex);

        }
    }

    // ============================================================
    // DELETE
    // ============================================================
    @Override
    public void deletebyid(Integer id) {
        try {
            em.getTransaction().begin();
            Cliente c = em.find(Cliente.class, id);
            if (c != null) {
                em.remove(c);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            ex.printStackTrace();
            throw new RuntimeException("Error al eliminar cliente: " + ex.getMessage(), ex);
        }
    }

    // ============================================================
    // UPDATE
    // ============================================================
    @Override
    public Cliente upLocalDateCliente(Cliente c) {
        try {
            em.getTransaction().begin();
            Cliente actualizado = em.merge(c);
            em.getTransaction().commit();
            return actualizado;
        } catch (Exception ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            ex.printStackTrace();
            throw new RuntimeException("Error al actualizar cliente: " + ex.getMessage(), ex);
        }
    }
}
