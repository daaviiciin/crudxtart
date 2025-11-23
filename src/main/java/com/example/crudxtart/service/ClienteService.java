package com.example.crudxtart.service;

import com.example.crudxtart.models.Cliente;
import com.example.crudxtart.repository.ClienteRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class ClienteService {

    @Inject
    private ClienteRepository clienteRepository;

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

    private void validarCliente(Cliente cliente) {

        if (cliente.getNombre() == null || cliente.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del cliente no puede estar vacío.");
        }
    }
}

