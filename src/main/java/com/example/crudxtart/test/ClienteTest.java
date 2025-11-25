package com.example.crudxtart.test;

import com.example.crudxtart.models.Cliente;
import com.example.crudxtart.models.Empleado;
import com.example.crudxtart.service.ClienteService;
import com.example.crudxtart.service.EmpleadoService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class ClienteTest {

    @Inject
    private ClienteService clienteService;

    @Inject
    private EmpleadoService empleadoService;

    public ClienteTest() {}

    public void testClienteRepository() {

        Empleado empleado = empleadoService.findEmpleadoById(2);

        Cliente nuevo = new Cliente();
        nuevo.setNombre("Cliente Prueba");
        nuevo.setEmail("cliente.prueba" + System.currentTimeMillis() + "@example.com");
        nuevo.setTelefono("699999999");
        nuevo.setTipo_cliente("persona");
        nuevo.setPassword("1234");
        nuevo.setFecha_alta(LocalDate.of(2020, 9, 9));
        nuevo.setEmpleado_responsable(empleado);


        Cliente creado = clienteService.createCliente(nuevo);

        System.out.println("==== NUEVO CLIENTE CREADO ====");
        System.out.println("Nuevo ID: " + creado.getId_cliente());

        List<Cliente> clientes = clienteService.findAllClientes();
        for (Cliente cliente : clientes) {
            System.out.println("ID: " + cliente.getId_cliente());
            System.out.println("Nombre: " + cliente.getNombre());
            System.out.println("Telefono: " + cliente.getTelefono());
            System.out.println("Tipo de cliente: " + cliente.getTipo_cliente());
            System.out.println("Fecha alta: " + cliente.getFecha_alta());
            System.out.println("Empleado respomsable: " + cliente.getEmpleado_responsable());
        }

    }
}

