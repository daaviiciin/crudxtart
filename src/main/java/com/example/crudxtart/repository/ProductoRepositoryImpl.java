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
    public Producto findProductoById(Integer id) {
        Producto p = em.find(Producto.class, id);
        return p;
    }

    @Override
    @Transactional
    public Producto createProducto(Producto p) {
        em.persist(p);
        return p;
    }

    @Override
    @Transactional
    public void saveProducto(Producto p) {
        em.merge(p);
    }

    @Override
    @Transactional
    public void deletebyid(Integer id) {
        Producto p = em.find(Producto.class, id);
        if (p != null) {
            em.remove(p);
        }
        return;
    }

    @Override
    @Transactional
    public Producto upLocalDateProducto(Producto p) {
        return em.merge(p);
    }
}
