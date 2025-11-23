package com.example.crudxtart.service;

import com.example.crudxtart.models.Cliente;
import com.example.crudxtart.repository.ClienteRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Date;
import java.util.List;

@ApplicationScoped
public class ClienteService {

    @Inject
    private ClienteRepository clienteRepository;

    public ClienteService() {
    }

    public List<Cliente> findAllClientes() {
        return clienteRepository.findAllClientes();
    }

    public Cliente findClienteById(int id) {
        return clienteRepository.findClienteById(id);
    }

    public Cliente createCliente(Cliente c) {
        validarCliente(c);
        return clienteRepository.createCliente(c);
    }

    public Cliente updateCliente(Cliente c) {
        validarCliente(c);
        return clienteRepository.updateCliente(c);
    }

    public void deleteCliente(int id) {
        clienteRepository.deleteById(id);
    }

    private void validarCliente(Cliente c) {
        if (c.getNombre() == null || c.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del cliente no puede estar vacío.");
        }

        if (c.getEmail() == null || !c.getEmail().contains("@")) {
            throw new IllegalArgumentException("Email de cliente no válido.");
        }

        if (c.getFechaAlta() != null && c.getFechaAlta().after(new Date())) {
            throw new IllegalArgumentException("La fecha de alta no puede ser futura.");
        }
    }
}

