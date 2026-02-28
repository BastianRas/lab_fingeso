package com.fingeso.service;

import com.fingeso.model.MatriculaAlumno;
import com.fingeso.repository.MatriculaAlumnoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class MatriculaService {

    @Autowired
    private MatriculaAlumnoRepository matriculaRepository;

    public Optional<MatriculaAlumno> obtenerPorAlumno(Long usuarioId) {
        return matriculaRepository.findByAlumnoUsuarioId(usuarioId);
    }
}
