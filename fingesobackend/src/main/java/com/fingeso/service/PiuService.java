package com.fingeso.service;

import com.fingeso.model.Piu;
import com.fingeso.repository.PiuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class PiuService {

    @Autowired
    private PiuRepository piuRepository;

    public List<Piu> obtenerTodos() {
        return piuRepository.findAll();
    }

    public Piu guardarPiu(Piu piu) {
        return piuRepository.save(piu);
    }

    public void eliminarPiu(Long id) {
        piuRepository.deleteById(id);
    }

    public Optional<Piu> obtenerPorId(Long id) {
        return piuRepository.findById(id);
    }

    // Metodo para modificar PIU
    public Piu actualizarPiu(Long id, Piu piuDetalles) {
        Optional<Piu> piuOptional = piuRepository.findById(id);

        if (piuOptional.isPresent()){
            Piu piuExistente = piuOptional.get();
            piuExistente.setCodigo(piuDetalles.getCodigo());
            piuExistente.setUbicacion(piuDetalles.getUbicacion());
            piuExistente.setEstado(piuDetalles.getEstado());
            // ¿Agregar ubicación?

            return piuRepository.save(piuExistente);
        } else {
            return null; // Piu no existe
        }
    }
}