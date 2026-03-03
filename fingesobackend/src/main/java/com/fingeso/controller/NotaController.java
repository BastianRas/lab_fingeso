package com.fingeso.controller;

import com.fingeso.model.Nota;
import com.fingeso.repository.NotaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/notas")
@CrossOrigin(origins = "*")
public class NotaController {

    @Autowired
    private NotaRepository notaRepository;

    @GetMapping("/alumno/{usuarioId}")
    public ResponseEntity<List<Nota>> obtenerNotasDeAlumno(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(notaRepository.findByAlumno_UsuarioId(usuarioId));
    }

    @GetMapping("/clase/{claseId}")
    public ResponseEntity<List<Nota>> obtenerNotasDeClase(@PathVariable Long claseId) {
        return ResponseEntity.ok(notaRepository.findByClase_Id(claseId));
    }

    @PostMapping
    public ResponseEntity<Nota> guardarNota(@RequestBody Nota nota) {
        return ResponseEntity.ok(notaRepository.save(nota));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Nota> actualizarNota(@PathVariable Long id, @RequestBody Nota notaDetalles) {
        Optional<Nota> notaExistente = notaRepository.findById(id);
        if (notaExistente.isPresent()) {
            Nota n = notaExistente.get();
            n.setNombreEvaluacion(notaDetalles.getNombreEvaluacion());
            n.setValor(notaDetalles.getValor());
            n.setPonderacion(notaDetalles.getPonderacion());
            n.setAlumno(notaDetalles.getAlumno());
            n.setClase(notaDetalles.getClase());
            return ResponseEntity.ok(notaRepository.save(n));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarNota(@PathVariable Long id) {
        notaRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}