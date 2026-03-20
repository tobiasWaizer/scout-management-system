package com.scout.scoutmanagement.backend.service;

import com.scout.scoutmanagement.backend.dto.EventoDTO;
import com.scout.scoutmanagement.domain.AlcanceEvento;
import com.scout.scoutmanagement.domain.Evento;
import com.scout.scoutmanagement.domain.EventoActividad;
import com.scout.scoutmanagement.domain.EventoBingo;
import com.scout.scoutmanagement.domain.EventoCampamento;
import com.scout.scoutmanagement.domain.EventoConsejoDeGrupo;
import com.scout.scoutmanagement.domain.EventoCurso;
import com.scout.scoutmanagement.domain.Persona;
import com.scout.scoutmanagement.domain.Rama;
import com.scout.scoutmanagement.domain.TipoEvento;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CreadorDeEventos {

    private final PersonasService personasService;
    private final RamasService ramasService;

    public CreadorDeEventos(PersonasService personasService, RamasService ramasService) {
        this.personasService = personasService;
        this.ramasService = ramasService;
    }

    public Evento crearDesde(EventoDTO eventoDTO) {
        Evento evento = crearEventoVacio(eventoDTO.getTipoEvento());
        actualizarDesde(evento, eventoDTO);
        return evento;
    }

    public void actualizarDesde(Evento evento, EventoDTO eventoDTO) {
        if (evento.getTipo() != eventoDTO.getTipoEvento()) {
            throw new IllegalArgumentException("No se puede cambiar el tipo de evento de " + evento.getTipo() + " a " + eventoDTO.getTipoEvento());
        }

        aplicarCamposEspecificos(evento, eventoDTO);
        aplicarCamposComunes(evento, eventoDTO);
    }

    private Evento crearEventoVacio(TipoEvento tipoEvento) {
        return switch (tipoEvento) {
            case ACTIVIDAD -> new EventoActividad();
            case CURSO -> new EventoCurso();
            case CAMPAMENTO -> new EventoCampamento();
            case BINGO -> new EventoBingo();
            case CONSEJO -> new EventoConsejoDeGrupo();
        };
    }

    private void aplicarCamposEspecificos(Evento evento, EventoDTO eventoDTO) {
        if (evento instanceof EventoActividad actividad) {
            actualizarActividad(actividad, eventoDTO);
            return;
        }

        if (evento instanceof EventoCurso curso) {
            actualizarCurso(curso, eventoDTO);
            return;
        }

        if (evento instanceof EventoCampamento campamento) {
            actualizarCampamento(campamento, eventoDTO);
            return;
        }

        if (evento instanceof EventoBingo bingo) {
            actualizarBingo(bingo, eventoDTO);
            return;
        }

        if (evento instanceof EventoConsejoDeGrupo consejo) {
            consejo.setAlcanceEvento(AlcanceEvento.EDUCADORES);
            return;
        }

        throw new IllegalArgumentException("Tipo de evento no soportado: " + evento.getClass().getSimpleName());
    }

    private void aplicarCamposComunes(Evento evento, EventoDTO eventoDTO) {
        evento.setTitulo(eventoDTO.getTitulo());
        evento.setFechaInicio(eventoDTO.getFechaInicio());
        evento.setFechaFin(eventoDTO.getFechaFin());

        Rama rama = resolverRamaParaEvento(eventoDTO, evento.getAlcanceEvento());
        evento.setRama(rama);
    }

    private void actualizarActividad(EventoActividad actividad, EventoDTO eventoDTO) {
        Persona educador = personasService.obtenerEducador(eventoDTO.getEducadorResponsableId());
        Persona beneficiario = eventoDTO.getBeneficiarioACargoId() != null
            ? personasService.obtenerPersona(eventoDTO.getBeneficiarioACargoId())
            : educador;

        if (eventoDTO.getDuracionMinutos() == null) {
            throw new IllegalArgumentException("Para ACTIVIDAD se requiere duracionMinutos");
        }

        actividad.setAlcanceEvento(AlcanceEvento.RAMA);
        actividad.setObjetivo(eventoDTO.getObjetivo());
        actividad.setDesarrollo(eventoDTO.getDesarrollo());
        actividad.setMateriales(eventoDTO.getMateriales());
        actividad.setRecupero(eventoDTO.getRecupero());
        actividad.setDuracionMinutos(eventoDTO.getDuracionMinutos());
        actividad.setEducadorResponsable(educador);
        actividad.setBeneficiarioACargo(beneficiario);
    }

    private void actualizarCurso(EventoCurso eventoCurso, EventoDTO eventoDTO) {
        if (eventoDTO.getNivelCurso() == null) {
            throw new IllegalArgumentException("Para CURSO se requiere nivelCurso");
        }

        eventoCurso.setAlcanceEvento(AlcanceEvento.EDUCADORES);
        eventoCurso.setNivel(eventoDTO.getNivelCurso());
        eventoCurso.setSuscriptos(personasService.obtenerPersonasPorIds(eventoDTO.getSuscriptosCursoIds(), "suscriptosCursoIds"));
    }

    private void actualizarCampamento(EventoCampamento campamento, EventoDTO eventoDTO) {
        if (eventoDTO.getLugar() == null || eventoDTO.getLugar().isBlank()) {
            throw new IllegalArgumentException("Para CAMPAMENTO se requiere lugar");
        }

        if (eventoDTO.getAlcanceEvento() == null) {
            throw new IllegalArgumentException("Para CAMPAMENTO se requiere alcanceEvento");
        }

        campamento.setAlcanceEvento(eventoDTO.getAlcanceEvento());
        campamento.setLugar(eventoDTO.getLugar());
        campamento.setContactoLugar(eventoDTO.getContactoLugar());
    }

    private void actualizarBingo(EventoBingo bingo, EventoDTO eventoDTO) {
        List<Persona> cocineros = personasService.obtenerPersonasPorIds(eventoDTO.getCocinerosIds(), "cocinerosIds");
        List<Persona> cartoneros = personasService.obtenerPersonasPorIds(eventoDTO.getCartonerosIds(), "cartonerosIds");
        List<Persona> bacha = personasService.obtenerPersonasPorIds(eventoDTO.getBachaIds(), "bachaIds");

        bingo.setAlcanceEvento(AlcanceEvento.GENERAL);
        bingo.setCocineros(cocineros);
        bingo.setCartoneros(cartoneros);
        bingo.setBacha(bacha);
    }

    private Rama resolverRamaParaEvento(EventoDTO eventoDTO, AlcanceEvento alcanceEvento) {
        if (alcanceEvento != AlcanceEvento.RAMA && eventoDTO.getTipoEvento() != TipoEvento.CURSO) {
            return null;
        }

        if (eventoDTO.getRamaId() == null) {
            throw new IllegalArgumentException("Para eventos de rama o cursos se requiere ramaId");
        }

        return ramasService.obtenerRamaPorId(eventoDTO.getRamaId());
    }
}

