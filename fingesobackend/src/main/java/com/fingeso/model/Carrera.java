package com.fingeso.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "carreras")
public class Carrera {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private String facultad;

    @OneToMany(mappedBy = "carrera", cascade = CascadeType.ALL)
    private List<Clase> clases;

    public Carrera() {}

    public Carrera(String nombre, String facultad) {
        this.nombre = nombre;
        this.facultad = facultad;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getFacultad() { return facultad; }
    public void setFacultad(String facultad) { this.facultad = facultad; }
    public List<Clase> getClases() { return clases; }
    public void setClases(List<Clase> clases) { this.clases = clases; }
}
