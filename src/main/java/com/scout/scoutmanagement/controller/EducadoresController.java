package com.scout.scoutmanagement.controller;

import com.scout.scoutmanagement.DTO.EducadorDTO;
import com.scout.scoutmanagement.domain.Educador;
import com.scout.scoutmanagement.service.EducadorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/educadores")
public class EducadoresController {

    @Autowired
    private EducadorService educadorService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> crearEducador(
            @Valid @RequestBody EducadorDTO educadorDTO,
            BindingResult bindingResult) {

        Map<String, Object> respuesta = new HashMap<>();

        if (bindingResult.hasErrors()) {
            respuesta.put("mensaje", "Datos inválidos");
            respuesta.put("errores", bindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .toArray());
            return ResponseEntity.badRequest().body(respuesta);
        }

        try {
            Educador educadorCreado = educadorService.crearEducador(educadorDTO);

            respuesta.put("mensaje", "Educador creado exitosamente");
            respuesta.put("id", educadorCreado.getId());
            respuesta.put("nombre", educadorCreado.getNombre());
            respuesta.put("apellido", educadorCreado.getApellido());
            respuesta.put("dni", educadorCreado.getDni());
            respuesta.put("mail", educadorCreado.getMail());
            respuesta.put("ramaId", educadorCreado.getRamaQueDirige().getId());

            return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);

        } catch (IllegalArgumentException e) {
            respuesta.put("mensaje", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(respuesta);
        } catch (Exception e) {
            respuesta.put("mensaje", "Error interno del servidor");
            respuesta.put("detalle", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuesta);
        }
    }
}

