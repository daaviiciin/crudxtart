package com.example.crudxtart.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clientes")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Cliente
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    private Integer id_cliente;

    @Column(name = "nombre", length = 150, nullable = false)
    private String nombre;

    @Column(name = "email",length = 150)
    private String email;

    @Column(name = "telefono",length = 20)
    private String telefono;

    @Column(name = "password",nullable = false,length = 150)
    private String password;

    @Column(name = "tipo_cliente",length = 50)
    private String tipo_cliente;

    @Column(name = "fecha_alta")
    private LocalDate fecha_alta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_empleado_responsable",nullable = true)
    private Empleado empleado_responsable;

    @OneToMany(mappedBy = "cliente_pagador",cascade = CascadeType.ALL,orphanRemoval = true)
    @JsonIgnore
    List<Factura> facturas=new ArrayList<>();

    @OneToMany(mappedBy = "cliente_beneficiario",cascade = CascadeType.ALL,orphanRemoval = true)
    @JsonIgnore
    List<Presupuestos> Presupuesto=new ArrayList<>();


    public Cliente()
    {}

    public Cliente(String nombre, Integer id_cliente, String email, String telefono, String tipo_cliente, LocalDate fecha_alta) {
        this.nombre = nombre;
        this.id_cliente = id_cliente;
        this.email = email;
        this.telefono = telefono;
        this.tipo_cliente = tipo_cliente;
        this.fecha_alta = fecha_alta;
    }

    public Integer getId_cliente() {
        return id_cliente;
    }

    public void setId_cliente(Integer id_cliente) {
        this.id_cliente = id_cliente;
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

    public String getTipo_cliente() {
        return tipo_cliente;
    }

    public void setTipo_cliente(String tipo_cliente) {
        this.tipo_cliente = tipo_cliente;
    }

    public LocalDate getFecha_alta() {
        return fecha_alta;
    }

    public void setFecha_alta(LocalDate fecha_alta) {
        this.fecha_alta = fecha_alta;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Empleado getEmpleado_responsable() {
        return empleado_responsable;
    }

    public void setEmpleado_responsable(Empleado empleado_responsable) {
        this.empleado_responsable = empleado_responsable;
    }

}
