package com.example.crudxtart.test;

import com.example.crudxtart.models.Cliente;
import com.example.crudxtart.service.ClienteService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class ClienteTest {

    @Inject
    private ClienteService clienteService;

    public ClienteTest() {}

    public void testClienteRepository() {
        List<Cliente> lista = clienteService.findAllClientes();
        lista.forEach(c -> {
            System.out.println(
                    "ID: " + c.getId_cliente() +
                            " | Nombre: " + c.getNombre() +
                            " | Email: " + c.getEmail() +
                            " | Teléfono: " + c.getTelefono() +
                            " | Tipo: " + c.getTipo_cliente() +
                            " | Fecha alta: " + c.getFecha_alta()
            );
        });

        Cliente cliente = new Cliente();
    }
}

