package com.fingeso.controller;

import com.fingeso.model.Lugar;
import com.fingeso.repository.LugarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lugares")
@CrossOrigin(origins = "*")
public class LugarController {

    @Autowired
    private LugarRepository lugarRepository;

    @GetMapping
    public List<Lugar> obtenerTodos() {
        return lugarRepository.findAll();
    }

    @PostMapping
    public Lugar crear(@RequestBody Lugar lugar) {
        return lugarRepository.save(lugar);
    }

    @PutMapping("/{id}")
    public Lugar actualizar(@PathVariable Long id, @RequestBody Lugar detalles) {
        Lugar lugar = lugarRepository.findById(id).orElseThrow();
        lugar.setNombre(detalles.getNombre());
        lugar.setDescripcion(detalles.getDescripcion());

        lugar.setLatitud(detalles.getLatitud());
        lugar.setLongitud(detalles.getLongitud());

        return lugarRepository.save(lugar);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        lugarRepository.deleteById(id);
    }
}