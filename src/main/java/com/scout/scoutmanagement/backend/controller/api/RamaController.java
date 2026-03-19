package com.scout.scoutmanagement.backend.controller.api;

import com.scout.scoutmanagement.domain.Rama;
import com.scout.scoutmanagement.backend.service.RamaService;
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
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("mensaje", "Ramas obtenidas exitosamente");
        respuesta.put("cantidadRamas", ramas.size());
        respuesta.put("ramas", ramas);
        return ResponseEntity.status(HttpStatus.OK).body(respuesta);
    }
}




