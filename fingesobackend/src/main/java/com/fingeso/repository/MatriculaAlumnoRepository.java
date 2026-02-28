package com.fingeso.repository;

import com.fingeso.model.MatriculaAlumno;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MatriculaAlumnoRepository extends JpaRepository<MatriculaAlumno, Long> {
    Optional<MatriculaAlumno> findByAlumnoUsuarioId(Long usuarioId);
}
