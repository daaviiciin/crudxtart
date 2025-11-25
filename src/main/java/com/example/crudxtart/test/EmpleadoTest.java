package com.example.crudxtart.test;

import com.example.crudxtart.models.Empleado;
import com.example.crudxtart.service.EmpleadoService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class EmpleadoTest {

    @Inject
    private EmpleadoService empleadoService;

    public EmpleadoTest() {}

    public void testEmpleadoRepository() {
        List<Empleado> lista = empleadoService.findAllEmpleados();
        lista.forEach(e -> {
            System.out.println(
                    "ID: " + e.getId_empleado() +
                            " | Nombre: " + e.getNombre() +
                            " | Email: " + e.getEmail() +
                            " | Teléfono: " + e.getTelefono()
            );
        });
    }
}
