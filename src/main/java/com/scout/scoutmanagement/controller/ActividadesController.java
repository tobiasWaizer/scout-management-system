package com.scout.scoutmanagement.controller;

import com.scout.scoutmanagement.DTO.ActividadDTO;
import com.scout.scoutmanagement.domain.Actividad;
import com.scout.scoutmanagement.service.ActividadesService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/actividades")
public class ActividadesController {

    private final ActividadesService actividadesService;

    public ActividadesController(ActividadesService actividadesService) {
        this.actividadesService = actividadesService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> crearActividad(@Valid @RequestBody ActividadDTO actividadDTO) {
        Actividad actividadCreada = actividadesService.crearActividad(actividadDTO);
        Map<String, Object> respuesta = construirRespuestaActividad("Actividad creada exitosamente", actividadCreada);

        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    private Map<String, Object> construirRespuestaActividad(String mensaje, Actividad actividad) {
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("mensaje", mensaje);
        respuesta.put("id", actividad.getId());
        respuesta.put("titulo", actividad.getTitulo());
        respuesta.put("objetivo", actividad.getObjetivo());
        respuesta.put("desarrollo", actividad.getDesarrollo());
        respuesta.put("materiales", actividad.getMateriales());
        respuesta.put("recupero", actividad.getRecupero());
        respuesta.put("duracionMinutos", actividad.getDuracionMinutos());
        respuesta.put("educadorResponsableId", actividad.getEducadorResponsable().getId());
        respuesta.put("beneficiarioACargoId", actividad.getBeneficiarioACargo().getId());
        return respuesta;
    }

}

