package com.scout.scoutmanagement.service;

import com.scout.scoutmanagement.domain.Pagos.Afiliacion;
import com.scout.scoutmanagement.domain.Pagos.Costos;
import com.scout.scoutmanagement.domain.Pagos.CostosFijos;
import com.scout.scoutmanagement.domain.Pagos.Motivo;
import com.scout.scoutmanagement.domain.Persona;
import com.scout.scoutmanagement.repository.CostosRepository;
import com.scout.scoutmanagement.repository.PersonaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CostosFijosAutomaticosServiceTest {

    @Mock
    private PersonaRepository personaRepository;

    @Mock
    private CostosRepository costosRepository;

    @InjectMocks
    private CostosFijosAutomaticosService costosFijosAutomaticosService;

    @Test
    void generarDesdeJulioHastaDiciembre_deberiaCrearAfiliacionYCuotaMensualParaPersonaNueva() {
        Persona persona = new Persona();
        persona.setId(10L);
        persona.setActivo(true);

        ReflectionTestUtils.setField(costosFijosAutomaticosService, "importeAfiliacion", new BigDecimal("5000"));
        ReflectionTestUtils.setField(costosFijosAutomaticosService, "importeCuotaMensual", new BigDecimal("10000"));

        when(costosRepository.existsCostoFijoByPersonaAndAnioAndTipo(10L, 2026, Motivo.AFILIACION)).thenReturn(false);
        when(costosRepository.existsCostoFijoByPersonaAndAnioAndTipo(10L, 2026, Motivo.CUOTA_MENSUAL)).thenReturn(false);

        costosFijosAutomaticosService.generarDesdeMesHastaFinDeAnioParaPersona(persona, 2026, 7);

        ArgumentCaptor<Costos> costosCaptor = ArgumentCaptor.forClass(Costos.class);
        verify(costosRepository, times(2)).save(costosCaptor.capture());
        List<Costos> costosGuardados = costosCaptor.getAllValues();

        Afiliacion afiliacion = (Afiliacion) costosGuardados.stream()
            .filter(c -> c.getMotivo() == Motivo.AFILIACION)
            .findFirst()
            .orElseThrow();

        CostosFijos cuotaMensual = (CostosFijos) costosGuardados.stream()
            .filter(c -> c.getMotivo() == Motivo.CUOTA_MENSUAL)
            .findFirst()
            .orElseThrow();

        assertEquals(10L, afiliacion.getPersonaQueTieneQuePagar().getId());
        assertEquals(2026, afiliacion.getAnio());
        assertEquals(1, afiliacion.getCuotas().size());
        assertEquals(new BigDecimal("5000"), afiliacion.getCuotas().get(0).getMonto());

        assertEquals(10L, cuotaMensual.getPersonaQueTieneQuePagar().getId());
        assertEquals(2026, cuotaMensual.getAnio());
        assertEquals(6, cuotaMensual.getCantidadMeses());
        assertEquals(6, cuotaMensual.getCuotas().size());
        assertTrue(cuotaMensual.getCuotas().stream().allMatch(c -> c.getMonto().compareTo(new BigDecimal("10000")) == 0));
    }

    @Test
    void generarDesdeMesHastaFinDeAnioParaPersona_noDeberiaGenerarCostosSiPersonaInactiva() {
        Persona persona = new Persona();
        persona.setId(11L);
        persona.setActivo(false);

        costosFijosAutomaticosService.generarDesdeMesHastaFinDeAnioParaPersona(persona, 2026, 7);

        verify(costosRepository, never()).save(org.mockito.ArgumentMatchers.any(Costos.class));
    }
}
