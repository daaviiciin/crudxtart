package com.example.crudxtart.service;

import com.example.crudxtart.models.Cliente;
import com.example.crudxtart.repository.ClienteRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.logging.Logger;

@ApplicationScoped
public class ClienteService {

    private static final Logger logger = Logger.getLogger(ClienteService.class.getName());
    private static final String CODIGO_LOG = "SRVC-CLI-";

    @Inject
    ClienteRepository clienteRepository;

    public List<Cliente> findAllClientes() {
        logger.info("[" + CODIGO_LOG + "001] findAllClientes() - Inicio" );
        return clienteRepository.findAllClientes();
    }

    public Cliente findClienteById(Integer id) {
        logger.info("[" + CODIGO_LOG + "002] findClienteById - Inicio. id=" + id);
        return clienteRepository.findClienteById(id);
    }

    public Cliente findClienteByEmail(String email) {
        logger.info("[" + CODIGO_LOG + "003] findClienteByEmail - Inicio. email=" + email);
        return clienteRepository.findClienteByEmail(email);
    }

    public List<Cliente> findClientesByFilters(String nombre, String email, String telefono) {
        logger.info("[" + CODIGO_LOG + "004] findClientesByFilters - Inicio. nombre=" + nombre
                + ", email=" + email + ", telefono=" + telefono);
        return clienteRepository.findClientesByFilters(nombre, email, telefono);
    }

    public Cliente createCliente(Cliente c) {
        logger.info("[" + CODIGO_LOG + "005] createCliente - inicio. cliente=" + c);
        validarCliente(c);
        return clienteRepository.createCliente(c);
    }

    public Cliente upLocalDateCliente(Cliente c) {
        logger.info("[" + CODIGO_LOG + "006] upLocalDateCliente - inicio. cliente=" + c);
        validarCliente(c);
        return clienteRepository.upDateCliente(c);
    }

    public void deleteCliente(Integer id) {
        logger.info("[" + CODIGO_LOG + "007] deleteCliente - inicio. id=" + id);
        clienteRepository.deletebyid(id);
    }

    private void validarCliente(Cliente cliente) {
        logger.info("[" + CODIGO_LOG + "008] validarCliente - inicio");

        if (cliente.getNombre() == null || cliente.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del cliente es obligatorio.");
        }

        // Password es obligatorio en BD, pero si no viene se establece un valor por defecto
        if (cliente.getPassword() == null || cliente.getPassword().trim().isEmpty()) {
            cliente.setPassword("cliente123"); // Valor por defecto
        }

        if (cliente.getEmail() != null && !cliente.getEmail().trim().isEmpty()) {
            if (!cliente.getEmail().contains("@")) {
                throw new IllegalArgumentException("El email del cliente no es válido: " + cliente.getEmail());
            }
        }

        if (cliente.getTelefono() != null && cliente.getTelefono().length() > 20) {
            throw new IllegalArgumentException("El teléfono supera la longitud máxima permitida.");
        }

        if (cliente.getTipo_cliente() != null && !cliente.getTipo_cliente().trim().isEmpty()) {
            String tipo = cliente.getTipo_cliente().trim().toUpperCase();
            if (tipo.equals("PERSONA") || tipo.equals("PARTICULAR")) {
                cliente.setTipo_cliente("PARTICULAR");
            } else if (tipo.equals("EMPRESA")) {
                cliente.setTipo_cliente("EMPRESA");
            } else if (!tipo.equals("PARTICULAR") && !tipo.equals("EMPRESA")) {
                throw new IllegalArgumentException("Tipo de cliente inválido: " + cliente.getTipo_cliente() +
                    ". Valores válidos: PARTICULAR, EMPRESA");
            } else {
                cliente.setTipo_cliente(tipo);
            }
        }
    }
}


