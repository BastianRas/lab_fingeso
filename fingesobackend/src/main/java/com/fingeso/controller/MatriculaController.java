package com.fingeso.controller;

import com.fingeso.model.MatriculaAlumno;
import com.fingeso.service.MatriculaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/matricula")
@CrossOrigin(origins = "*")
public class MatriculaController {

    @Autowired
    private MatriculaService matriculaService;

    @GetMapping("/alumno/{usuarioId}")
    public ResponseEntity<MatriculaAlumno> obtenerMatricula(@PathVariable Long usuarioId) {
        return matriculaService.obtenerPorAlumno(usuarioId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
