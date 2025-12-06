package com.example.crudxtart.service;

import java.util.List;
import java.util.logging.Logger;

import com.example.crudxtart.models.Empleado;
import com.example.crudxtart.repository.EmpleadoRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class EmpleadoService {


    private static final Logger logger = Logger.getLogger(EmpleadoService.class.getName());
    private static final String CODIGO_LOG = "SRVC-EMP-";

    @Inject
    EmpleadoRepository empleadoRepository;

    public List<Empleado> findAllEmpleados() {
        logger.info("[" + CODIGO_LOG + "001] findAllEmpleados() - Inicio"); // CAMBIO LOG
        return empleadoRepository.findAllEmpleados();
    }

    public Empleado findEmpleadoById(Integer id) {
        logger.info("[" + CODIGO_LOG + "002] findEmpleadoById() - Inicio"); // CAMBIO LOG
        return empleadoRepository.findEmpleadoById(id);
    }

    public Empleado findEmpleadoByEmail(String email) {
        logger.info("[" + CODIGO_LOG + "003] findEmpleadoByEmail() - Inicio"); // CAMBIO LOG
        return empleadoRepository.findEmpleadoByEmail(email);
    }

    public Empleado createEmpleado(Empleado e) {
        logger.info("[" + CODIGO_LOG + "004] createEmpleado() - Inicio"); // CAMBIO LOG
        validarEmpleado(e);
        return empleadoRepository.createEmpleado(e);
    }

    public Empleado upLocalDateEmpleado(Empleado e) {
        logger.info("[" + CODIGO_LOG + "005] upLocalDateEmpleado() - Inicio"); // CAMBIO LOG
        validarEmpleado(e);
        return empleadoRepository.updateEmpleado(e);
    }

    public void deleteEmpleado(Integer id) {
        logger.info("[" + CODIGO_LOG + "006] deleteEmpleado() - Inicio"); // CAMBIO LOG
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

        // Estado es obligatorio en BD, pero si no viene se establece un valor por defecto
        if (empleado.getEstado() == null || empleado.getEstado().trim().isEmpty()) {
            empleado.setEstado("activo"); // Valor por defecto
        }

        // Fecha de ingreso es obligatoria en BD, pero si no viene se establece la fecha actual
        if (empleado.getFecha_ingreso() == null) {
            empleado.setFecha_ingreso(java.time.LocalDate.now()); // Valor por defecto: fecha actual
        }

        if (empleado.getTelefono() != null && empleado.getTelefono().length() > 20) {
            throw new IllegalArgumentException("El teléfono supera la longitud máxima permitida.");
        }
    }
}
