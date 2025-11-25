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

    public void testClienteRepository()
    {


        Cliente nuevo = new Cliente();
        nuevo.setNombre("Cliente Prueba");
        nuevo.setEmail("cliente.prueba" + System.currentTimeMillis() + "@example.com");
        nuevo.setTelefono("699999999");
        nuevo.setTipo_cliente("persona");
        nuevo.setPassword("1234");
        nuevo.setFecha_alta(new Date());

        Cliente creado = clienteRepository.createCliente(nuevo);

        System.out.println("==== NUEVO CLIENTE CREADO ====");
        System.out.println("Nuevo ID: " + creado.getId_cliente());

        // 3. Lista después de insertar
        System.out.println("==== LISTA DE CLIENTES DESPUÉS DE INSERTAR ====");
        List<Cliente> listaFinal = clienteRepository.findAllClientes();
        listaFinal.forEach(c -> {
            System.out.println(
                    "ID: " + c.getId_cliente() +
                            " | Nombre: " + c.getNombre() +
                            " | Email: " + c.getEmail() +
                            " | Teléfono: " + c.getTelefono() +
                            " | Tipo: " + c.getTipo_cliente() +
                            " | Fecha alta: " + c.getFecha_alta()



        }
}

