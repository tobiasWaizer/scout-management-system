package com.scout.scoutmanagement.backend.repository;

import com.scout.scoutmanagement.domain.AlcanceEvento;
import com.scout.scoutmanagement.domain.Evento;
import com.scout.scoutmanagement.domain.EventoCurso;
import com.scout.scoutmanagement.domain.NivelCurso;
import com.scout.scoutmanagement.backend.exception.ObjectNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Long> {

    @Query(
        "SELECT e FROM Evento e " +
        "WHERE e.fechaInicio >= :desde AND e.fechaInicio <= :hasta " +
        "ORDER BY e.fechaInicio ASC"
    )
    List<Evento> obtenerEventosEnRango(
        @Param("desde") LocalDateTime desde,
        @Param("hasta") LocalDateTime hasta
    );

    @Query(
        "SELECT e FROM Evento e " +
        "WHERE e.fechaInicio >= :desde AND e.fechaInicio <= :hasta " +
        "AND e.alcanceEvento = :rama " +
        "AND e.rama.id = :ramaId " +
        "ORDER BY e.fechaInicio ASC"
    )
    List<Evento> obtenerEventosDeRamaEnRango(
        @Param("desde") LocalDateTime desde,
        @Param("hasta") LocalDateTime hasta,
        @Param("ramaId") Long ramaId,
        @Param("rama") AlcanceEvento rama
    );

    @Query(
        "SELECT e FROM Evento e " +
        "WHERE e.fechaInicio >= :desde AND e.fechaInicio <= :hasta " +
        "AND (" +
        "e.alcanceEvento = :general " +
        "OR (e.alcanceEvento = :rama AND e.rama.id = :ramaId) " +
        "OR (:esEducador = true AND e.alcanceEvento = :educadores)" +
        ") " +
        "ORDER BY e.fechaInicio ASC"
    )
    List<Evento> obtenerEventosVisiblesParaPersonaEnRango(
        @Param("desde") LocalDateTime desde,
        @Param("hasta") LocalDateTime hasta,
        @Param("ramaId") Long ramaId,
        @Param("general") AlcanceEvento general,
        @Param("rama") AlcanceEvento rama,
        @Param("educadores") AlcanceEvento educadores,
        @Param("esEducador") boolean esEducador
    );

    @Query(
        "SELECT c.id FROM EventoCurso c " +
        "WHERE c.rama.id = :ramaId AND c.nivel = :nivel"
    )
    List<Long> obtenerIdsCursosPorRamaYNivel(
        @Param("ramaId") Long ramaId,
        @Param("nivel") NivelCurso nivel
    );

    default EventoCurso getCursoById(Long idCurso) {
        return findById(idCurso)
            .filter(evento -> evento instanceof EventoCurso)
            .map(evento -> (EventoCurso) evento)
            .orElseThrow(() -> new ObjectNotFoundException("No existe un curso con ID: " + idCurso));
    }
}



