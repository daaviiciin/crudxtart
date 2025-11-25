package com.example.crudxtart.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

@ApplicationScoped
public class JpaFactory {

    private static final String PERSISTENCE_UNIT_NAME = "crm";
    private final EntityManagerFactory emf;

    public JpaFactory() {
        this.emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
    }

    @Produces
    @ApplicationScoped
    public EntityManagerFactory produceEntityManagerFactory() {
        return emf;
    }

    @Produces
    @RequestScoped
    public EntityManager produceEntityManager() {
        return emf.createEntityManager();
    }

    public void closeEntityManager(@Disposes EntityManager em) {
        if (em.isOpen()) {
            em.close();
        }
    }

    public void closeEntityManagerFactory() {
        if (emf.isOpen()) {
            emf.close();
        }
    }
}


