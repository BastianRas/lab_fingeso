package com.fingeso.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "clases")
public class Clase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private String codigo;
    private String horario;  // Ej: "Lunes 10:00 - 11:30"
    private String sala;     // Ej: "D-101"
    private String profesor;
    private String semestre; // Ej: "2026-1"

    @ManyToOne
    @JoinColumn(name = "carrera_id")
    @JsonIgnore
    private Carrera carrera;

    @ManyToOne
    @JoinColumn(name = "profesor_usuario_id")
    @JsonIgnore
    private Usuario profesorUsuario;

    public Clase() {}

    public Clase(String nombre, String codigo, String horario, String sala, String profesor, String semestre, Carrera carrera) {
        this.nombre = nombre;
        this.codigo = codigo;
        this.horario = horario;
        this.sala = sala;
        this.profesor = profesor;
        this.semestre = semestre;
        this.carrera = carrera;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getHorario() { return horario; }
    public void setHorario(String horario) { this.horario = horario; }
    public String getSala() { return sala; }
    public void setSala(String sala) { this.sala = sala; }
    public String getProfesor() { return profesor; }
    public void setProfesor(String profesor) { this.profesor = profesor; }
    public String getSemestre() { return semestre; }
    public void setSemestre(String semestre) { this.semestre = semestre; }
    public Carrera getCarrera() { return carrera; }
    public void setCarrera(Carrera carrera) { this.carrera = carrera; }
    public Usuario getProfesorUsuario() { return profesorUsuario; }
    public void setProfesorUsuario(Usuario profesorUsuario) { this.profesorUsuario = profesorUsuario; }
}
