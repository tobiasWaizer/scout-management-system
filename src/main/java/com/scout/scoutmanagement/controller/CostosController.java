package com.scout.scoutmanagement.controller;

import com.scout.scoutmanagement.DTO.CostoDTO;
import com.scout.scoutmanagement.controller.response.ApiResponseBuilder;
import com.scout.scoutmanagement.domain.Pagos.CostosVariables;
import com.scout.scoutmanagement.service.CostosService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/costos")
public class CostosController {
    private final CostosService costosService;
    private final ApiResponseBuilder apiResponseBuilder;

    public CostosController(CostosService costosService, ApiResponseBuilder apiResponseBuilder) {
        this.costosService = costosService;
        this.apiResponseBuilder = apiResponseBuilder;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> crearCosto(@Valid @RequestBody CostoDTO costoDTO) {
        CostosVariables costoCreado = costosService.crearCosto(costoDTO);
        Map<String, Object> respuesta = apiResponseBuilder.costo("Costo Manual creado exitosamente", costoCreado);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }
}
