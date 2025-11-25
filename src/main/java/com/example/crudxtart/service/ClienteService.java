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

        if (cliente.getEmail() != null && !cliente.getEmail().trim().isEmpty()) {
            if (!cliente.getEmail().contains("@")) {
                throw new IllegalArgumentException("El email del cliente no es válido: " + cliente.getEmail());
            }
        }

        if (cliente.getTelefono() != null && cliente.getTelefono().length() > 20) {
            throw new IllegalArgumentException("El teléfono supera la longitud máxima permitida.");
        }

        if (cliente.getTipo_cliente() != null && cliente.getTipo_cliente().length() > 50) {
            throw new IllegalArgumentException("El tipo de cliente supera la longitud máxima permitida.");
        }
    }
}


