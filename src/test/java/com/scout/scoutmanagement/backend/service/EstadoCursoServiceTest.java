package com.scout.scoutmanagement.backend.service;

import com.scout.scoutmanagement.backend.repository.EventoRepository;
import com.scout.scoutmanagement.domain.EstadoCurso;
import com.scout.scoutmanagement.domain.NivelCurso;
import com.scout.scoutmanagement.domain.Rama;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EstadoCursoServiceTest {

    @Mock
    private EventoRepository eventoRepository;

    @InjectMocks
    private EstadoCursoService estadoCursoService;

    @Test
    void promocionarSiCorresponde_deberiaPromoverCuandoCompletoTodosLosCursosDelNivel() {
        Rama rama = new Rama();
        rama.setId(2L);

        EstadoCurso estadoCurso = new EstadoCurso();
        estadoCurso.setRama(rama);
        estadoCurso.setNivelActual(NivelCurso.NIVEL_1);
        estadoCurso.getCursosCompletadosIds().add(10L);
        estadoCurso.getCursosCompletadosIds().add(11L);

        when(eventoRepository.obtenerIdsCursosPorRamaYNivel(2L, NivelCurso.NIVEL_1)).thenReturn(List.of(10L, 11L));

        boolean promovido = estadoCursoService.promocionarSiCorresponde(estadoCurso);

        assertTrue(promovido);
        assertEquals(NivelCurso.NIVEL_2, estadoCurso.getNivelActual());
    }

    @Test
    void promocionarSiCorresponde_noDeberiaPromoverCuandoFaltanCursos() {
        Rama rama = new Rama();
        rama.setId(2L);

        EstadoCurso estadoCurso = new EstadoCurso();
        estadoCurso.setRama(rama);
        estadoCurso.setNivelActual(NivelCurso.NIVEL_1);
        estadoCurso.getCursosCompletadosIds().add(10L);

        when(eventoRepository.obtenerIdsCursosPorRamaYNivel(2L, NivelCurso.NIVEL_1)).thenReturn(List.of(10L, 11L));

        boolean promovido = estadoCursoService.promocionarSiCorresponde(estadoCurso);

        assertFalse(promovido);
        assertEquals(NivelCurso.NIVEL_1, estadoCurso.getNivelActual());
    }
}

