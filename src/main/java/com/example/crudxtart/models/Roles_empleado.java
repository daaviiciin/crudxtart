package com.example.crudxtart.models;


import jakarta.persistence.*;

@Entity
@Table (name = "roles_empleado")
public class Roles_empleado
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol")
    private Integer id_rol;

    @Column(name = "nombre_rol", length = 150, nullable = false)
    private String nombre_rol;

    public Roles_empleado(int id_rol, String nombre_rol)
    {
        this.id_rol = id_rol;
        this.nombre_rol = nombre_rol;
    }

    public Roles_empleado()
    {

    }

    public Integer getId_rol()
    {
        return id_rol;
    }
    public void setId_rol(Integer id_rol)
    {
        this.id_rol = id_rol;
    }
    public String getNombre_rol()
    {
        return nombre_rol;

    }
    public void setNombre_rol(String nombre_rol)
    {
        this.nombre_rol = nombre_rol;
    }

}
