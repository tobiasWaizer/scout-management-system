package com.scout.scoutmanagement.service.pagos;

import com.scout.scoutmanagement.domain.Pagos.Costos;
import com.scout.scoutmanagement.domain.Pagos.Motivo;
import com.scout.scoutmanagement.domain.Persona;
import com.scout.scoutmanagement.backend.repository.CostosRepository;
import com.scout.scoutmanagement.backend.service.CostosFijosAutomaticosService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CostosYPagosServiceTest {

    @Mock
    private CostosRepository costosRepository;

    @InjectMocks
    private CostosFijosAutomaticosService costosFijosAutomaticosService;

    @Test
    void generarParaPersona_noDeberiaPersistirSiLosCostosFijosYaExisten() {
        Persona persona = new Persona();
        persona.setId(20L);
        persona.setActivo(true);

        when(costosRepository.existsCostoFijoByPersonaAndAnioAndTipo(20L, 2026, Motivo.AFILIACION)).thenReturn(true);
        when(costosRepository.existsCostoFijoByPersonaAndAnioAndTipo(20L, 2026, Motivo.CUOTA_MENSUAL)).thenReturn(true);

        costosFijosAutomaticosService.generarParaPersona(persona, 2026, 3);

        verify(costosRepository, never()).save(org.mockito.ArgumentMatchers.any(Costos.class));
    }
}
