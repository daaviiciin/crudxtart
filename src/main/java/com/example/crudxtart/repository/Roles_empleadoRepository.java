package com.example.crudxtart.repository;

import com.example.crudxtart.models.Roles_empleado;
import jakarta.persistence.criteria.CriteriaBuilder;

import java.util.List;

public interface Roles_empleadoRepository {

    public List<Roles_empleado> findAllRoles_empleado();

    public Roles_empleado findRolById(Integer id);

    public void saveRol(Roles_empleado r);

    public void deletebyid(Integer id);

    public Roles_empleado updateRol(Roles_empleado r);

    public Roles_empleado createRol(Roles_empleado r);
}

