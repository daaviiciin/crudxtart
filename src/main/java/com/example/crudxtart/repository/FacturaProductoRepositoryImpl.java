package com.example.crudxtart.repository;

import java.util.List;

import com.example.crudxtart.models.FacturaProducto;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

@ApplicationScoped
public class FacturaProductoRepositoryImpl implements FacturaProductoRepository {

    @Inject
    EntityManager em;

    public FacturaProductoRepositoryImpl() {}

    @Override
    public List<FacturaProducto> findAllFacturaProductos() {
        return em.createQuery(
                "SELECT fp FROM FacturaProducto fp " +
                        "LEFT JOIN FETCH fp.Producto " +
                        "LEFT JOIN FETCH fp.factura",
                FacturaProducto.class
        ).getResultList();
    }

    @Override
    public FacturaProducto findFacturaProductoById(Integer id)
    {
        try
        {
            em.getTransaction().begin();
            FacturaProducto fp = em.find(FacturaProducto.class, id);
            em.getTransaction().commit();
            return fp;
        }
        catch(Exception ex)
        {
            em.getTransaction().rollback();
        }
        return null;
    }

    @Override

    public FacturaProducto createFacturaProducto(FacturaProducto fp)
    {
        try
        {
            em.getTransaction().begin();
            em.persist(fp);
            em.getTransaction().commit();
            return fp;
        }catch(Exception ex)
        {
            em.getTransaction().rollback();
        }
        return null;
    }

    @Override
    public void saveFacturaProducto(FacturaProducto fp)
    {
        try
        {
            em.getTransaction().begin();
            em.persist(fp);
            em.getTransaction().commit();
        }catch(Exception ex)
        {
            em.getTransaction().rollback();
        }
    }

    @Override

    public void deletebyid(Integer id) {
        try
        {
            em.getTransaction().begin();
            FacturaProducto fp = em.find(FacturaProducto.class, id);
            if (fp != null) {
                em.remove(fp);
            }
            em.getTransaction().commit();
        }catch(Exception ex)
        {
            em.getTransaction().rollback();
        }

    }

    @Override

    public FacturaProducto updateFacturaProducto(FacturaProducto fp)
    {
        try
        {
            em.getTransaction().begin();
            em.merge(fp);
            em.getTransaction().commit();
            return fp;
        }catch (Exception ex)
        {
            em.getTransaction().rollback();
        }
        return null;
    }
}
