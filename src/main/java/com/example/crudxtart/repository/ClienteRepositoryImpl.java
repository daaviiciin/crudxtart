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
    public List<Cliente> findAllClientes()
    {


        return em.createQuery("SELECT c FROM Cliente c", Cliente.class).getResultList();
    }

    @Override
    public Cliente findClienteById(Integer id)
    {
        em.getTransaction().begin();
        Cliente c = em.find(Cliente.class, id);
        em.getTransaction().commit();
        return c;
    }

    @Override
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
    public void saveCliente(Cliente c) {
        em.merge(c);
    }

    @Override
    public void deletebyid(Integer id) {
        try {
            Cliente c = em.find(Cliente.class, id);
            if (c != null)
            {
                em.getTransaction().begin();
                em.remove(c);
                em.getTransaction().commit();
            }
        }catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
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

