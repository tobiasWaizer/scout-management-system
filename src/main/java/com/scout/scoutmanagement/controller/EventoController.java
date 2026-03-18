package com.scout.scoutmanagement.controller;

import com.scout.scoutmanagement.DTO.EventoDTO;
import com.scout.scoutmanagement.controller.response.ApiResponseBuilder;
import com.scout.scoutmanagement.domain.Evento;
import com.scout.scoutmanagement.service.EventoService;
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

    @GetMapping("/rango")
    public ResponseEntity<Map<String, Object>> obtenerEventosEnRango(
        @RequestParam("desde") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
        @RequestParam("hasta") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta,
        @RequestParam(name = "personaId", required = false) Long personaId,
        @RequestParam(name = "ramaId", required = false) Long ramaId,
        @RequestParam(name = "tipoEvento", required = false) List<String> tiposEvento
    ) {
        if (desde.isAfter(hasta)) {
            throw new IllegalArgumentException("El parametro 'desde' no puede ser mayor a 'hasta'");
        }

        List<Evento> eventos = eventoService.obtenerEventosEnRango(desde, hasta, personaId, ramaId, tiposEvento);
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

