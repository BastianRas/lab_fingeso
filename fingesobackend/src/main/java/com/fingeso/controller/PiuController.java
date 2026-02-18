package com.fingeso.controller;

import com.fingeso.model.Piu;
import com.fingeso.service.PiuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pius")
@CrossOrigin(origins = "http://localhost:5173") // Permite conexión con Vue
public class PiuController {

    @Autowired
    private PiuService piuService;

    @GetMapping
    public List<Piu> listarPius() {
        return piuService.obtenerTodos();
    }

    @PostMapping
    public Piu crearPiu(@RequestBody Piu piu) {
        return piuService.guardarPiu(piu);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarPiu(@PathVariable Long id) {
        piuService.eliminarPiu(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Piu> actualizarPiu(@PathVariable Long id, @RequestBody Piu piuDetalles) {
        Piu piuActualizado = piuService.actualizarPiu(id, piuDetalles);

        if (piuActualizado != null) {
            return ResponseEntity.ok(piuActualizado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}