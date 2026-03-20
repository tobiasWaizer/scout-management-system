package com.scout.scoutmanagement.backend.service;

import com.scout.scoutmanagement.backend.dto.EventoDTO;
import com.scout.scoutmanagement.backend.exception.ObjectNotFoundException;
import com.scout.scoutmanagement.domain.*;
import com.scout.scoutmanagement.backend.repository.EventoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EventosService {

    private final EventoRepository eventoRepository;
    private final CreadorDeEventos creadorDeEventos;
    private final RamasService ramasService;
    private final PersonasService personasService;
    private final EstadoCursoService estadoCursoService;

    public EventosService(
        EventoRepository eventoRepository,
        CreadorDeEventos creadorDeEventos,
        RamasService ramasService,
        PersonasService personasService,
        EstadoCursoService estadoCursoService
    ) {
        this.eventoRepository = eventoRepository;
        this.creadorDeEventos = creadorDeEventos;
        this.ramasService = ramasService;
        this.personasService = personasService;
        this.estadoCursoService = estadoCursoService;
    }

    public Evento crearEvento(EventoDTO eventoDTO) {
        Evento evento = creadorDeEventos.crearDesde(eventoDTO);
        return eventoRepository.save(evento);
    }

    public Evento modificarEvento(Long idEvento, EventoDTO eventoDTO) {
        Evento eventoExistente = obtenerEventoPorId(idEvento);
        creadorDeEventos.actualizarDesde(eventoExistente, eventoDTO);
        return eventoRepository.save(eventoExistente);
    }

    public Evento reprogramarEvento(Long idEvento, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        if (fechaInicio == null || fechaFin == null) {
            throw new IllegalArgumentException("Para reprogramar un evento se requieren fechaInicio y fechaFin");
        }
        if (fechaInicio.isAfter(fechaFin)) {
            throw new IllegalArgumentException("La fechaInicio no puede ser mayor a la fechaFin");
        }

        Evento eventoExistente = obtenerEventoPorId(idEvento);
        eventoExistente.setFechaInicio(fechaInicio);
        eventoExistente.setFechaFin(fechaFin);
        return eventoRepository.save(eventoExistente);
    }

    public Evento obtenerEventoPorId(Long idEvento) {
        return eventoRepository.findById(idEvento)
            .orElseThrow(() -> new ObjectNotFoundException("No existe un evento con ID: " + idEvento));
    }

    public void eliminarEvento(Long idEvento) {
        Evento evento = obtenerEventoPorId(idEvento);
        eventoRepository.delete(evento);
    }

    public EventoCurso suscribirPersonaACurso(Long idCurso, Long idPersona) {
        Evento evento = obtenerEventoPorId(idCurso);
        if (!(evento instanceof EventoCurso curso)) {
            throw new IllegalArgumentException("El evento con ID " + idCurso + " no es un curso");
        }

        Persona persona = personasService.obtenerPersona(idPersona);
        EstadoCurso estadoCurso = personasService.obtenerEstadoCursoActualSegunPersona(persona);
        validarSuscripcionACurso(curso, persona, estadoCurso);

        curso.agregarSuscripto(persona);
        eventoRepository.save(curso);
        return curso;
    } //testeado como bien

    public EventoCurso registrarCursoCompletado(Long idPersona, Long idCurso) {
        Persona persona = personasService.obtenerPersona(idPersona);
        EstadoCurso estadoCurso = personasService.obtenerEstadoCursoActualSegunPersona(persona);

        EventoCurso curso = eventoRepository.getCursoById(idCurso);

        validarCursoCompletadoContraEstado(persona, curso, estadoCurso);

        estadoCurso.marcarCompletado(curso);
        //lo de abajo aun no lo chequeamos
        estadoCursoService.promocionarSiCorresponde(estadoCurso); //consultar si dejo que cada vez que marco un curso como completado se haga esta pregunta o si que lo haga el actualizador de estados cada x cantidad de tiempo a chat
        return curso;
    }

    public List<Evento> obtenerEventosSegun(LocalDateTime desde, LocalDateTime hasta, Long personaId, List<Long> ramaIds, List<String> tiposEvento, List<String> alcancesEvento) {
        List<Evento> eventos = eventoRepository.obtenerEventosEnRango(desde, hasta);

        /*if (personaId != null) { //TODO: CUANDO IMPLEMENTEMOS LAS SESIONES SI USAREMOS EL PERSONA ID PARA VER QUE EVENTOS DEJAMOS VISIBLES
            Persona persona = personaService.obtenerPersona(personaId);
            eventos = filtrarEventosVisiblesParaPersona(eventos, persona);
        }*/

        if (ramaIds != null && !ramaIds.isEmpty()) {
            Set<Long> ramasSolicitadas = ramaIds.stream().collect(Collectors.toSet());
            ramasSolicitadas.forEach(rama -> ramasService.obtenerRamaPorId(rama));

            eventos = eventos.stream()
                .filter(evento -> evento.getRama() != null && ramasSolicitadas.contains(evento.getRama().getId()))
                .toList();
        }

        return filtrarEventos(eventos, tiposEvento, alcancesEvento);
    }

    private List<Evento> filtrarEventos(List<Evento> eventos, List<String> tiposEvento, List<String> alcancesEvento) {
        Set<TipoEvento> tiposPermitidos = parsearTipos(tiposEvento);
        Set<AlcanceEvento> alcancesPermitidos = parsearAlcances(alcancesEvento);

        return eventos.stream()
            .filter(evento -> tiposPermitidos.isEmpty() || tiposPermitidos.contains(evento.getTipo()))
            .filter(evento -> alcancesPermitidos.isEmpty() || alcancesPermitidos.contains(evento.getAlcanceEvento()))
            .toList();
    }
    

    private Set<TipoEvento> parsearTipos(List<String> tiposEvento) {
        if (tiposEvento == null || tiposEvento.isEmpty()) {
            return Set.of();
        }

        try {
            return tiposEvento.stream()
                .map(String::trim)
                .map(String::toUpperCase)
                .map(TipoEvento::valueOf)
                .collect(Collectors.toSet());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Tipo de evento inválido. Valores permitidos: " + Arrays.toString(TipoEvento.values()));
        }
    }

    private Set<AlcanceEvento> parsearAlcances(List<String> alcancesEvento) {
        if (alcancesEvento == null || alcancesEvento.isEmpty()) {
            return Set.of();
        }

        try {
            return alcancesEvento.stream()
                .map(String::trim)
                .map(String::toUpperCase)
                .map(AlcanceEvento::valueOf)
                .collect(Collectors.toSet());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Alcance de evento inválido. Valores permitidos: " + Arrays.toString(AlcanceEvento.values()));
        }
    }

    private void validarSuscripcionACurso(EventoCurso curso, Persona persona, EstadoCurso estadoCurso) {
        validarMismaRama(curso, estadoCurso);

        if (curso.getSuscriptos().stream().anyMatch(suscripto -> suscripto.getId().equals(persona.getId()))) {
            throw new IllegalArgumentException("La persona ya se encuentra suscripta al curso");
        }

        if (!personaHabilitadaParaNivel(curso, estadoCurso)) {
            throw new IllegalArgumentException(
                "La persona no esta habilitada para este curso. Nivel actual: " +
                    estadoCurso.getNivelActual() + ", nivel del curso: " + curso.getNivel()
            );
        }
    }

    private void validarMismaRama(EventoCurso curso, EstadoCurso estadoCurso) {
        if (curso.getRama() == null) {
            throw new IllegalArgumentException("El curso no tiene rama configurada");
        }

        if (!curso.getRama().getId().equals(estadoCurso.getRama().getId())) {
            throw new IllegalArgumentException("La persona solo puede suscribirse a cursos de su misma rama");
        }
    }

    private boolean personaHabilitadaParaNivel(EventoCurso curso, EstadoCurso estadoCurso) {
        return estadoCurso.getNivelActual() == curso.getNivel();
    }

    private void validarCursoCompletadoContraEstado(Persona persona, EventoCurso curso, EstadoCurso estadoCurso) {
        if (persona.getRama() == null) {
            throw new IllegalArgumentException("La persona no tiene rama o estado de curso configurado");
        }

        if (curso.getRama() == null) {
            throw new IllegalArgumentException("El curso no tiene rama configurada");
        }

        if (!curso.getRama().getId().equals(persona.getRama().getId())) {
            throw new IllegalArgumentException("El curso completado debe ser de la misma rama de la persona");
        }

        if (!curso.getNivel().equals(estadoCurso.getNivelActual())) {
            throw new IllegalArgumentException(
                "El curso completado no corresponde al nivel actual de la persona: " + estadoCurso.getNivelActual()
            );
        }
    }


}




