package com.scout.scoutmanagement.controller;

import com.scout.scoutmanagement.DTO.PagoDTO;
import com.scout.scoutmanagement.domain.Pagos.Costos;
import com.scout.scoutmanagement.domain.Pagos.Pago;
import com.scout.scoutmanagement.service.PagosService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pagos")
public class PagosController {

    private final PagosService pagosService;

    public PagosController(PagosService pagosService) {
        this.pagosService = pagosService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> crearPago(@Valid @RequestBody PagoDTO pagoDTO) {
        Pago pagoCreado = pagosService.generarPago(pagoDTO);
        Map<String, Object> respuesta = construirRespuestaPago("Pago registrado exitosamente", pagoCreado);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    private Map<String, Object> construirRespuestaPago(String mensaje, Pago pagoCreado) {
        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("mensaje", mensaje);
        respuesta.put("id", pagoCreado.getId());
        respuesta.put("fecha", pagoCreado.getFecha());
        respuesta.put("personaId", pagoCreado.getPersona().getId());
        respuesta.put("personaQueRegistraId", pagoCreado.getPersonaQueRegistra().getId());
        respuesta.put("montoTotal", pagoCreado.getMontoTotal());
        respuesta.put("costos", construirCostosEnRespuesta(pagoCreado.getCostos()));
        return respuesta;
    }

    private List<Map<String, Object>> construirCostosEnRespuesta(List<Costos> costos) {
        return costos.stream().map(costo -> {
            Map<String, Object> costoMap = new LinkedHashMap<>();
            costoMap.put("id", costo.getId());
            costoMap.put("tipo", costo.getClass().getSimpleName());
            costoMap.put("importe", costo.getImporte());
            return costoMap;
        }).toList();
    }
}
