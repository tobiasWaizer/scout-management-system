package com.scout.scoutmanagement.service;

import com.scout.scoutmanagement.domain.Pagos.Afiliacion;
import com.scout.scoutmanagement.domain.Pagos.CuotaMensual;
import com.scout.scoutmanagement.domain.Persona;
import com.scout.scoutmanagement.repository.AfiliacionRepository;
import com.scout.scoutmanagement.repository.CuotaMensualRepository;
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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CostosFijosAutomaticosServiceTest {

    @Mock
    private PersonaRepository personaRepository;

    @Mock
    private AfiliacionRepository afiliacionRepository;

    @Mock
    private CuotaMensualRepository cuotaMensualRepository;

    @InjectMocks
    private CostosFijosAutomaticosService costosFijosAutomaticosService;

    @Test
    void generarDesdeJulioHastaDiciembre_deberiaCrearAfiliacionYCuotasParaPersonaNueva() {
        Persona persona = new Persona();
        persona.setId(10L);
        persona.setActivo(true);

        ReflectionTestUtils.setField(costosFijosAutomaticosService, "importeAfiliacion", new BigDecimal("5000"));
        ReflectionTestUtils.setField(costosFijosAutomaticosService, "importeCuotaMensual", new BigDecimal("10000"));

        when(afiliacionRepository.existsByPersonaQueTieneQuePagar_IdAndAnio(10L, 2026)).thenReturn(false);
        when(cuotaMensualRepository.existsByPersonaQueTieneQuePagar_IdAndAnioAndMes(anyLong(), anyInt(), anyInt()))
            .thenReturn(false);

        costosFijosAutomaticosService.generarDesdeMesHastaFinDeAnioParaPersona(persona, 2026, 7);

        ArgumentCaptor<Afiliacion> afiliacionCaptor = ArgumentCaptor.forClass(Afiliacion.class);
        verify(afiliacionRepository, times(1)).save(afiliacionCaptor.capture());
        Afiliacion afiliacionCreada = afiliacionCaptor.getValue();
        assertEquals(10L, afiliacionCreada.getPersonaQueTieneQuePagar().getId());
        assertEquals(2026, afiliacionCreada.getAnio());
        assertEquals(new BigDecimal("5000"), afiliacionCreada.getImporte());

        ArgumentCaptor<CuotaMensual> cuotaCaptor = ArgumentCaptor.forClass(CuotaMensual.class);
        verify(cuotaMensualRepository, times(6)).save(cuotaCaptor.capture());
        List<CuotaMensual> cuotasGuardadas = cuotaCaptor.getAllValues();

        assertEquals(6, cuotasGuardadas.size());
        for (int i = 0; i < cuotasGuardadas.size(); i++) {
            CuotaMensual cuota = cuotasGuardadas.get(i);
            assertEquals(10L, cuota.getPersonaQueTieneQuePagar().getId());
            assertEquals(2026, cuota.getAnio());
            assertEquals(Integer.valueOf(7 + i), cuota.getMes());
            assertEquals(new BigDecimal("10000"), cuota.getImporte());
        }

        assertTrue(cuotasGuardadas.stream().allMatch(c -> c.getMes() >= 7 && c.getMes() <= 12));
    }

    @Test
    void generarDesdeMesHastaFinDeAnioParaPersona_noDeberiaGenerarCostosSiPersonaInactiva() {
        Persona persona = new Persona();
        persona.setId(11L);
        persona.setActivo(false);

        costosFijosAutomaticosService.generarDesdeMesHastaFinDeAnioParaPersona(persona, 2026, 7);

        verify(afiliacionRepository, never()).save(org.mockito.ArgumentMatchers.any(Afiliacion.class));
        verify(cuotaMensualRepository, never()).save(org.mockito.ArgumentMatchers.any(CuotaMensual.class));
    }
}
