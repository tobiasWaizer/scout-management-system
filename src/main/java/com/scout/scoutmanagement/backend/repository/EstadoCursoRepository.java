package com.scout.scoutmanagement.backend.repository;

import com.scout.scoutmanagement.domain.EstadoCurso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstadoCursoRepository extends JpaRepository<EstadoCurso, Long> {

    Optional<EstadoCurso> findByPersonaIdAndRamaId(Long personaId, Long ramaId);

    Optional<EstadoCurso> findByPersonaIdAndActualTrue(Long personaId);

    default Optional<EstadoCurso> obtenerEstadoCursoActualPorPersonaId(Long personaId) {
        return findByPersonaIdAndActualTrue(personaId);
    }

    @Query("SELECT ec FROM EstadoCurso ec JOIN ec.persona p WHERE p.activo = true AND ec.actual = true")
    List<EstadoCurso> obtenerEstadosDeCursosDePersonasActivas();
}

