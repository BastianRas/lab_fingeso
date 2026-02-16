package com.fingeso.repository;

import com.fingeso.model.Piu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PiuRepository extends JpaRepository<Piu, Long> {
    List<Piu> obtenerPorEstado(String estado);

    List<Piu> findByEstado(String estado);
}