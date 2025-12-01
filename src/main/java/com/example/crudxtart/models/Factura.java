package com.example.crudxtart.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonGetter;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "facturas")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Factura
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_factura")
    private Integer id_factura;

    @Column(name = "num_factura",unique = true,length = 50,nullable = false)
    private String num_factura;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente_pagador", nullable = false)
    @JsonIgnore
    private Cliente cliente_pagador;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "id_empleado",nullable = false)
    @JsonIgnore
    private Empleado empleado;

    @Column(name = "fecha_emision", nullable = false)
    private LocalDate fecha_emision;

    @Column(name = "total",nullable = false)
    private double total;

    @Column(name = "estado",nullable = false, length = 50)
    private String estado;

    @Column(name = "notas",nullable = false, length = 150)
    private String notas;

    @OneToMany(mappedBy = "factura",cascade = CascadeType.ALL,orphanRemoval = true)
    @JsonIgnore
    private List<Pagos> pagos = new ArrayList<>();

    @OneToMany(mappedBy = "factura",cascade = CascadeType.ALL,orphanRemoval = true)
    @JsonIgnore
    List<FacturaProducto> productos = new ArrayList<>();




    public Factura  ()
    {

    }

    public Factura(int id_factura, String num_factura, LocalDate fecha_emision, double total, String estado) {
        this.id_factura = id_factura;
        this.num_factura = num_factura;
        this.fecha_emision = fecha_emision;
        this.total = total;
        this.estado = estado;
    }

    public Integer getId_factura() {
        return id_factura;
    }

    public void setId_factura(Integer id_factura) {
        this.id_factura = id_factura;
    }

    public String getNum_factura() {
        return num_factura;
    }

    public void setNum_factura(String num_factura) {
        this.num_factura = num_factura;
    }

    public Cliente getCliente_pagador() {
        return cliente_pagador;
    }

    public void setCliente_pagador(Cliente cliente_pagador) {
        this.cliente_pagador = cliente_pagador;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

    public LocalDate getFecha_emision() {
        return fecha_emision;
    }

    public void setFecha_emision(LocalDate fecha_emision) {
        this.fecha_emision = fecha_emision;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    // ============================================================
    // Getters personalizados para serializar solo los IDs
    // ============================================================
    @JsonGetter("id_cliente")
    public Integer getId_cliente() {
        return cliente_pagador != null ? cliente_pagador.getId_cliente() : null;
    }

    @JsonGetter("id_empleado")
    public Integer getId_empleado() {
        return empleado != null ? empleado.getId_empleado() : null;
    }

    @JsonGetter("fecha")
    public LocalDate getFecha() {
        return fecha_emision;
    }
}


