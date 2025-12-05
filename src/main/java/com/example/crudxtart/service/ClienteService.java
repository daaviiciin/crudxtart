package com.example.crudxtart.service;

import com.example.crudxtart.models.Cliente;
import com.example.crudxtart.repository.ClienteRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class ClienteService {

    @Inject
    ClienteRepository clienteRepository;

    public List<Cliente> findAllClientes() {
        return clienteRepository.findAllClientes();
    }

    public Cliente findClienteById(Integer id) {
        return clienteRepository.findClienteById(id);
    }

    public Cliente findClienteByEmail(String email) {
        return clienteRepository.findClienteByEmail(email);
    }

    public List<Cliente> findClientesByFilters(String nombre, String email, String telefono) {
        return clienteRepository.findClientesByFilters(nombre, email, telefono);
    }

    public Cliente createCliente(Cliente c) {
        validarCliente(c);
        return clienteRepository.createCliente(c);
    }

    public Cliente upLocalDateCliente(Cliente c) {
        validarCliente(c);
        return clienteRepository.upLocalDateCliente(c);
    }

    public void deleteCliente(Integer id) {
        clienteRepository.deletebyid(id);
    }

    private void validarCliente(Cliente cliente) {

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


