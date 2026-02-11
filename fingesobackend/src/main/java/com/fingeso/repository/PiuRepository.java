package com.fingeso.repository;

import com.fingeso.model.Piu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PiuRepository extends JpaRepository<Piu, Long> {
    // Aquí puedes agregar métodos extra si necesitas, ej: findByEstado(String estado);
}