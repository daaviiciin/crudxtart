package com.example.crudxtart.models;


import jakarta.persistence.*;
import com.example.crudxtart.models.Factura;

@Entity
@Table(name = "factura_productos")
public class FacturaProducto
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_factura_producto")
    private Integer id_factura_producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_factura",nullable = false)
    private Factura factura;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto",nullable = false)
    private Producto Producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente_beneficiario",nullable = false)
    private Cliente cliente_beneficiario;

    @Column(name = "cantidad")
    private int cantidad;

    @Column (name = "orecio_unitario")
    private double orecio_unitario;

    public FacturaProducto()
    {

    }


    public FacturaProducto(int id_factura_producto, int cantidad, double orecio_unitario) {
        this.id_factura_producto = id_factura_producto;
        this.cantidad = cantidad;
        this.orecio_unitario = orecio_unitario;
    }

    public int getId_factura_producto() {
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

    public double getOrecio_unitario() {
        return orecio_unitario;
    }

    public void setOrecio_unitario(double orecio_unitario) {
        this.orecio_unitario = orecio_unitario;
    }
}
