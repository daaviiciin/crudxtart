package com.example.crudxtart.service;

import com.example.crudxtart.models.Roles_empleado;
import com.example.crudxtart.repository.Roles_empleadoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class Roles_empleadoService {

    @Inject
    Roles_empleadoRepository roles_empleadoRepository;

    public List<Roles_empleado> findAllRoles_empleado() {
        return roles_empleadoRepository.findAllRoles_empleado();
    }

    public Roles_empleado findRolById(int id) {
        return roles_empleadoRepository.findRolById(id);
    }

    public Roles_empleado createRol(Roles_empleado r) {
        validarRol(r);
        return roles_empleadoRepository.createRol(r);
    }

    public Roles_empleado updateRol(Roles_empleado r) {
        validarRol(r);
        return roles_empleadoRepository.updateRol(r);
    }

    public void deleteRol(int id) {
        roles_empleadoRepository.deletebyid(id);
    }

    private void validarRol(Roles_empleado rol) {

        if (rol.getNombre_rol() == null || rol.getNombre_rol().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del rol es obligatorio.");
        }

        if (rol.getNombre_rol().length() > 150) {
            throw new IllegalArgumentException("El nombre del rol supera la longitud máxima permitida.");
        }
    }
}
