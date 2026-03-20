package com.scout.scoutmanagement.backend.service;

import com.scout.scoutmanagement.backend.repository.EstadoCursoRepository;
import com.scout.scoutmanagement.domain.EstadoCurso;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@ConditionalOnProperty(name = "cursos.estados-actualizador.enabled", havingValue = "true", matchIfMissing = true)
public class ActualizadorDeEstadosDeCursos {

    private final EstadoCursoRepository estadoCursoRepository;
    private final EstadoCursoService estadoCursoService;

    public ActualizadorDeEstadosDeCursos(
        EstadoCursoRepository estadoCursoRepository,
        EstadoCursoService estadoCursoService
    ) {
        this.estadoCursoRepository = estadoCursoRepository;
        this.estadoCursoService = estadoCursoService;
    }

    @Scheduled(cron = "${cursos.estados-actualizador.cron:0 0 3 ? * MON}")
    @Transactional
    public void actualizarNivelesSemanalmente() {
        List<EstadoCurso> estados = estadoCursoRepository.obtenerEstadosDeCursosDePersonasActivas();

        estados.stream()
            .filter(estadoCursoService::promocionarSiCorresponde)
            .forEach(estadoCursoRepository::save);
    }
}

