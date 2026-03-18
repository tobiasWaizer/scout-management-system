package com.scout.scoutmanagement.controller;

import com.scout.scoutmanagement.DTO.PagoDTO;
import com.scout.scoutmanagement.domain.Pagos.Cuota;
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
        respuesta.put("cuotasPagadas", construirCuotasEnRespuesta(pagoCreado.getCuotas()));
        return respuesta;
    }

    private List<Map<String, Object>> construirCuotasEnRespuesta(List<Cuota> cuotas) {
        return cuotas.stream().map(cuota -> {
            Map<String, Object> cuotaMap = new LinkedHashMap<>();
            cuotaMap.put("id", cuota.getId());
            cuotaMap.put("ordenCuota", cuota.getOrdenCuota());
            cuotaMap.put("monto", cuota.getMonto());
            cuotaMap.put("costoId", cuota.getCosto().getId());
            cuotaMap.put("motivo", cuota.getCosto().getMotivo());
            return cuotaMap;
        }).toList();
    }
}
