package com.example.crudxtart.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonGetter;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name ="pagos")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Pagos
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago")
    private Integer id_pago;

    @ManyToOne (fetch = FetchType.EAGER)
    @JoinColumn (name = "id_factura", nullable = false)
    @JsonIgnore
    private Factura factura;

    @Column (name = "fecha_pago", nullable=false)
    private LocalDate fecha_pago;

    @Column (name = "importe")
    private double importe;

    @Column (name = "metodo_pago" ,length = 50)
    private String metodo_pago;

    @Column (name = "estado",length = 50)
    private String estado;

    public Pagos()
    {

    }

    public Pagos(Factura factura, LocalDate fecha_pago, double importe, String metodo_pago, String estado)
    {
        this.factura = factura;
        this.fecha_pago = fecha_pago;
        this.importe = importe;
        this.metodo_pago = metodo_pago;
        this.estado = estado;
    }


    public Integer getId_pago() {
        return id_pago;
    }

    public void setId_pago(Integer id_pago) {
        this.id_pago = id_pago;
    }

    public LocalDate getFecha_pago() {
        return fecha_pago;
    }

    public void setFecha_pago(LocalDate fecha_pago) {
        this.fecha_pago = fecha_pago;
    }

    public double getImporte() {
        return importe;
    }

    public void setImporte(double importe) {
        this.importe = importe;
    }

    public String getMetodo_pago() {
        return metodo_pago;
    }

    public void setMetodo_pago(String metodo_pago) {
        this.metodo_pago = metodo_pago;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Factura getFactura() {
        return factura;
    }

    public void setFactura(Factura factura) {
        this.factura = factura;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Pagos pagos = (Pagos) o;
        return id_pago == pagos.id_pago && Double.compare(importe, pagos.importe) == 0 && Objects.equals(factura, pagos.factura) && Objects.equals(fecha_pago, pagos.fecha_pago) && Objects.equals(metodo_pago, pagos.metodo_pago) && Objects.equals(estado, pagos.estado);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id_pago, factura, fecha_pago, importe, metodo_pago, estado);
    }

    // ============================================================
    // Getters personalizados para serializar solo los IDs
    // ============================================================
    @JsonGetter("id_factura")
    public Integer getId_factura() {
        return factura != null ? factura.getId_factura() : null;
    }

    @JsonGetter("id_cliente")
    public Integer getId_cliente() {
        if (factura != null && factura.getCliente_pagador() != null) {
            return factura.getCliente_pagador().getId_cliente();
        }
        return null;
    }

    @JsonGetter("fecha")
    public LocalDate getFecha() {
        return fecha_pago;
    }
}
