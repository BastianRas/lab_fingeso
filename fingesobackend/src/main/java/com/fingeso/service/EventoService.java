package com.fingeso.service;

import com.fingeso.model.Evento;
import com.fingeso.repository.EventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EventoService {

    @Autowired
    private EventoRepository eventoRepository;

    public List<Evento> obtenerTodos() {
        // Podrías ordenarlos por fecha aquí si quisieras
        return eventoRepository.findAll();
    }

    public Evento guardarEvento(Evento evento) {
        return eventoRepository.save(evento);
    }

    public void eliminarEvento(Long id) {
        eventoRepository.deleteById(id);
    }

    // Metodo para actualizar eventos en vista de administrador
    public Evento actualizarEvento(Long id, Evento eventoDetalles) {
        Evento eventoExistente = eventoRepository.findById(id).orElse(null);
        if (eventoExistente != null) {
            eventoExistente.setTitulo(eventoDetalles.getTitulo());
            eventoExistente.setDescripcion(eventoDetalles.getDescripcion());
            eventoExistente.setFecha(eventoDetalles.getFecha());
            eventoExistente.setTipo(eventoDetalles.getTipo());
            return eventoRepository.save(eventoExistente);
        }
        return null;
    }
}