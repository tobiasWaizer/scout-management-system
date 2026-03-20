package com.scout.scoutmanagement.backend.controller.api;

import com.scout.scoutmanagement.domain.Rama;
import com.scout.scoutmanagement.domain.Persona;
import com.scout.scoutmanagement.backend.service.RamasService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/ramas")
public class RamasController {

    private final RamasService ramasService;

    public RamasController(RamasService ramasService) {
        this.ramasService = ramasService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> obtenerTodasLasRamas() {
        List<Rama> ramas = ramasService.obtenerTodasLasRamas();
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("mensaje", "Ramas obtenidas exitosamente");
        respuesta.put("cantidadRamas", ramas.size());
        respuesta.put("ramas", ramas.stream().map(this::construirRespuestaRama).toList());
        return ResponseEntity.status(HttpStatus.OK).body(respuesta);
    }

    private Map<String, Object> construirRespuestaRama(Rama rama) {
        Map<String, Object> ramaMap = new LinkedHashMap<>();
        ramaMap.put("id", rama.getId());
        ramaMap.put("nombre", rama.getNombre());
        ramaMap.put("jefeDeRama", construirRespuestaJefe(rama.getJefeDeRama()));
        return ramaMap;
    }

    private Map<String, Object> construirRespuestaJefe(Persona jefe) {
        if (jefe == null) {
            return null;
        }

        Map<String, Object> jefeMap = new LinkedHashMap<>();
        jefeMap.put("id", jefe.getId());
        jefeMap.put("nombre", jefe.getNombre());
        jefeMap.put("apellido", jefe.getApellido());
        jefeMap.put("mail", jefe.getMail());
        jefeMap.put("rol", jefe.getRol());
        return jefeMap;
    }
}




