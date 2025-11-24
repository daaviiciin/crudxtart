package com.example.crudxtart.repository;

import com.example.crudxtart.models.Cliente;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class ClienteRepositoryImpl implements ClienteRepository {

    @Inject
    EntityManager em;

    public ClienteRepositoryImpl() {}

    @Override
    public List<Cliente> findAllClientes() {
        return em.createQuery("SELECT c FROM Cliente c", Cliente.class).getResultList();
    }

    @Override
    public Cliente findClienteById(int id) {
        Cliente c = em.find(Cliente.class, id);
        return c;
    }

    @Override
    @Transactional
    public Cliente createCliente(Cliente c) {
        em.persist(c);
        return c;
    }

    @Override
    @Transactional
    public void saveCliente(Cliente c) {
        em.merge(c);
    }

    @Override
    @Transactional
    public void deletebyid(int id) {
        Cliente c = em.find(Cliente.class, id);
        if (c != null) {
            em.remove(c);
        }
        return;
    }

    @Override
    @Transactional
    public Cliente updateCliente(Cliente c) {
        return em.merge(c);
    }
}

