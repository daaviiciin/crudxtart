package com.example.crudxtart.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "factura_productos")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class FacturaProducto
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_factura_producto")
    private Integer id_factura_producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_factura",nullable = false)
    @JsonIgnore
    private Factura factura;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto",nullable = false)
    @JsonIgnore
    private Producto Producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente_beneficiario",nullable = false)
    @JsonIgnore
    private Cliente cliente_beneficiario;

    @Column(name = "cantidad")
    private int cantidad;

    @Column(name = "subtotal")
    private double subtotal;

    @Column (name = "precio_unitario")
    private double precio_unitario;

    public FacturaProducto()
    {

    }


    public FacturaProducto(int id_factura_producto, int cantidad, double orecio_unitario) {
        this.id_factura_producto = id_factura_producto;
        this.cantidad = cantidad;
        this.precio_unitario = orecio_unitario;
    }

    public Integer getId_factura_producto() {
        return id_factura_producto;
    }

    public void setId_factura_producto(Integer id_factura_producto) {
        this.id_factura_producto = id_factura_producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getprecio_unitario() {
        return precio_unitario;
    }

    public void setprecio_unitario(double precio_unitario) {
        this.precio_unitario = precio_unitario;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public Factura getFactura() {
        return factura;
    }

    public void setFactura(Factura factura) {
        this.factura = factura;
    }

    public Producto getProducto() {
        return Producto;
    }

    public void setProducto(Producto producto) {
        Producto = producto;
    }

    public Cliente getCliente_beneficiario() {
        return cliente_beneficiario;
    }

    public void setCliente_beneficiario(Cliente cliente_beneficiario) {
        this.cliente_beneficiario = cliente_beneficiario;
    }

    public double getPrecio_unitario() {
        return precio_unitario;
    }

    public void setPrecio_unitario(double precio_unitario) {
        this.precio_unitario = precio_unitario;
    }
}
