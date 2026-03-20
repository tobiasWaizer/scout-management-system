package com.scout.scoutmanagement.backend.service;

import com.scout.scoutmanagement.backend.dto.EventoDTO;
import com.scout.scoutmanagement.backend.exception.ObjectNotFoundException;
import com.scout.scoutmanagement.backend.repository.EventoRepository;
import com.scout.scoutmanagement.domain.Evento;
import com.scout.scoutmanagement.domain.EventoActividad;
import com.scout.scoutmanagement.domain.EventoCurso;
import com.scout.scoutmanagement.domain.EstadoCurso;
import com.scout.scoutmanagement.domain.NivelCurso;
import com.scout.scoutmanagement.domain.Persona;
import com.scout.scoutmanagement.domain.Rama;
import com.scout.scoutmanagement.domain.TipoEvento;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventosServiceTest {

    @Mock
    private EventoRepository eventoRepository;

    @Mock
    private CreadorDeEventos creadorDeEventos;

    @Mock
    private RamasService ramasService;

    @Mock
    private PersonasService personasService;

    @Mock
    private EstadoCursoService estadoCursoService;

    @InjectMocks
    private EventosService eventosService;

    @Test
    void modificarEvento_deberiaMantenerIdYActualizarDatos() {
        EventoActividad existente = new EventoActividad();
        existente.setId(10L);
        existente.setTitulo("Evento original");

        EventoDTO dto = new EventoDTO();
        dto.setTipoEvento(TipoEvento.ACTIVIDAD);
        dto.setTitulo("Evento actualizado");

        when(eventoRepository.findById(10L)).thenReturn(Optional.of(existente));
        doAnswer(invocation -> {
            Evento evento = invocation.getArgument(0);
            EventoDTO eventoDTO = invocation.getArgument(1);
            evento.setTitulo(eventoDTO.getTitulo());
            return null;
        }).when(creadorDeEventos).actualizarDesde(any(Evento.class), any(EventoDTO.class));
        when(eventoRepository.save(existente)).thenReturn(existente);

        Evento resultado = eventosService.modificarEvento(10L, dto);

        assertSame(existente, resultado);
        assertEquals(10L, resultado.getId());
        assertEquals("Evento actualizado", resultado.getTitulo());
        verify(creadorDeEventos).actualizarDesde(existente, dto);
        verify(eventoRepository).save(existente);
    }

    @Test
    void reprogramarEvento_deberiaLanzarErrorSiFechasInvalidas() {
        LocalDateTime inicio = LocalDateTime.of(2026, 3, 20, 18, 0);
        LocalDateTime fin = LocalDateTime.of(2026, 3, 20, 17, 0);

        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> eventosService.reprogramarEvento(5L, inicio, fin)
        );

        assertEquals("La fechaInicio no puede ser mayor a la fechaFin", ex.getMessage());
        verify(eventoRepository, never()).save(any(Evento.class));
    }

    @Test
    void obtenerEventoPorId_deberiaLanzarObjectNotFoundSiNoExiste() {
        when(eventoRepository.findById(999L)).thenReturn(Optional.empty());

        ObjectNotFoundException ex = assertThrows(
            ObjectNotFoundException.class,
            () -> eventosService.obtenerEventoPorId(999L)
        );

        assertEquals("No existe un evento con ID: 999", ex.getMessage());
    }

    @Test
    void suscribirPersonaACurso_deberiaSuscribirSiCumpleRamaYPrerequisito() {
        Rama rama = new Rama();
        rama.setId(2L);

        EventoCurso cursoNivel2 = new EventoCurso();
        cursoNivel2.setId(7L);
        cursoNivel2.setRama(rama);
        cursoNivel2.setNivel(NivelCurso.NIVEL_1);

        Persona persona = new Persona();
        persona.setId(9L);
        persona.setRama(rama);
        EstadoCurso estadoCurso = new EstadoCurso();
        estadoCurso.setNivelActual(NivelCurso.NIVEL_1);

        when(eventoRepository.findById(7L)).thenReturn(Optional.of(cursoNivel2));
        when(personasService.obtenerPersona(9L)).thenReturn(persona);
        when(personasService.obtenerEstadoCursoActualSegunPersona(persona)).thenReturn(estadoCurso);
        when(eventoRepository.save(cursoNivel2)).thenReturn(cursoNivel2);

        EventoCurso resultado = eventosService.suscribirPersonaACurso(7L, 9L);

        assertSame(cursoNivel2, resultado);
        assertTrue(resultado.getSuscriptos().stream().anyMatch(p -> p.getId().equals(9L)));
        verify(eventoRepository).save(cursoNivel2);
    }

    @Test
    void suscribirPersonaACurso_deberiaFallarSiNoCumplePrerequisito() {
        Rama rama = new Rama();
        rama.setId(2L);

        EventoCurso cursoNivel2 = new EventoCurso();
        cursoNivel2.setId(7L);
        cursoNivel2.setRama(rama);
        cursoNivel2.setNivel(NivelCurso.NIVEL_2);

        Persona persona = new Persona();
        persona.setId(9L);
        persona.setRama(rama);
        EstadoCurso estadoCurso = new EstadoCurso();
        estadoCurso.setNivelActual(NivelCurso.NIVEL_1);

        when(eventoRepository.findById(7L)).thenReturn(Optional.of(cursoNivel2));
        when(personasService.obtenerPersona(9L)).thenReturn(persona);
        when(personasService.obtenerEstadoCursoActualSegunPersona(persona)).thenReturn(estadoCurso);

        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> eventosService.suscribirPersonaACurso(7L, 9L)
        );

        assertEquals("La persona no esta habilitada para este curso. Nivel actual: NIVEL_1, nivel del curso: NIVEL_2", ex.getMessage());
        verify(eventoRepository, never()).save(any(Evento.class));
    }

    @Test
    void registrarCursoCompletado_deberiaAgregarCursoYPromoverNivel() {
        Rama rama = new Rama();
        rama.setId(2L);

        Persona persona = new Persona();
        persona.setId(5L);
        persona.setRama(rama);
        EstadoCurso estadoCurso = new EstadoCurso();
        estadoCurso.setNivelActual(NivelCurso.NIVEL_1);

        EventoCurso curso = new EventoCurso();
        curso.setId(100L);
        curso.setRama(rama);
        curso.setNivel(NivelCurso.NIVEL_1);

        when(personasService.obtenerPersona(5L)).thenReturn(persona);
        when(personasService.obtenerEstadoCursoActualSegunPersona(persona)).thenReturn(estadoCurso);
        when(eventoRepository.findById(100L)).thenReturn(Optional.of(curso));
        when(estadoCursoService.promocionarSiCorresponde(estadoCurso)).thenReturn(true);
        when(personasService.guardarEstadoCurso(estadoCurso)).thenReturn(estadoCurso);

        eventosService.registrarCursoCompletado(5L, 100L);

        assertTrue(estadoCurso.getCursosCompletadosIds().contains(100L));
        verify(estadoCursoService).promocionarSiCorresponde(estadoCurso);
        verify(personasService).guardarEstadoCurso(estadoCurso);
    }

    @Test
    void registrarCursoCompletado_noDeberiaPromoverSiFaltanCursosDelNivel() {
        Rama rama = new Rama();
        rama.setId(2L);

        Persona persona = new Persona();
        persona.setId(5L);
        persona.setRama(rama);
        EstadoCurso estadoCurso = new EstadoCurso();
        estadoCurso.setNivelActual(NivelCurso.NIVEL_1);

        EventoCurso curso = new EventoCurso();
        curso.setId(100L);
        curso.setRama(rama);
        curso.setNivel(NivelCurso.NIVEL_1);

        when(personasService.obtenerPersona(5L)).thenReturn(persona);
        when(personasService.obtenerEstadoCursoActualSegunPersona(persona)).thenReturn(estadoCurso);
        when(eventoRepository.findById(100L)).thenReturn(Optional.of(curso));
        when(estadoCursoService.promocionarSiCorresponde(estadoCurso)).thenReturn(false);
        when(personasService.guardarEstadoCurso(estadoCurso)).thenReturn(estadoCurso);

        eventosService.registrarCursoCompletado(5L, 100L);

        assertEquals(NivelCurso.NIVEL_1, estadoCurso.getNivelActual());
    }
}



