package com.example.crudxtart.repository;

import com.example.crudxtart.models.Cliente;
import java.util.List;

public interface ClienteRepository {

    List<Cliente> findAllClientes();

    Cliente findClienteById(int id);

    Cliente createCliente(Cliente c);

    Cliente updateCliente(Cliente c);

    void deleteById(int id);

    void saveCliente(Cliente c);
}