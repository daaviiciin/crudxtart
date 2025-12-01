package com.example.crudxtart.repository;

import com.example.crudxtart.models.Cliente;

import java.util.List;

public interface ClienteRepository {

    public List<Cliente> findAllClientes();

    public Cliente findClienteById( Integer id);

    public Cliente findClienteByEmail(String email);

    public List<Cliente> findClientesByFilters(String nombre, String email, String telefono);

    public void saveCliente(Cliente c);

    public void deletebyid(Integer id);

    public Cliente upLocalDateCliente(Cliente c);

    public Cliente createCliente(Cliente c);
}

