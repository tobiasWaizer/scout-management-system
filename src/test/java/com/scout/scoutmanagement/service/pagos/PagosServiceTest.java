package com.scout.scoutmanagement.service.pagos;

import com.scout.scoutmanagement.DTO.PagoDTO;
import com.scout.scoutmanagement.domain.Pagos.Costos;
import com.scout.scoutmanagement.domain.Pagos.CostosFijos;
import com.scout.scoutmanagement.domain.Pagos.Cuota;
import com.scout.scoutmanagement.domain.Pagos.Motivo;
import com.scout.scoutmanagement.domain.Pagos.Pago;
import com.scout.scoutmanagement.domain.Persona;
import com.scout.scoutmanagement.repository.PagoRepository;
import com.scout.scoutmanagement.service.CostosService;
import com.scout.scoutmanagement.service.PagosService;
import com.scout.scoutmanagement.service.PersonaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PagosServiceTest {

    @Mock
    private PagoRepository pagoRepository;

    @Mock
    private CostosService costosService;

    @Mock
    private PersonaService personaService;

    @InjectMocks
    private PagosService pagosService;

    @Test
    void generarPago_deberiaGuardarPagoConVariosCostos() {
        PagoDTO dto = new PagoDTO();
        dto.setPersonaId(1L);
        dto.setPersonaQueRegistraId(2L);
        dto.setCostoIds(List.of(100L, 101L));
        dto.setFecha(LocalDate.of(2026, 3, 17));

        Persona personaQuePaga = new Persona();
        personaQuePaga.setId(1L);
        personaQuePaga.setActivo(true);

        Persona personaQueRegistra = new Persona();
        personaQueRegistra.setId(2L);
        personaQueRegistra.setActivo(true);

        Costos costo1 = crearCosto(100L, personaQuePaga, "1000");
        Costos costo2 = crearCosto(101L, personaQuePaga, "2500");

        when(personaService.obtenerPersona(1L)).thenReturn(personaQuePaga);
        when(personaService.obtenerPersona(2L)).thenReturn(personaQueRegistra);
        when(costosService.obtenerCostosPorIds(dto.getCostoIds())).thenReturn(List.of(costo1, costo2));
        when(pagoRepository.save(any(Pago.class))).thenAnswer(invocation -> {
            Pago pago = invocation.getArgument(0);
            java.lang.reflect.Field idField = Pago.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(pago, 77L);
            return pago;
        });

        Pago resultado = pagosService.generarPago(dto);

        assertEquals(77L, resultado.getId());
        assertEquals(1L, resultado.getPersona().getId());
        assertEquals(2L, resultado.getPersonaQueRegistra().getId());
        assertEquals(new BigDecimal("3500"), resultado.getMontoTotal());
        assertEquals(2, resultado.getCostos().size());
        assertEquals(77L, costo1.getCuotas().get(0).getPago().getId());
        assertEquals(77L, costo2.getCuotas().get(0).getPago().getId());
    }

    @Test
    void generarPago_deberiaFallarSiAlgunoDeLosCostosYaFuePagado() {
        PagoDTO dto = new PagoDTO();
        dto.setPersonaId(1L);
        dto.setPersonaQueRegistraId(2L);
        dto.setCostoIds(List.of(100L));

        Persona personaQuePaga = new Persona();
        personaQuePaga.setId(1L);
        personaQuePaga.setActivo(true);

        Persona personaQueRegistra = new Persona();
        personaQueRegistra.setId(2L);
        personaQueRegistra.setActivo(true);

        Costos costoYaPagado = crearCosto(100L, personaQuePaga, "1000");
        costoYaPagado.getCuotas().get(0).setPago(new Pago());

        when(personaService.obtenerPersona(1L)).thenReturn(personaQuePaga);
        when(personaService.obtenerPersona(2L)).thenReturn(personaQueRegistra);
        when(costosService.obtenerCostosPorIds(dto.getCostoIds())).thenReturn(List.of(costoYaPagado));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> pagosService.generarPago(dto));

        assertEquals("El costo con ID 100 ya fue pagado", ex.getMessage());
    }

    private Costos crearCosto(Long id, Persona personaObjetivo, String importe) {
        CostosFijos costo = new CostosFijos();
        costo.setPersonaQueTieneQuePagar(personaObjetivo);
        costo.setMotivo(Motivo.AFILIACION);

        Cuota cuota = new Cuota();
        cuota.setOrdenCuota(1);
        cuota.setMonto(new BigDecimal(importe));
        costo.agregarCuota(cuota);

        try {
            java.lang.reflect.Field idField = Costos.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(costo, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return costo;
    }
}




