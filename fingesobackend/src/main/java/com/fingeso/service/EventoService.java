package com.fingeso.service;

import com.fingeso.model.Evento;
import com.fingeso.repository.EventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

    public List<Evento> ObtenerPorFecha(LocalDate fecha) {
        return eventoRepository.findByFecha(fecha);
    }
    public List<Evento> ObtenerPorTipo(String tipo) {
        return eventoRepository.findByTipo(tipo);
    }
}