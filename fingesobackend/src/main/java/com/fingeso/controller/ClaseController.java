package com.fingeso.controller;

import com.fingeso.model.Clase;
import com.fingeso.repository.ClaseRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clases")
public class ClaseController {

    private final ClaseRepository claseRepository;

    public ClaseController(ClaseRepository claseRepository) {
        this.claseRepository = claseRepository;
    }

    @GetMapping("/profesor/{usuarioId}")
    public List<Clase> getClasesByProfesor(@PathVariable Long usuarioId) {
        return claseRepository.findByProfesorUsuarioUsuarioId(usuarioId);
    }
}
