package com.example.crudxtart.models;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "presupuesto_productos")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class PresupuestoProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_presupuesto_producto")
    private Integer id_presupuesto_producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_presupuesto", nullable = false)
    @JsonIgnore
    private Presupuestos presupuesto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto", nullable = false)
    @JsonIgnore
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente_beneficiario", nullable = false)
    @JsonIgnore
    private Cliente cliente_beneficiario;

    @Column(name = "cantidad")
    private int cantidad;

    @Column(name = "precio_unitario")
    private double precio_unitario;

    @Column(name = "subtotal")
    private double subtotal;

    public PresupuestoProducto() {
    }

    public Integer getId_presupuesto_producto() {
        return id_presupuesto_producto;
    }

    public void setId_presupuesto_producto(Integer id_presupuesto_producto) {
        this.id_presupuesto_producto = id_presupuesto_producto;
    }

    public Presupuestos getPresupuesto() {
        return presupuesto;
    }

    public void setPresupuesto(Presupuestos presupuesto) {
        this.presupuesto = presupuesto;
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

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecio_unitario() {
        return precio_unitario;
    }

    public void setPrecio_unitario(double precio_unitario) {
        this.precio_unitario = precio_unitario;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    @JsonGetter("id_presupuesto")
    public Integer getId_presupuesto() {
        return presupuesto != null ? presupuesto.getId_Presupuesto() : null;
    }

    @JsonGetter("id_producto")
    public Integer getId_producto() {
        return producto != null ? producto.getId_producto() : null;
    }

    @JsonGetter("id_cliente_beneficiario")
    public Integer getId_cliente_beneficiario() {
        return cliente_beneficiario != null ? cliente_beneficiario.getId_cliente() : null;
    }
}

