package com.fingeso.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "notas")
public class Nota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombreEvaluacion; // Ej: "PEP 1"
    private Double valor;            // Ej: 6.5
    private Integer ponderacion;     // Ej: 30 (Porcentaje)

    @ManyToOne
    @JoinColumn(name = "alumno_id")
    private Usuario alumno; // ¡Aquí borramos el JsonIgnore!

    @ManyToOne
    @JoinColumn(name = "clase_id")
    private Clase clase;

    public Nota() {}

    public Nota(String nombreEvaluacion, Double valor, Integer ponderacion, Usuario alumno, Clase clase) {
        this.nombreEvaluacion = nombreEvaluacion;
        this.valor = valor;
        this.ponderacion = ponderacion;
        this.alumno = alumno;
        this.clase = clase;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombreEvaluacion() { return nombreEvaluacion; }
    public void setNombreEvaluacion(String nombreEvaluacion) { this.nombreEvaluacion = nombreEvaluacion; }
    public Double getValor() { return valor; }
    public void setValor(Double valor) { this.valor = valor; }
    public Integer getPonderacion() { return ponderacion; }
    public void setPonderacion(Integer ponderacion) { this.ponderacion = ponderacion; }
    public Usuario getAlumno() { return alumno; }
    public void setAlumno(Usuario alumno) { this.alumno = alumno; }
    public Clase getClase() { return clase; }
    public void setClase(Clase clase) { this.clase = clase; }
}