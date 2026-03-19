package com.scout.scoutmanagement.backend.service;

import com.scout.scoutmanagement.domain.Rama;
import com.scout.scoutmanagement.backend.exception.ObjectNotFoundException;
import com.scout.scoutmanagement.backend.repository.RamaRepository;
import com.scout.scoutmanagement.backend.service.RamaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RamaServiceTest {

    @Mock
    private RamaRepository ramaRepository;

    @InjectMocks
    private RamaService ramaService;

    @Test
    void obtenerRamaPorId_deberiaRetornarRamaCuandoExiste() {
        Rama rama = new Rama("Unidad");
        rama.setId(2L);

        when(ramaRepository.findById(2L)).thenReturn(Optional.of(rama));

        Rama resultado = ramaService.obtenerRamaPorId(2L);

        assertEquals(2L, resultado.getId());
        assertEquals("Unidad", resultado.getNombre());
    }

    @Test
    void obtenerRamaPorId_deberiaLanzarObjectNotFoundCuandoNoExiste() {
        when(ramaRepository.findById(99L)).thenReturn(Optional.empty());

        ObjectNotFoundException ex = assertThrows(ObjectNotFoundException.class, () -> ramaService.obtenerRamaPorId(99L));

        assertEquals("No existe una rama con ID: 99", ex.getMessage());
    }

    @Test
    void obtenerRamaPorId_deberiaLanzarErrorSiIdEsNulo() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> ramaService.obtenerRamaPorId(null));

        assertEquals("El ID de rama es obligatorio", ex.getMessage());
    }
}

