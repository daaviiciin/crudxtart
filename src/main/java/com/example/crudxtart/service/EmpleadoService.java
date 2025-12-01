package com.example.crudxtart.service;

import com.example.crudxtart.models.Empleado;
import com.example.crudxtart.repository.EmpleadoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class EmpleadoService {

    @Inject
    EmpleadoRepository empleadoRepository;

    public List<Empleado> findAllEmpleados() {
        return empleadoRepository.findAllEmpleados();
    }

    public Empleado findEmpleadoById(Integer id) {
        return empleadoRepository.findEmpleadoById(id);
    }

    public Empleado findEmpleadoByEmail(String email) {
        return empleadoRepository.findEmpleadoByEmail(email);
    }

    public Empleado createEmpleado(Empleado e) {
        validarEmpleado(e);
        return empleadoRepository.createEmpleado(e);
    }

    public Empleado upLocalDateEmpleado(Empleado e) {
        validarEmpleado(e);
        return empleadoRepository.updateEmpleado(e);
    }

    public void deleteEmpleado(Integer id) {
        empleadoRepository.deletebyid(id);
    }

    private void validarEmpleado(Empleado empleado) {

        if (empleado.getNombre() == null || empleado.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del empleado es obligatorio.");
        }

        if (empleado.getEmail() == null || empleado.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("El email del empleado es obligatorio.");
        }

        if (!empleado.getEmail().contains("@")) {
            throw new IllegalArgumentException("El email del empleado no es válido: " + empleado.getEmail());
        }

        // Password es obligatorio en BD, pero si no viene se establece un valor por defecto
        if (empleado.getPassword() == null || empleado.getPassword().trim().isEmpty()) {
            empleado.setPassword("empleado123"); // Valor por defecto
        }

        if (empleado.getTelefono() != null && empleado.getTelefono().length() > 20) {
            throw new IllegalArgumentException("El teléfono supera la longitud máxima permitida.");
        }
    }
}
