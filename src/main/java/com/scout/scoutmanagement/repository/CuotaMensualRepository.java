package com.scout.scoutmanagement.repository;

import com.scout.scoutmanagement.domain.Pagos.CuotaMensual;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CuotaMensualRepository extends JpaRepository<CuotaMensual, Long> {
    boolean existsByPersonaQueTieneQuePagar_IdAndAnioAndMes(Long personaId, Integer anio, Integer mes);
}

