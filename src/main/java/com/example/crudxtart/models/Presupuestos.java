package com.example.crudxtart.models;


import jakarta.persistence.*;
import org.jboss.weld.annotated.runtime.InvokableAnnotatedMethod;

@Entity
@Table (name = "presupuestos")
public class Presupuestos
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
}
