package com.scout.scoutmanagement.controller;

import com.scout.scoutmanagement.DTO.PagoDTO;
import com.scout.scoutmanagement.domain.Pagos.Pago;
import com.scout.scoutmanagement.controller.response.ApiResponseBuilder;
import com.scout.scoutmanagement.service.PagosService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/pagos")
public class PagosController {

    private final PagosService pagosService;
    private final ApiResponseBuilder apiResponseBuilder;

    public PagosController(PagosService pagosService, ApiResponseBuilder apiResponseBuilder) {
        this.pagosService = pagosService;
        this.apiResponseBuilder = apiResponseBuilder;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> crearPago(@Valid @RequestBody PagoDTO pagoDTO) {
        Pago pagoCreado = pagosService.generarPago(pagoDTO);
        Map<String, Object> respuesta = apiResponseBuilder.pago("Pago registrado exitosamente", pagoCreado);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }
}
