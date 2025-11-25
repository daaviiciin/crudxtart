package com.example.crudxtart.test;

import com.example.crudxtart.models.Empleado;
import com.example.crudxtart.models.Roles_empleado;
import com.example.crudxtart.service.EmpleadoService;
import com.example.crudxtart.service.Roles_empleadoService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class EmpleadoTest {

    @Inject
    private EmpleadoService empleadoService;

    @Inject
    private Roles_empleadoService roles_empleadoService;

    public EmpleadoTest() {}

    public void testEmpleadoRepository() {


     empleadoService.deleteEmpleado(6);




    }
}
