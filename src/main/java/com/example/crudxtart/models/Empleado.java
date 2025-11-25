package com.example.crudxtart.models;


import jakarta.persistence.*;

@Entity
@Table (name = "empleados")
public class Empleado
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_empleado")
    private Integer id_empleado;

    @Column(name = "nombre",nullable = false, length = 150)
    private String nombre;

    @Column(name = "email",length = 150, unique = true, nullable = false)
    private String email;

    @Column (name = "telefono",length = 20)
    private String telefono;

    @Column (name = "password",nullable = false,length = 150)
    private String password;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_rol")
    private Roles_empleado id_rol;



    public Empleado (String nombre, String email, String telefono) {
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;

    }
    public Empleado()
    {}

    public Integer getId_empleado() {
        return id_empleado;
    }

    public void setId_empleado(Integer id_empleado) {
        this.id_empleado = id_empleado;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Roles_empleado getId_rol() {
        return id_rol;
    }

    public void setId_rol(Roles_empleado id_rol) {
        this.id_rol = id_rol;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
