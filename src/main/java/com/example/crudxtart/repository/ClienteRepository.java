package com.example.crudxtart.repository;

import com.example.crudxtart.models.Cliente;
import jakarta.transaction.Transactional;

import java.util.List;

public interface ClienteRepository {
    public List<Cliente> findAllClientes();

    public Cliente findClienteById(int id);

    public void saveCliente(Cliente c);

    public void deleteById(int id);

    @Transactional
    void deletebyid(int id);

    public Cliente updateCliente(Cliente c);

    public Cliente createCliente(Cliente c);

}
