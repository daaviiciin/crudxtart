package com.example.crudxtart.models;


import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "factura")
public class Factura
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_factura")
    private int id_factura;

    @Column(name = "num_factura",unique = true,length = 50,nullable = false)
    private String num_factura;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente_pagador", nullable = false)
    private Cliente cliente_pagador;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "id_empleado",nullable = false)
    private Empleado empleado;

    @Column(name = "fecha_emision", nullable = false)
    private Date fecha_emision;

    @Column(name = "total",nullable = false)
    private double total;

    @Column(name = "estado",nullable = false, length = 50)
    private String estado;

    @OneToMany(mappedBy = "factura",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Pagos> pagos = new ArrayList<>();

    @OneToMany(mappedBy = "factura",cascade = CascadeType.ALL,orphanRemoval = true)
    List<FacturaProducto> productos = new ArrayList<>();




    public Factura  ()
    {

    }

    public Factura(int id_factura, String num_factura, Date fecha_emision, double total, String estado) {
        this.id_factura = id_factura;
        this.num_factura = num_factura;
        this.fecha_emision = fecha_emision;
        this.total = total;
        this.estado = estado;
    }

    public int getId_factura() {
        return id_factura;
    }

    public void setId_factura(int id_factura) {
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

    public Date getFecha_emision() {
        return fecha_emision;
    }

    public void setFecha_emision(Date fecha_emision) {
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

    public List<Pagos> getPagos() {
        return pagos;
    }

    public void setPagos(List<Pagos> pagos) {
        this.pagos = pagos;
    }
}
