package com.fingeso.repository;

import com.fingeso.model.Nota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotaRepository extends JpaRepository<Nota, Long> {
    List<Nota> findByAlumno_UsuarioId(Long usuarioId);
    List<Nota> findByClase_Id(Long claseId);
}