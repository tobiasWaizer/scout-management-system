package com.scout.scoutmanagement.backend.service;

import com.scout.scoutmanagement.backend.repository.EventoRepository;
import com.scout.scoutmanagement.domain.EstadoCurso;
import com.scout.scoutmanagement.domain.NivelCurso;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class EstadoCursoService {

    private final EventoRepository eventoRepository;

    public EstadoCursoService(EventoRepository eventoRepository) {
        this.eventoRepository = eventoRepository;
    }

    public boolean promocionarSiCorresponde(EstadoCurso estadoCurso) {
        if (estadoCurso == null || estadoCurso.getRama() == null || estadoCurso.getNivelActual() == null) {
            return false;
        }

        NivelCurso nivelActual = estadoCurso.getNivelActual();
        Long ramaId = estadoCurso.getRama().getId();
        if (ramaId == null) {
            return false;
        }

        List<Long> cursosDelNivel = eventoRepository.obtenerIdsCursosPorRamaYNivel(ramaId, nivelActual);
        if (cursosDelNivel.isEmpty()) {
            return false;
        }

        Set<Long> cursosCompletados = estadoCurso.getCursosCompletadosIds();
        if (!cursosCompletados.containsAll(cursosDelNivel)) {
            return false;
        }

        return nivelActual.siguienteNivel()
            .map(siguiente -> {
                estadoCurso.setNivelActual(siguiente);
                return true;
            })
            .orElse(false);
    }
}

