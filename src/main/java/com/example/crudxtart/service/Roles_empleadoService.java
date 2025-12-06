package com.example.crudxtart.service;

import java.util.List;
import java.util.logging.Logger;

import com.example.crudxtart.models.Roles_empleado;
import com.example.crudxtart.repository.Roles_empleadoRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class Roles_empleadoService {


    private static final Logger logger = Logger.getLogger(Roles_empleadoService.class.getName());
    private static final String CODIGO_LOG = "SRV-ROL-";

    @Inject
    Roles_empleadoRepository roles_empleadoRepository;

    public List<Roles_empleado> findAllRoles_empleado() {
        logger.info("[" + CODIGO_LOG + "001] findAllRoles_empleado() - Inicio");
        List<Roles_empleado> roles = roles_empleadoRepository.findAllRoles_empleado();
        logger.info("[" + CODIGO_LOG + "002] findAllRoles_empleado() - Resultado: " + (roles != null ? roles.size() : "null") + " roles");
        return roles;
    }

    public Roles_empleado findRolById(Integer id) {
        logger.info("[" + CODIGO_LOG + "001] findRolById() - Inicio"); // CAMBIO LOG
        return roles_empleadoRepository.findRolById(id);
    }

    public Roles_empleado createRol(Roles_empleado r) {
        logger.info("[" + CODIGO_LOG + "001] createRol() - Inicio"); // CAMBIO LOG
        validarRol(r);
        return roles_empleadoRepository.createRol(r);
    }

    public Roles_empleado updateRol(Roles_empleado r) {
        logger.info("[" + CODIGO_LOG + "001] updateRol() - Inicio"); // CAMBIO LOG
        validarRol(r);
        return roles_empleadoRepository.updateRol(r);
    }

    public void deleteRol(Integer id) {
        logger.info("[" + CODIGO_LOG + "001] deleteRol() - Inicio"); // CAMBIO LOG
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
