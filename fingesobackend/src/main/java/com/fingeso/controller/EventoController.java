package com.fingeso.controller;

import com.fingeso.model.Evento;
import com.fingeso.service.EventoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/eventos")
@CrossOrigin(origins = "*")
public class EventoController {

    @Autowired
    private EventoService eventoService;

    @GetMapping
    public List<Evento> listarEventos() {
        return eventoService.obtenerTodos();
    }

    @PostMapping
    public Evento crearEvento(@RequestBody Evento evento) {
        return eventoService.guardarEvento(evento);
    }

    @DeleteMapping("/{id}")
    public void eliminarEvento(@PathVariable Long id) {
        eventoService.eliminarEvento(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Evento> actualizarEvento(@PathVariable Long id, @RequestBody Evento evento) {
        Evento eventoActualizado = eventoService.actualizarEvento(id, evento);
        if (eventoActualizado != null) {
            return ResponseEntity.ok(eventoActualizado);
        } else  {
            return ResponseEntity.notFound().build();
        }
    }
}