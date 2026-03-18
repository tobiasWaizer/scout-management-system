package com.scout.scoutmanagement.controller;

import com.scout.scoutmanagement.DTO.RamaDTO;
import com.scout.scoutmanagement.domain.Rama;
import com.scout.scoutmanagement.service.RamaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ramas")
public class RamaController {

    private final RamaService ramaService;

    public RamaController(RamaService ramaService) {
        this.ramaService = ramaService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> obtenerTodasLasRamas() {
        List<Rama> ramas = ramaService.obtenerTodasLasRamas();
        List<RamaDTO> ramasDTOs = ramas.stream()
            .map(rama -> new RamaDTO(rama.getId(), rama.getNombre()))
            .toList();
        
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("mensaje", "Ramas obtenidas exitosamente");
        respuesta.put("cantidadRamas", ramasDTOs.size());
        respuesta.put("ramas", ramasDTOs);
        return ResponseEntity.status(HttpStatus.OK).body(respuesta);
    }
}

