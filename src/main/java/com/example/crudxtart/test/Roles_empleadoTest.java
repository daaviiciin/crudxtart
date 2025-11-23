package com.example.crudxtart.test;

import com.example.crudxtart.models.Roles_empleado;
import com.example.crudxtart.service.Roles_empleadoService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class Roles_empleadoTest {

    @Inject
    private Roles_empleadoService roles_empleadoService;

    public Roles_empleadoTest() {}

    public void testRoles_empleadoRepository() {
        List<Roles_empleado> lista = roles_empleadoService.findAllRoles_empleado();
        lista.forEach(r -> {
            System.out.println(
                    "ID: " + r.getId_rol() +
                            " | Nombre rol: " + r.getNombre_rol()
            );
        });
    }
}
