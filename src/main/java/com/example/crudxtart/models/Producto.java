package com.example.crudxtart.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "productos")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Producto
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Integer id_producto;

    @Column(name = "nombre",length = 150,nullable = false)
    private String nombre;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "categoria")
    private String categoria;

    @Column(name = "precio")
    private double precio;

    @Column(name = "activo")
    private boolean activo;

    @OneToMany(mappedBy = "Producto",cascade = CascadeType.ALL,orphanRemoval = true)
    @JsonIgnore
    List<FacturaProducto> facturaProducto=new ArrayList<>();

    public  Producto()
    {}

    public Producto(int id_producto, String nombre, String descripcion, String categoria, double precio, boolean activo) {
        this.id_producto = id_producto;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.precio = precio;
        this.activo = activo;
    }

    public Integer getId_producto() {
        return id_producto;
    }

    public void setId_producto(Integer id_producto) {
        this.id_producto = id_producto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
