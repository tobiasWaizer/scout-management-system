package com.scout.scoutmanagement.controller;

import com.scout.scoutmanagement.DTO.EducadorDTO;
import com.scout.scoutmanagement.domain.Educador;
import com.scout.scoutmanagement.service.EducadorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/educadores")
public class EducadoresController {

    private final EducadorService educadorService;

    public EducadoresController(EducadorService educadorService) {
        this.educadorService = educadorService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> crearEducador(@Valid @RequestBody EducadorDTO educadorDTO) {

        Map<String, Object> respuesta = new HashMap<>();

        Educador educadorCreado = educadorService.crearEducador(educadorDTO);

        respuesta.put("mensaje", "Educador creado exitosamente");
        respuesta.put("id", educadorCreado.getId());
        respuesta.put("nombre", educadorCreado.getNombre());
        respuesta.put("apellido", educadorCreado.getApellido());
        respuesta.put("dni", educadorCreado.getDni());
        respuesta.put("mail", educadorCreado.getMail());
        respuesta.put("ramaId", educadorCreado.getRamaQueDirige().getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }
    
    //TODO: ENDPOINT CAMBIAR DE RAMA, ENDPOINT ESTABLECER COMO JEFE DE RAMA, DAR DE BAJA,
}

