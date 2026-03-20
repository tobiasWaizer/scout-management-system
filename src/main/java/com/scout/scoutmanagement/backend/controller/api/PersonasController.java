package com.scout.scoutmanagement.backend.controller.api;

import com.scout.scoutmanagement.backend.dto.PersonaDTO;
import com.scout.scoutmanagement.backend.dto.CursoCompletadoDTO;
import com.scout.scoutmanagement.domain.EventoCurso;
import com.scout.scoutmanagement.domain.Pagos.Pago;
import com.scout.scoutmanagement.domain.Persona;
import com.scout.scoutmanagement.domain.Rama;
import com.scout.scoutmanagement.backend.response.ApiResponseBuilder;
import com.scout.scoutmanagement.backend.service.EventosService;
import com.scout.scoutmanagement.backend.service.PagosService;
import com.scout.scoutmanagement.backend.service.PersonasService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import java.time.Year;

@RestController
@RequestMapping("/api/personas")
public class PersonasController {

    private final PersonasService personasService;
    private final PagosService pagosService;
    private final EventosService eventosService;
    private final ApiResponseBuilder apiResponseBuilder;

    public PersonasController(
        PersonasService personasService,
        PagosService pagosService,
        EventosService eventosService,
        ApiResponseBuilder apiResponseBuilder
    ) {
        this.personasService = personasService;
        this.pagosService = pagosService;
        this.eventosService = eventosService;
        this.apiResponseBuilder = apiResponseBuilder;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> crearPersona(@Valid @RequestBody PersonaDTO personaDTO) {
        Persona personaCreada = personasService.crearPersona(personaDTO);
        Map<String, Object> respuesta = apiResponseBuilder.persona("Persona creada exitosamente", personaCreada);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @PutMapping("/{id_persona}")
    public ResponseEntity<Map<String, Object>> modificarPersona(
        @PathVariable("id_persona") Long idPersona,
        @Valid @RequestBody PersonaDTO personaDTO) {

        Persona personaActualizada = personasService.modificarPersona(personaDTO, idPersona);
        Map<String, Object> respuesta = apiResponseBuilder.persona("Persona modificada exitosamente", personaActualizada);
        return ResponseEntity.status(HttpStatus.OK).body(respuesta);
    }

    @GetMapping("/{id_persona}")
    public ResponseEntity<Map<String, Object>> obtenerPersona(@PathVariable("id_persona") Long idPersona) {
        Persona persona = personasService.obtenerPersona(idPersona);
        Map<String, Object> respuesta = apiResponseBuilder.persona("Persona encontrada exitosamente", persona);
        return ResponseEntity.status(HttpStatus.OK).body(respuesta);
    }

    @DeleteMapping("/{id_persona}")
    public ResponseEntity<Map<String, Object>> darDeBajaPersona(@PathVariable("id_persona") Long idPersona) {
        Persona persona = personasService.inhabilitarPersona(idPersona);
        Map<String, Object> respuesta = apiResponseBuilder.persona("Persona dada de baja exitosamente", persona);
        return ResponseEntity.status(HttpStatus.OK).body(respuesta);
    }

    @PatchMapping("/{id_persona}/partida")
    public ResponseEntity<Map<String, Object>> realizarPartida(@PathVariable("id_persona") Long idPersona, Long rama_id) {
        Persona persona = personasService.realizarPartida(idPersona, rama_id);
        Map<String, Object> respuesta = apiResponseBuilder.persona("Partida realizada exitosamente", persona);
        return ResponseEntity.status(HttpStatus.OK).body(respuesta);
    }

    @PatchMapping("/{id_persona}/jefe-rama")
    public ResponseEntity<Map<String, Object>> establecerJefeDeRama(@PathVariable("id_persona") Long idPersona) {
        Rama ramaActualizada = personasService.establecerJefeDeRama(idPersona);
        Persona persona = personasService.obtenerPersona(idPersona);

        Map<String, Object> respuesta = apiResponseBuilder.persona(
            "Persona establecida como jefe de la rama " + ramaActualizada.getNombre() + " exitosamente",
            persona
        );

        return ResponseEntity.status(HttpStatus.OK).body(respuesta);
    }

    @PostMapping("/{id_persona}/cursosCompletados") //esto no se si es necesario ver luego
    public ResponseEntity<Map<String, Object>> registrarCursoCompletado(
        @PathVariable("id_persona") Long idPersona,
        @Valid @RequestBody CursoCompletadoDTO cursoCompletadoDTO
    ) {
        Persona persona = personasService.obtenerPersona(idPersona);
        EventoCurso curso = eventosService.registrarCursoCompletado(idPersona, cursoCompletadoDTO.getIdCurso());
        Map<String, Object> respuesta = apiResponseBuilder.persona("Curso completado registrado exitosamente", persona);
        return ResponseEntity.status(HttpStatus.OK).body(respuesta);
    }

    @GetMapping("/{id_persona}/pagos")
    public ResponseEntity<Map<String, Object>> obtenerPagosDePersona(
        @PathVariable("id_persona") Long idPersona,
        @RequestParam(name = "anio", required = false) Integer anio
    ) {
        Integer anioFiltro = anio != null ? anio : Year.now().getValue();
        List<Pago> pagos = pagosService.obtenerPagosDePersonaId(idPersona, anioFiltro);
        Map<String, Object> respuesta = apiResponseBuilder.pagosDePersona("Pagos obtenidos exitosamente", idPersona, anioFiltro, pagos);
        return ResponseEntity.status(HttpStatus.OK).body(respuesta);
    }
}





