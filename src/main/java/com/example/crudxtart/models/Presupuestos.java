package com.example.crudxtart.models;


import jakarta.persistence.*;
import org.jboss.weld.annotated.runtime.InvokableAnnotatedMethod;

import java.time.LocalDate;

@Entity
@Table (name = "presupuestos")
public class Presupuestos
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_presupuesto")
    private Integer id_Presupuesto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_empleado",nullable = false)
    private Empleado empleado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente_pagador",nullable = false)
    private Cliente cliente_pagador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente_beneficiario",nullable = false)
    private Cliente cliente_beneficiario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto",nullable = false)
    private Producto producto;

    @Column(name = "presupuesto",nullable = false)
    private double presupuesto;

    @Column(name = "estado", nullable = false)
    private String estado;

    @Column(name = "fecha_apertura")
    private LocalDate fecha_apertura;

    @Column(name = "fecha_cierre")
    private LocalDate fecha_cierre;

    public Presupuestos ()
    {}

    public Presupuestos(int id_Presupuesto, double presupuesto, String estado) {
        this.id_Presupuesto = id_Presupuesto;
        this.presupuesto = presupuesto;
        this.estado = estado;
    }

    public int getId_Presupuesto() {
        return id_Presupuesto;
    }

    public void setId_Presupuesto(Integer id_Presupuesto) {
        this.id_Presupuesto = id_Presupuesto;
    }

    public double getPresupuesto() {
        return presupuesto;
    }

    public void setPresupuesto(double presupuesto) {
        this.presupuesto = presupuesto;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

    public LocalDate getFecha_cierre() {
        return fecha_cierre;
    }

    public void setFecha_cierre(LocalDate fecha_cierre) {
        this.fecha_cierre = fecha_cierre;
    }

    public LocalDate getFecha_apertura() {
        return fecha_apertura;
    }

    public void setFecha_apertura(LocalDate fecha_apertura) {
        this.fecha_apertura = fecha_apertura;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Cliente getCliente_beneficiario() {
        return cliente_beneficiario;
    }

    public void setCliente_beneficiario(Cliente cliente_beneficiario) {
        this.cliente_beneficiario = cliente_beneficiario;
    }

    public Cliente getCliente_pagador() {
        return cliente_pagador;
    }

    public void setCliente_pagador(Cliente cliente_pagador) {
        this.cliente_pagador = cliente_pagador;
    }
}
