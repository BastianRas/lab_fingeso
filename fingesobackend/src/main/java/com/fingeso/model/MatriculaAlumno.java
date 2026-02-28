package com.fingeso.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "matriculas_alumnos")
public class MatriculaAlumno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "usuario_id", unique = true)
    private Usuario alumno;

    @ManyToOne
    @JoinColumn(name = "carrera_id")
    private Carrera carrera;

    @Enumerated(EnumType.STRING)
    private EstadoMatricula estadoMatricula;

    @ManyToMany
    @JoinTable(
        name = "matricula_clases",
        joinColumns = @JoinColumn(name = "matricula_id"),
        inverseJoinColumns = @JoinColumn(name = "clase_id")
    )
    private List<Clase> clases;

    public MatriculaAlumno() {}

    public MatriculaAlumno(Usuario alumno, Carrera carrera, EstadoMatricula estadoMatricula, List<Clase> clases) {
        this.alumno = alumno;
        this.carrera = carrera;
        this.estadoMatricula = estadoMatricula;
        this.clases = clases;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Usuario getAlumno() { return alumno; }
    public void setAlumno(Usuario alumno) { this.alumno = alumno; }
    public Carrera getCarrera() { return carrera; }
    public void setCarrera(Carrera carrera) { this.carrera = carrera; }
    public EstadoMatricula getEstadoMatricula() { return estadoMatricula; }
    public void setEstadoMatricula(EstadoMatricula estadoMatricula) { this.estadoMatricula = estadoMatricula; }
    public List<Clase> getClases() { return clases; }
    public void setClases(List<Clase> clases) { this.clases = clases; }
}
