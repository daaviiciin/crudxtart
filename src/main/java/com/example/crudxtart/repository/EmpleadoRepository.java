package com.example.crudxtart.repository;

import com.example.crudxtart.models.Cliente;
import com.example.crudxtart.models.Empleado;

import java.util.List;

public interface EmpleadoRepository {

    public List<Empleado> findAllEmpleados();

    public Empleado findEmpleadoById(Integer id);

    public Empleado findEmpleadoByEmail(String email);

//    public List<Empleado> findCEmpleadoByFilters(String nombre, String email, String telefono);

    public void saveEmpleado(Empleado e);

    public void deletebyid(Integer id);

    public Empleado updateEmpleado(Empleado e);

    public Empleado createEmpleado(Empleado e);
}
