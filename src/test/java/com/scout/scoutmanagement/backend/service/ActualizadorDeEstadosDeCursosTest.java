package com.scout.scoutmanagement.backend.service;

import com.scout.scoutmanagement.backend.repository.EstadoCursoRepository;
import com.scout.scoutmanagement.domain.EstadoCurso;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActualizadorDeEstadosDeCursosTest {

    @Mock
    private EstadoCursoRepository estadoCursoRepository;

    @Mock
    private EstadoCursoService estadoCursoService;

    @InjectMocks
    private ActualizadorDeEstadosDeCursos actualizadorDeEstadosDeCursos;

    @Test
    void actualizarNivelesSemanalmente_deberiaGuardarSoloEstadosPromovidos() {
        EstadoCurso estado1 = new EstadoCurso();
        EstadoCurso estado2 = new EstadoCurso();

        when(estadoCursoRepository.obtenerEstadosDeCursosDePersonasActivas()).thenReturn(List.of(estado1, estado2));
        when(estadoCursoService.promocionarSiCorresponde(estado1)).thenReturn(true);
        when(estadoCursoService.promocionarSiCorresponde(estado2)).thenReturn(false);

        actualizadorDeEstadosDeCursos.actualizarNivelesSemanalmente();

        verify(estadoCursoRepository).save(estado1);
        verify(estadoCursoRepository, never()).save(estado2);
    }
}

