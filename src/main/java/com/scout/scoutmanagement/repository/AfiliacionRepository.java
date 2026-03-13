package com.scout.scoutmanagement.repository;

import com.scout.scoutmanagement.domain.Pagos.Afiliacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AfiliacionRepository extends JpaRepository<Afiliacion, Long> {
    boolean existsByPersonaQueTieneQuePagar_IdAndAnio(Long personaId, Integer anio);
}

