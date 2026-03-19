package com.scout.scoutmanagement.backend.controller.api;

import com.scout.scoutmanagement.backend.dto.EventoDTO;
import com.scout.scoutmanagement.backend.response.ApiResponseBuilder;
import com.scout.scoutmanagement.domain.Evento;
import com.scout.scoutmanagement.backend.service.EventoService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/eventos")
public class EventoController {

    private final EventoService eventoService;
    private final ApiResponseBuilder apiResponseBuilder;

    public EventoController(EventoService eventoService, ApiResponseBuilder apiResponseBuilder) {
        this.eventoService = eventoService;
        this.apiResponseBuilder = apiResponseBuilder;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> crearEvento(@Valid @RequestBody EventoDTO eventoDTO) {
        Evento eventoCreado = eventoService.crearEvento(eventoDTO);
        Map<String, Object> respuesta = apiResponseBuilder.evento("Evento creado exitosamente", eventoCreado);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> obtenerEventos(
        @RequestParam("desde") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
        @RequestParam("hasta") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta,
        @RequestParam(name = "personaId", required = false) Long personaId,
        @RequestParam(name = "ramaIds", required = false) List<Long> ramaIds,
        @RequestParam(name = "tipoEvento", required = false) List<String> tiposEvento,
        @RequestParam(name = "alcanceEvento", required = false) List<String> alcancesEvento
    ) {
        if (desde.isAfter(hasta)) {
            throw new IllegalArgumentException("El parametro 'desde' no puede ser mayor a 'hasta'");
        }

        List<Evento> eventos = eventoService.obtenerEventosSegun(desde, hasta, personaId, ramaIds, tiposEvento, alcancesEvento);
        Map<String, Object> respuesta = apiResponseBuilder.eventosCalendario(
            "Eventos obtenidos exitosamente",
            desde,
            hasta,
            personaId,
            eventos
        );

        return ResponseEntity.status(HttpStatus.OK).body(respuesta);
    }
}





