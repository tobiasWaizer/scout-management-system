package com.scout.scoutmanagement.backend.service.pagos;

import com.scout.scoutmanagement.backend.dto.PagoDTO;
import com.scout.scoutmanagement.domain.Pagos.Costos;
import com.scout.scoutmanagement.domain.Pagos.CostosFijos;
import com.scout.scoutmanagement.domain.Pagos.Cuota;
import com.scout.scoutmanagement.domain.Pagos.Motivo;
import com.scout.scoutmanagement.domain.Pagos.Pago;
import com.scout.scoutmanagement.domain.Persona;
import com.scout.scoutmanagement.backend.repository.PagoRepository;
import com.scout.scoutmanagement.backend.service.CostosService;
import com.scout.scoutmanagement.backend.service.PagosService;
import com.scout.scoutmanagement.backend.service.PersonasService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PagosServiceTest {

    @Mock
    private PagoRepository pagoRepository;

    @Mock
    private CostosService costosService;

    @Mock
    private PersonasService personasService;

    @InjectMocks
    private PagosService pagosService;

    @Test
    void generarPago_deberiaGuardarPagoConVariasCuotas() {
        PagoDTO dto = new PagoDTO();
        dto.setPersonaId(1L);
        dto.setPersonaQueRegistraId(2L);
        dto.setCuotaIds(List.of(1000L, 1001L));
        dto.setFecha(LocalDate.of(2026, 3, 17));

        Persona personaQuePaga = new Persona();
        personaQuePaga.setId(1L);
        personaQuePaga.setActivo(true);

        Persona personaQueRegistra = new Persona();
        personaQueRegistra.setId(2L);
        personaQueRegistra.setActivo(true);

        Costos costo1 = crearCosto(100L, personaQuePaga, "1000");
        Costos costo2 = crearCosto(101L, personaQuePaga, "2500");
        Cuota cuota1 = costo1.getCuotas().get(0);
        cuota1.setId(1000L);
        Cuota cuota2 = costo2.getCuotas().get(0);
        cuota2.setId(1001L);

        when(personasService.obtenerPersona(1L)).thenReturn(personaQuePaga);
        when(personasService.obtenerPersona(2L)).thenReturn(personaQueRegistra);
        when(costosService.obtenerCuotasPorIds(dto.getCuotaIds())).thenReturn(List.of(cuota1, cuota2));
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
        verify(costosService).marcarCuotasComoPagadas(List.of(cuota1, cuota2), resultado);
    }

    @Test
    void generarPago_deberiaFallarSiAlgunaCuotaYaFuePagada() {
        PagoDTO dto = new PagoDTO();
        dto.setPersonaId(1L);
        dto.setPersonaQueRegistraId(2L);
        dto.setCuotaIds(List.of(1000L));

        Persona personaQuePaga = new Persona();
        personaQuePaga.setId(1L);
        personaQuePaga.setActivo(true);

        Persona personaQueRegistra = new Persona();
        personaQueRegistra.setId(2L);
        personaQueRegistra.setActivo(true);

        Costos costoYaPagado = crearCosto(100L, personaQuePaga, "1000");
        Cuota cuotaYaPagada = costoYaPagado.getCuotas().get(0);
        cuotaYaPagada.setId(1000L);
        cuotaYaPagada.setPago(new Pago());

        when(personasService.obtenerPersona(1L)).thenReturn(personaQuePaga);
        when(personasService.obtenerPersona(2L)).thenReturn(personaQueRegistra);
        when(costosService.obtenerCuotasPorIds(dto.getCuotaIds())).thenReturn(List.of(cuotaYaPagada));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> pagosService.generarPago(dto));

        assertEquals("La cuota con ID 1000 ya fue pagada", ex.getMessage());
    }

    @Test
    void generarPago_deberiaFallarSiHayCuotasRepetidasEnElMismoPago() {
        PagoDTO dto = new PagoDTO();
        dto.setPersonaId(1L);
        dto.setPersonaQueRegistraId(2L);
        dto.setCuotaIds(List.of(1000L, 1000L));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> pagosService.generarPago(dto));

        assertEquals("No se permiten IDs de cuotas repetidos en el mismo pago", ex.getMessage());
    }

    @Test
    void obtenerPagosDePersonaId_deberiaValidarPersonaYRetornarPagosOrdenados() {
        Long idPersona = 1L;
        Integer anio = 2026;
        Persona persona = new Persona();
        persona.setId(idPersona);

        Pago pago = new Pago();
        when(personasService.obtenerPersona(idPersona)).thenReturn(persona);
        when(pagoRepository.obtenerPagosDePersonaDesdeAnioOrdenadasPorFecha(idPersona, anio)).thenReturn(List.of(pago));

        List<Pago> resultado = pagosService.obtenerPagosDePersonaId(idPersona, anio);

        assertEquals(1, resultado.size());
        verify(personasService).obtenerPersona(eq(idPersona));
        verify(pagoRepository).obtenerPagosDePersonaDesdeAnioOrdenadasPorFecha(eq(idPersona), eq(anio));
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




