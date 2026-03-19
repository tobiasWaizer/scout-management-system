package com.scout.scoutmanagement.backend.controller.api;

import com.scout.scoutmanagement.backend.dto.PersonaDTO;
import com.scout.scoutmanagement.domain.Pagos.Cuota;
import com.scout.scoutmanagement.domain.Pagos.Pago;
import com.scout.scoutmanagement.domain.Persona;
import com.scout.scoutmanagement.domain.Rama;
import com.scout.scoutmanagement.backend.response.ApiResponseBuilder;
import com.scout.scoutmanagement.backend.service.PagosService;
import com.scout.scoutmanagement.backend.service.PersonaService;
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

    private final PersonaService personaService;
    private final PagosService pagosService;
    private final ApiResponseBuilder apiResponseBuilder;

    public PersonasController(
        PersonaService personaService,
        PagosService pagosService,
        ApiResponseBuilder apiResponseBuilder
    ) {
        this.personaService = personaService;
        this.pagosService = pagosService;
        this.apiResponseBuilder = apiResponseBuilder;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> crearPersona(@Valid @RequestBody PersonaDTO personaDTO) {
        Persona personaCreada = personaService.crearPersona(personaDTO);
        Map<String, Object> respuesta = apiResponseBuilder.persona("Persona creada exitosamente", personaCreada);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @PutMapping("/{id_persona}")
    public ResponseEntity<Map<String, Object>> modificarPersona(
        @PathVariable("id_persona") Long idPersona,
        @Valid @RequestBody PersonaDTO personaDTO) {

        Persona personaActualizada = personaService.modificarPersona(personaDTO, idPersona);
        Map<String, Object> respuesta = apiResponseBuilder.persona("Persona modificada exitosamente", personaActualizada);
        return ResponseEntity.status(HttpStatus.OK).body(respuesta);
    }

    @GetMapping("/{id_persona}")
    public ResponseEntity<Map<String, Object>> obtenerPersona(@PathVariable("id_persona") Long idPersona) {
        Persona persona = personaService.obtenerPersona(idPersona);
        Map<String, Object> respuesta = apiResponseBuilder.persona("Persona encontrada exitosamente", persona);
        return ResponseEntity.status(HttpStatus.OK).body(respuesta);
    }

    @DeleteMapping("/{id_persona}")
    public ResponseEntity<Map<String, Object>> darDeBajaPersona(@PathVariable("id_persona") Long idPersona) {
        Persona persona = personaService.inhabilitarPersona(idPersona);
        Map<String, Object> respuesta = apiResponseBuilder.persona("Persona dada de baja exitosamente", persona);
        return ResponseEntity.status(HttpStatus.OK).body(respuesta);
    }

    @PatchMapping("/{id_persona}/partida")
    public ResponseEntity<Map<String, Object>> realizarPartida(@PathVariable("id_persona") Long idPersona, Long rama_id) {
        Persona persona = personaService.realizarPartida(idPersona, rama_id);
        Map<String, Object> respuesta = apiResponseBuilder.persona("Partida realizada exitosamente", persona);
        return ResponseEntity.status(HttpStatus.OK).body(respuesta);
    }

    @PatchMapping("/{id_persona}/jefe-rama")
    public ResponseEntity<Map<String, Object>> establecerJefeDeRama(@PathVariable("id_persona") Long idPersona) {
        Rama ramaActualizada = personaService.establecerJefeDeRama(idPersona);
        Persona persona = personaService.obtenerPersona(idPersona);

        Map<String, Object> respuesta = apiResponseBuilder.persona(
            "Persona establecida como jefe de la rama " + ramaActualizada.getNombre() + " exitosamente",
            persona
        );

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





