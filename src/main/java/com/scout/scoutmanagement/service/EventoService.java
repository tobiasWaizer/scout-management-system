package com.scout.scoutmanagement.service;

import com.scout.scoutmanagement.DTO.EventoDTO;
import com.scout.scoutmanagement.domain.*;
import com.scout.scoutmanagement.repository.EventoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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
        AlcanceEvento alcanceEvento = eventoDTO.getAlcanceEvento() != null
            ? eventoDTO.getAlcanceEvento()
            : AlcanceEvento.RAMA;

        if (alcanceEvento == AlcanceEvento.INDIVIDUAL) { //todo:sacar despues 
            throw new IllegalArgumentException("Los eventos INDIVIDUAL no estan soportados en este flujo");
        }

        Rama rama = resolverRamaParaEvento(eventoDTO, alcanceEvento);
        Evento evento = construirEventoSegunTipo(eventoDTO);
        LocalDateTime fechaFin = resolverFechaFin(eventoDTO);

        evento.setTitulo(eventoDTO.getTitulo());
        evento.setFechaInicio(eventoDTO.getFechaInicio());
        evento.setFechaFin(fechaFin);
        evento.setAlcanceEvento(alcanceEvento);
        evento.setRama(rama);

        return eventoRepository.save(evento);
    }

    public List<Evento> obtenerEventosEnRango(LocalDateTime desde, LocalDateTime hasta, Long personaId, Long ramaId, List<String> tiposEvento) {
        if (ramaId != null) {
            ramaService.obtenerRamaPorId(ramaId);
            List<Evento> eventos = eventoRepository.obtenerEventosDeRamaEnRango(
                desde,
                hasta,
                ramaId,
                AlcanceEvento.RAMA
            );
            return filtrarEventosPorTipos(eventos, tiposEvento);
        }

        if (personaId == null) {
            List<Evento> eventos = eventoRepository.obtenerEventosEnRango(desde, hasta);
            return filtrarEventosPorTipos(eventos, tiposEvento);
        }

        Persona persona = personaService.obtenerPersona(personaId);

        if (persona.getRama() == null) {
            throw new IllegalArgumentException("La persona con ID " + personaId + " no tiene rama asignada");
        }

        List<Evento> eventos = eventoRepository.obtenerEventosVisiblesParaPersonaEnRango(
            desde,
            hasta,
            persona.getRama().getId(),
            AlcanceEvento.GENERAL,
            AlcanceEvento.RAMA
        );
        return filtrarEventosPorTipos(eventos, tiposEvento);
    }

    private List<Evento> filtrarEventosPorTipos(List<Evento> eventos, List<String> tiposEvento) {
        if (tiposEvento == null || tiposEvento.isEmpty()) {
            return eventos;
        }

        return eventos.stream()
            .filter(evento -> {
                TipoEvento tipoDelEvento = obtenerTipoDeEvento(evento);
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

    private TipoEvento obtenerTipoDeEvento(Evento evento) {
        if (evento instanceof Actividad) {
            return TipoEvento.ACTIVIDAD;
        } else if (evento instanceof Curso) {
            return TipoEvento.CURSO;
        } else if (evento instanceof EventoCampamento) {
            return TipoEvento.CAMPAMENTO;
        } else if (evento instanceof EventoBingo) {
            return TipoEvento.BINGO;
        }
        return TipoEvento.ACTIVIDAD;
    }

    private Evento construirEventoSegunTipo(EventoDTO eventoDTO) {
        return switch (eventoDTO.getTipoEvento()) {
            case ACTIVIDAD -> construirActividad(eventoDTO);
            case CURSO -> construirCurso(eventoDTO);
            case CAMPAMENTO -> construirCampamento(eventoDTO);
            case BINGO -> construirBingo(eventoDTO);
        };
    }

    private Actividad construirActividad(EventoDTO eventoDTO) {
        Persona educador = personaService.obtenerEducador(eventoDTO.getEducadorResponsableId());
        Persona beneficiario = eventoDTO.getBeneficiarioACargoId() != null
            ? personaService.obtenerPersona(eventoDTO.getBeneficiarioACargoId())
            : educador;

        if (eventoDTO.getDuracionMinutos() == null) {
            throw new IllegalArgumentException("Para ACTIVIDAD se requiere duracionMinutos");
        }

        return new Actividad(
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

    private Curso construirCurso(EventoDTO eventoDTO) {
        if (eventoDTO.getNivelCurso() == null) {
            throw new IllegalArgumentException("Para CURSO se requiere nivelCurso");
        }

        Curso curso = new Curso();
        curso.setNivel(eventoDTO.getNivelCurso());
        curso.setSuscriptos(personaService.obtenerPersonasPorIds(eventoDTO.getSuscriptosCursoIds(), "suscriptosCursoIds"));
        return curso;
    }

    private EventoCampamento construirCampamento(EventoDTO eventoDTO) {
        if (eventoDTO.getLugar() == null || eventoDTO.getLugar().isBlank()) {
            throw new IllegalArgumentException("Para CAMPAMENTO se requiere lugar");
        }

        EventoCampamento campamento = new EventoCampamento();
        campamento.setLugar(eventoDTO.getLugar());
        campamento.setContactoLugar(eventoDTO.getContactoLugar());
        return campamento;
    }

    private EventoBingo construirBingo(EventoDTO eventoDTO) {
        List<Persona> cocineros = personaService.obtenerPersonasPorIds(eventoDTO.getCocinerosIds(), "cocinerosIds");
        List<Persona> cartoneros = personaService.obtenerPersonasPorIds(eventoDTO.getCartonerosIds(), "cartonerosIds");
        List<Persona> bacha = personaService.obtenerPersonasPorIds(eventoDTO.getBachaIds(), "bachaIds");

        return new EventoBingo(eventoDTO.getTitulo(), cocineros, cartoneros, bacha);
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


