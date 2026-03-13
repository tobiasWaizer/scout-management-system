package com.scout.scoutmanagement.controller;

import com.scout.scoutmanagement.DTO.PersonaDTO;
import com.scout.scoutmanagement.domain.Persona;
import com.scout.scoutmanagement.domain.Rama;
import com.scout.scoutmanagement.service.PersonaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/personas")
public class PersonasController {

    private final PersonaService personaService;

    public PersonasController(PersonaService personaService) {
        this.personaService = personaService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> crearPersona(@Valid @RequestBody PersonaDTO personaDTO) {
        Persona personaCreada = personaService.crearPersona(personaDTO);
        Map<String, Object> respuesta = construirRespuestaPersona("Persona creada exitosamente", personaCreada);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @PutMapping("/{id_persona}")
    public ResponseEntity<Map<String, Object>> modificarPersona(
        @PathVariable("id_persona") Long idPersona,
        @Valid @RequestBody PersonaDTO personaDTO) {

        Persona personaActualizada = personaService.modificarPersona(personaDTO, idPersona);
        Map<String, Object> respuesta = construirRespuestaPersona("Persona modificada exitosamente", personaActualizada);
        return ResponseEntity.status(HttpStatus.OK).body(respuesta);
    }

    @GetMapping("/{id_persona}")
    public ResponseEntity<Map<String, Object>> obtenerPersona(@PathVariable("id_persona") Long idPersona) {
        Persona persona = personaService.obtenerPersona(idPersona);
        Map<String, Object> respuesta = construirRespuestaPersona("Persona encontrada exitosamente", persona);
        return ResponseEntity.status(HttpStatus.OK).body(respuesta);
    }

    @DeleteMapping("/{id_persona}")
    public ResponseEntity<Map<String, Object>> darDeBajaPersona(@PathVariable("id_persona") Long idPersona) {
        Persona persona = personaService.inhabilitarPersona(idPersona);
        Map<String, Object> respuesta = construirRespuestaPersona("Persona dada de baja exitosamente", persona);
        return ResponseEntity.status(HttpStatus.OK).body(respuesta);
    }

    @PatchMapping("/{id_persona}/partida")
    public ResponseEntity<Map<String, Object>> realizarPartida(@PathVariable("id_persona") Long idPersona, Long rama_id) {
        Persona persona = personaService.realizarPartida(idPersona, rama_id);
        Map<String, Object> respuesta = construirRespuestaPersona("Partida realizada exitosamente", persona);
        return ResponseEntity.status(HttpStatus.OK).body(respuesta);
    }

    @PatchMapping("/{id_persona}/jefe-rama")
    public ResponseEntity<Map<String, Object>> establecerJefeDeRama(@PathVariable("id_persona") Long idPersona) {
        Rama ramaActualizada = personaService.establecerJefeDeRama(idPersona);
        Persona persona = personaService.obtenerPersona(idPersona);

        Map<String, Object> respuesta = construirRespuestaPersona(
            "Persona establecida como jefe de la rama " + ramaActualizada.getNombre() + " exitosamente",
            persona
        );

        return ResponseEntity.status(HttpStatus.OK).body(respuesta);
    }

    private Map<String, Object> construirRespuestaPersona(String mensaje, Persona persona) {
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("mensaje", mensaje);
        respuesta.put("id", persona.getId());
        respuesta.put("nombre", persona.getNombre());
        respuesta.put("apellido", persona.getApellido());
        respuesta.put("dni", persona.getDni());
        respuesta.put("mail", persona.getMail());
        respuesta.put("rol", persona.getRol());
        respuesta.put("ramaId", persona.getRama() != null ? persona.getRama().getId() : null);
        respuesta.put("activo", persona.getActivo());
        return respuesta;
    }
}

//TODO: vamos a ponernos a investigar un poco sobre como se gestionan los pagos en un sistema comun,
// como se registran si se tienen que realizar pagos cada cierta frecuencia,
// solicitar por mail cada x dia del mes que se pague la cuota mensual,
// tmbn la afiliacion, pensar como mostrar los datos de los pagos, como se pagan los campamentos,
// como se guardan las fotos de papeles en nuestro sistema etc..., que hacer si se quieren pagar varias cuotas juntas,