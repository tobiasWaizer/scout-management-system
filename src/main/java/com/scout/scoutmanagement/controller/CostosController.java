package com.scout.scoutmanagement.controller;

import com.scout.scoutmanagement.DTO.CostoDTO;
import com.scout.scoutmanagement.domain.Pagos.CostosVariables;
import com.scout.scoutmanagement.service.CostosService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/costos")
public class CostosController {
    private final CostosService costosService;

    public CostosController(CostosService costosService) {
        this.costosService = costosService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> crearCosto(@Valid @RequestBody CostoDTO costoDTO) {
        CostosVariables costoCreado = costosService.crearCosto(costoDTO);
        Map<String, Object> respuesta = construirRespuestaCosto("Costo Manual creado exitosamente", costoCreado);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    private Map<String, Object> construirRespuestaCosto(String mensaje, CostosVariables costoCreado) {
        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("mensaje", mensaje);
        respuesta.put("id", costoCreado.getId());
        respuesta.put("nombre", costoCreado.getNombre());
        respuesta.put("cantidadCuotas", costoCreado.getCantidadCuotas());
        respuesta.put(
            "cuotas",
            costoCreado.getCuotas().stream().map(cuota -> {
                Map<String, Object> cuotaMap = new LinkedHashMap<>();
                cuotaMap.put("ordenCuota", cuota.getOrdenCuota());
                cuotaMap.put("monto", cuota.getMonto());
                return cuotaMap;
            }).toList()
        );
        respuesta.put("importeTotal", costoCreado.getImporte());
        respuesta.put(
            "creadoPorId",
            costoCreado.getCreadoPor() != null ? costoCreado.getCreadoPor().getId() : null
        );
        return respuesta;
    }
}
