package com.fingeso.model;

import jakarta.persistence.*;

@Entity
@Table(name = "pius")
public class Piu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String codigo; // Ej: PIU-001

    @Column(nullable = false)
    private String ubicacion; // Ej: Edificio M - Piso 1

    @Column(nullable = false)
    private String estado; // "Activo", "Inactivo", "Mantenimiento"

    private Double latitud;
    private Double longitud;

    // Constructores, Getters y Setters
    public Piu() {}

    public Piu(String codigo, String ubicacion, String estado, Double latitud, Double longitud) {
        this.codigo = codigo;
        this.ubicacion = ubicacion;
        this.estado = estado;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Double getLatitud() { return latitud; }
    public void setLatitud(Double latitud) { this.latitud = latitud; }

    public Double getLongitud() { return longitud; }
    public void setLongitud(Double longitud) { this.longitud = longitud; }
}