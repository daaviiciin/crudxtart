package com.example.crudxtart.test;

import com.example.crudxtart.service.Roles_empleadoService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;


@ApplicationScoped
public class Roles_empleadoTest {

    @Inject
    private Roles_empleadoService roles_empleadoService;

    public Roles_empleadoTest() {}

    public void testRoles_empleadoRepository() {

        roles_empleadoService.deleteRol(7);
    }
}
