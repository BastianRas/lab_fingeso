package com.fingeso.repository;

import com.fingeso.model.Clase;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ClaseRepository extends JpaRepository<Clase, Long> {
    List<Clase> findByCarreraId(Long carreraId);
    List<Clase> findByProfesorUsuarioUsuarioId(Long usuarioId);
}
