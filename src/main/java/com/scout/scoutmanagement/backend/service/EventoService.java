package com.scout.scoutmanagement.backend.service;

import com.scout.scoutmanagement.backend.dto.EventoDTO;
import com.scout.scoutmanagement.domain.*;
import com.scout.scoutmanagement.backend.repository.EventoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EventoService {

    private final EventoRepository eventoRepository;
    private final PersonaService personaService;
    private final RamaService ramaService;

    public EventoService(
        EventoRepository eventoRepository,
        PersonaService personaService,
        RamaService ramaService
    ) {
        this.eventoRepository = eventoRepository;
        this.personaService = personaService;
        this.ramaService = ramaService;
    }

    public Evento crearEvento(EventoDTO eventoDTO) {
        Evento evento = construirEventoSegunTipo(eventoDTO);
        AlcanceEvento alcanceEvento = evento.getAlcanceEvento();


        Rama rama = resolverRamaParaEvento(eventoDTO, alcanceEvento);
        LocalDateTime fechaFin = resolverFechaFin(eventoDTO);

        evento.setTitulo(eventoDTO.getTitulo());
        evento.setFechaInicio(eventoDTO.getFechaInicio());
        evento.setFechaFin(fechaFin);
        evento.setRama(rama);

        return eventoRepository.save(evento);
    }

    public List<Evento> obtenerEventosSegun(LocalDateTime desde, LocalDateTime hasta, Long personaId, List<Long> ramaIds, List<String> tiposEvento, List<String> alcancesEvento) {
        List<Evento> eventos = eventoRepository.obtenerEventosEnRango(desde, hasta);

        /*if (personaId != null) { //TODO: CUANDO IMPLEMENTEMOS LAS SESIONES SI USAREMOS EL PERSONA ID PARA VER QUE EVENTOS DEJAMOS VISIBLES
            Persona persona = personaService.obtenerPersona(personaId);
            eventos = filtrarEventosVisiblesParaPersona(eventos, persona);
        }*/

        if (ramaIds != null && !ramaIds.isEmpty()) {
            Set<Long> ramasSolicitadas = ramaIds.stream().collect(Collectors.toSet());
            ramasSolicitadas.forEach(ramaService::obtenerRamaPorId);

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

    private List<Evento> filtrarEventosPorTipos(List<Evento> eventos, List<String> tiposEvento) {
        if (tiposEvento == null || tiposEvento.isEmpty()) {
            return eventos;
        }

        return eventos.stream()
            .filter(evento -> {
                TipoEvento tipoDelEvento = evento.getTipo();
                return tiposEvento.stream()
                    .anyMatch(tipoStr -> {
                        try {
                            TipoEvento tipo = TipoEvento.valueOf(tipoStr.toUpperCase());
                            return tipoDelEvento == tipo;
                        } catch (IllegalArgumentException e) {
                            throw new IllegalArgumentException("Tipo de evento inválido: " + tipoStr);
                        }
                    });
            })
            .toList();
    }

    private List<Evento> filtrarEventosPorAlcances(List<Evento> eventos, List<String> alcancesEvento) {
        if (alcancesEvento == null || alcancesEvento.isEmpty()) {
            return eventos;
        }

        return eventos.stream()
            .filter(evento -> {
                AlcanceEvento alcanceDelEvento = evento.getAlcanceEvento();
                return alcancesEvento.stream()
                    .anyMatch(alcanceStr -> {
                        try {
                            AlcanceEvento alcance = AlcanceEvento.valueOf(alcanceStr.toUpperCase());
                            return alcanceDelEvento == alcance;
                        } catch (IllegalArgumentException e) {
                            throw new IllegalArgumentException("Alcance de evento inválido: " + alcanceStr);
                        }
                    });
            })
            .toList();
    }

    private List<Evento> filtrarEventosVisiblesParaPersona(List<Evento> eventos, Persona persona) {
        return eventos.stream()
            .filter(evento -> {
                if (evento.getAlcanceEvento() == AlcanceEvento.GENERAL) {
                    return true;
                }
                if (evento.getAlcanceEvento() == AlcanceEvento.RAMA) {
                    return evento.getRama() != null
                        && persona.getRama() != null
                        && evento.getRama().getId().equals(persona.getRama().getId());
                }
                if (evento.getAlcanceEvento() == AlcanceEvento.EDUCADORES) {
                    return persona.getRol() == Rol.EDUCADOR;
                }
                return false;
            })
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


    private Evento construirEventoSegunTipo(EventoDTO eventoDTO) {
        return switch (eventoDTO.getTipoEvento()) {
            case ACTIVIDAD -> construirActividad(eventoDTO);
            case CURSO -> construirCurso(eventoDTO);
            case CAMPAMENTO -> construirCampamento(eventoDTO);
            case BINGO -> construirBingo(eventoDTO);
            case CONSEJO -> construirConsejo();
        };
    }

    private EventoActividad construirActividad(EventoDTO eventoDTO) {
        Persona educador = personaService.obtenerEducador(eventoDTO.getEducadorResponsableId());
        Persona beneficiario = eventoDTO.getBeneficiarioACargoId() != null
            ? personaService.obtenerPersona(eventoDTO.getBeneficiarioACargoId())
            : educador;

        if (eventoDTO.getDuracionMinutos() == null) {
            throw new IllegalArgumentException("Para ACTIVIDAD se requiere duracionMinutos");
        }

        return new EventoActividad(
            eventoDTO.getTitulo(),
            eventoDTO.getObjetivo(),
            eventoDTO.getDesarrollo(),
            eventoDTO.getMateriales(),
            eventoDTO.getRecupero(),
            eventoDTO.getDuracionMinutos(),
            educador,
            beneficiario
        );
    }

    private EventoCurso construirCurso(EventoDTO eventoDTO) {
        if (eventoDTO.getNivelCurso() == null) {
            throw new IllegalArgumentException("Para CURSO se requiere nivelCurso");
        }

        EventoCurso eventoCurso = new EventoCurso();
        eventoCurso.setNivel(eventoDTO.getNivelCurso());
        eventoCurso.setSuscriptos(personaService.obtenerPersonasPorIds(eventoDTO.getSuscriptosCursoIds(), "suscriptosCursoIds"));
        return eventoCurso;
    }

    private EventoCampamento construirCampamento(EventoDTO eventoDTO) {
        if (eventoDTO.getLugar() == null || eventoDTO.getLugar().isBlank()) {
            throw new IllegalArgumentException("Para CAMPAMENTO se requiere lugar");
        }

        if (eventoDTO.getAlcanceEvento() == null) {
            throw new IllegalArgumentException("Para CAMPAMENTO se requiere alcanceEvento");
        }

        return new EventoCampamento(eventoDTO.getAlcanceEvento(), eventoDTO.getLugar(), eventoDTO.getContactoLugar());
    }

    private EventoBingo construirBingo(EventoDTO eventoDTO) {
        List<Persona> cocineros = personaService.obtenerPersonasPorIds(eventoDTO.getCocinerosIds(), "cocinerosIds");
        List<Persona> cartoneros = personaService.obtenerPersonasPorIds(eventoDTO.getCartonerosIds(), "cartonerosIds");
        List<Persona> bacha = personaService.obtenerPersonasPorIds(eventoDTO.getBachaIds(), "bachaIds");

        return new EventoBingo(eventoDTO.getTitulo(), cocineros, cartoneros, bacha);
    }

    private EventoConsejoDeGrupo construirConsejo() {
        return new EventoConsejoDeGrupo();
    }

    private LocalDateTime resolverFechaFin(EventoDTO eventoDTO) {
        if (eventoDTO.getFechaFin() != null) {
            return eventoDTO.getFechaFin();
        }

        if (eventoDTO.getTipoEvento() == TipoEvento.ACTIVIDAD && eventoDTO.getDuracionMinutos() != null) {
            return eventoDTO.getFechaInicio().plusMinutes(eventoDTO.getDuracionMinutos());
        }

        return eventoDTO.getFechaInicio().plusHours(1);
    }

    private Rama resolverRamaParaEvento(EventoDTO eventoDTO, AlcanceEvento alcanceEvento) {
        if (alcanceEvento != AlcanceEvento.RAMA) {
            return null;
        }

        if (eventoDTO.getRamaId() == null) {
            throw new IllegalArgumentException("Para eventos de rama se requiere ramaId");
        }

        return ramaService.obtenerRamaPorId(eventoDTO.getRamaId());
    }
}




