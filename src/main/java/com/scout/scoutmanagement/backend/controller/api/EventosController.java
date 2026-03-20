package com.scout.scoutmanagement.backend.controller.api;

import com.scout.scoutmanagement.backend.dto.EventoDTO;
import com.scout.scoutmanagement.backend.dto.EventoFechasDTO;
import com.scout.scoutmanagement.backend.dto.SuscripcionCursoDTO;
import com.scout.scoutmanagement.backend.response.ApiResponseBuilder;
import com.scout.scoutmanagement.domain.Evento;
import com.scout.scoutmanagement.backend.service.EventosService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/eventos")
public class EventosController {

    private final EventosService eventosService;
    private final ApiResponseBuilder apiResponseBuilder;

    public EventosController(EventosService eventosService, ApiResponseBuilder apiResponseBuilder) {
        this.eventosService = eventosService;
        this.apiResponseBuilder = apiResponseBuilder;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> crearEvento(@Valid @RequestBody EventoDTO eventoDTO) {
        Evento eventoCreado = eventosService.crearEvento(eventoDTO);
        Map<String, Object> respuesta = apiResponseBuilder.evento("Evento creado exitosamente", eventoCreado);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @GetMapping("/{idEvento}")
    public ResponseEntity<Map<String, Object>> obtenerEventoPorId(@PathVariable Long idEvento) {
        Evento evento = eventosService.obtenerEventoPorId(idEvento);
        Map<String, Object> respuesta = apiResponseBuilder.evento("Evento obtenido exitosamente", evento);
        return ResponseEntity.status(HttpStatus.OK).body(respuesta);
    }



    @DeleteMapping("/{idEvento}")
    public ResponseEntity<Map<String, Object>> eliminarEvento(@PathVariable Long idEvento) {
        eventosService.eliminarEvento(idEvento);

        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("mensaje", "Evento eliminado exitosamente");
        respuesta.put("id", idEvento);
        return ResponseEntity.status(HttpStatus.OK).body(respuesta);
    }

    @PutMapping("/{idEvento}")
    public ResponseEntity<Map<String, Object>> modificarEvento(
        @PathVariable Long idEvento,
        @Valid @RequestBody EventoDTO eventoDTO
    ) {
        Evento eventoModificado = eventosService.modificarEvento(idEvento, eventoDTO);
        Map<String, Object> respuesta = apiResponseBuilder.evento("Evento modificado exitosamente", eventoModificado);
        return ResponseEntity.status(HttpStatus.OK).body(respuesta);
    }

    @PatchMapping("/{idEvento}/fechas")
    public ResponseEntity<Map<String, Object>> reprogramarEvento(
        @PathVariable Long idEvento,
        @Valid @RequestBody EventoFechasDTO eventoFechasDTO
    ) {
        Evento eventoReprogramado = eventosService.reprogramarEvento(
            idEvento,
            eventoFechasDTO.getFechaInicio(),
            eventoFechasDTO.getFechaFin()
        );
        Map<String, Object> respuesta = apiResponseBuilder.evento("Evento reprogramado exitosamente", eventoReprogramado);
        return ResponseEntity.status(HttpStatus.OK).body(respuesta);
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

        List<Evento> eventos = eventosService.obtenerEventosSegun(desde, hasta, personaId, ramaIds, tiposEvento, alcancesEvento);
        Map<String, Object> respuesta = apiResponseBuilder.eventosCalendario(
            "Eventos obtenidos exitosamente",
            desde,
            hasta,
            personaId,
            eventos
        );

        return ResponseEntity.status(HttpStatus.OK).body(respuesta);
    }

    @PostMapping("/{idCurso}/suscriptores")
    public ResponseEntity<Map<String, Object>> suscribirPersonaACurso(
        @PathVariable Long idCurso,
        @Valid @RequestBody SuscripcionCursoDTO suscripcionCursoDTO
    ) {
        Evento eventoActualizado = eventosService.suscribirPersonaACurso(idCurso, suscripcionCursoDTO.getPersonaId());
        Map<String, Object> respuesta = apiResponseBuilder.evento("Suscripcion realizada exitosamente", eventoActualizado);
        return ResponseEntity.status(HttpStatus.OK).body(respuesta);
    }

}





