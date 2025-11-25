package com.example.crudxtart.repository;

import com.example.crudxtart.models.Producto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class ProductoRepositoryImpl implements ProductoRepository {

    @Inject
    EntityManager em;

    public ProductoRepositoryImpl() {}

    @Override
    public List<Producto> findAllProductos() {
        return em.createQuery("SELECT p FROM Producto p", Producto.class)
                .getResultList();
    }

    @Override
    public Producto findProductoById(Integer id)
    {
        try
        {
            em.getTransaction().begin();
            Producto p = em.find(Producto.class, id);
            em.getTransaction().commit();
            return p;

        }catch (Exception ex)
        {
            em.getTransaction().rollback();
        }
        return null;
    }

    @Override
    public Producto createProducto(Producto p)
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
    public void saveProducto(Producto p)
    {
        try
        {
            em.getTransaction().begin();
            em.persist(p);
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
            Producto p = em.find(Producto.class, id);
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
    public Producto updateProducto(Producto p)
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
