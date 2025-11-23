package com.example.crudxtart.repository;

import com.example.crudxtart.models.Cliente;

import java.util.List;

public interface ClienteRepository {

    List<Cliente> findAllClientes();
    Cliente findClienteById(int id);
    void saveCliente(Cliente c);      // opcional, por si lo quieres usar
    void deleteById(int id);
    Cliente updateCliente(Cliente c);
    Cliente createCliente(Cliente c);
}
