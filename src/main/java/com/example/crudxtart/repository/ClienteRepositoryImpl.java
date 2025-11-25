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
    public Cliente findClienteById(Integer id) {
        Cliente c = em.find(Cliente.class, id);
        return c;
    }

    @Override
    @Transactional
    public Cliente createCliente(Cliente c)
    {
        try
        {
            em.getTransaction().begin();
            em.persist(c);
            em.getTransaction().commit();
        }
        catch (Exception ex)
        {
            em.getTransaction().rollback();
            ex.printStackTrace();
        }
        return c;
    }

    @Override
    @Transactional
    public void saveCliente(Cliente c) {
        em.merge(c);
    }

    @Override
    @Transactional
    public void deletebyid(Integer id) {
        try {
            Cliente c = em.find(Cliente.class, id);
            if (c != null) {
                em.remove(c);
            }
            return;
        }catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    @Transactional
    public Cliente upLocalDateCliente(Cliente c)
    {
        try {
            em.getTransaction().begin();
            em.merge(c);
            em.getTransaction().commit();
        }catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return c;
    }
}

