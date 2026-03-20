package com.scout.scoutmanagement.backend.service;

import com.scout.scoutmanagement.backend.dto.PersonaDTO;
import com.scout.scoutmanagement.backend.exception.ObjectNotFoundException;
import com.scout.scoutmanagement.backend.repository.EstadoCursoRepository;
import com.scout.scoutmanagement.backend.repository.PersonaRepository;
import com.scout.scoutmanagement.domain.Pagos.CostosFijos;
import com.scout.scoutmanagement.domain.Pagos.Cuota;
import com.scout.scoutmanagement.domain.Pagos.Motivo;
import com.scout.scoutmanagement.domain.EstadoCurso;
import com.scout.scoutmanagement.domain.Persona;
import com.scout.scoutmanagement.domain.Rama;
import com.scout.scoutmanagement.domain.Rol;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PersonasServiceTest {

    @Mock
    private PersonaRepository personaRepository;

    @Mock
    private EstadoCursoRepository estadoCursoRepository;

    @Mock
    private RamasService ramasService;

    @Mock
    private CuotasService cuotasService;

    @Mock
    private CostosFijosAutomaticosService costosFijosAutomaticosService;

    @InjectMocks
    private PersonasService personasService;

    @Test
    void crearPersona_deberiaGuardarPersonaCuandoDatosSonValidos() {
        PersonaDTO dto = new PersonaDTO();
        dto.setNombre("Ana");
        dto.setApellido("Lopez");
        dto.setDni(32165498L);
        dto.setMail("ana.lopez@test.com");
        dto.setRamaId(1L);
        dto.setRol(Rol.BENEFICIARIO);

        Rama rama = new Rama("Manada");
        rama.setId(1L);

        Persona guardada = new Persona("Ana", "Lopez", 32165498L, "ana.lopez@test.com", rama, Rol.BENEFICIARIO);
        guardada.setId(10L);

        when(personaRepository.findByDni(dto.getDni())).thenReturn(Optional.empty());
        when(personaRepository.findByMail(dto.getMail())).thenReturn(Optional.empty());
        when(ramasService.obtenerRamaPorId(dto.getRamaId())).thenReturn(rama);
        when(personaRepository.save(any(Persona.class))).thenReturn(guardada);
        when(estadoCursoRepository.save(any(EstadoCurso.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Persona resultado = personasService.crearPersona(dto);

        assertEquals(10L, resultado.getId());
        assertEquals(Rol.BENEFICIARIO, resultado.getRol());
        assertEquals(1L, resultado.getRama().getId());
        verify(estadoCursoRepository).save(any(EstadoCurso.class));
    }

    @Test
    void realizarPartida_deberiaCambiarRolAEducador() {
        Persona persona = new Persona();
        persona.setId(7L);
        persona.setRol(Rol.BENEFICIARIO);

        Rama ramaDestino = new Rama("Unidad");
        ramaDestino.setId(2L);

        when(personaRepository.findByIdAndRol(7L, Rol.BENEFICIARIO)).thenReturn(Optional.of(persona));
        when(ramasService.obtenerRamaPorId(2L)).thenReturn(ramaDestino);
        when(estadoCursoRepository.findByPersonaIdAndActualTrue(7L)).thenReturn(Optional.empty());
        when(estadoCursoRepository.findByPersonaIdAndRamaId(7L, 2L)).thenReturn(Optional.empty());
        when(estadoCursoRepository.save(any(EstadoCurso.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(personaRepository.save(any(Persona.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Persona resultado = personasService.realizarPartida(7L, 2L);

        assertEquals(Rol.EDUCADOR, resultado.getRol());
        assertEquals(2L, resultado.getRama().getId());
    }

    @Test
    void realizarPartida_deberiaLanzarNotFoundSiNoEsBeneficiario() {
        when(personaRepository.findByIdAndRol(7L, Rol.BENEFICIARIO)).thenReturn(Optional.empty());

        ObjectNotFoundException ex = assertThrows(ObjectNotFoundException.class, () -> personasService.realizarPartida(7L, 2L));

        assertEquals("No existe un beneficiario con ID: 7", ex.getMessage());
    }

    @Test
    void obtenerCuotasDePersona_deberiaTraerSoloPendientesCuandoPendienteEsTrue() {
        Persona persona = new Persona();
        persona.setId(2L);
        persona.setActivo(true);

        Cuota cuotaPendiente = new Cuota();
        cuotaPendiente.setOrdenCuota(1);
        cuotaPendiente.setCosto(new CostosFijos());
        cuotaPendiente.getCosto().setMotivo(Motivo.CUOTA_MENSUAL);

        when(personaRepository.findById(2L)).thenReturn(Optional.of(persona));
        when(cuotasService.obtenerCuotasDePersonaOrdenadasPorCosto(2L, true, 2026))
            .thenReturn(java.util.List.of(cuotaPendiente));

        List<Cuota> resultado = personasService.obtenerCuotasDePersona(2L, true, false, 2026);

        assertEquals(1, resultado.size());
    }

    @Test
    void obtenerCuotasDePersona_deberiaFallarSiActivoEsTrueYLaPersonaEstaInactiva() {
        Persona persona = new Persona();
        persona.setId(2L);
        persona.setActivo(false);

        when(personaRepository.findById(2L)).thenReturn(Optional.of(persona));

        List<Cuota> resultado = personasService.obtenerCuotasDePersona(2L, false, true);

        assertEquals(0, resultado.size());
    }

    @Test
    void obtenerCuotasDePersona_deberiaFiltrarPorAnioYTraerDesdeeseAnio() {
        Persona persona = new Persona();
        persona.setId(2L);
        persona.setActivo(true);

        CostosFijos costo2025 = new CostosFijos();
        costo2025.setMotivo(Motivo.CUOTA_MENSUAL);
        costo2025.setAnio(2025);

        Cuota cuota2025 = new Cuota();
        cuota2025.setOrdenCuota(1);
        cuota2025.setCosto(costo2025);

        when(personaRepository.findById(2L)).thenReturn(Optional.of(persona));
        when(cuotasService.obtenerCuotasDePersonaOrdenadasPorCosto(2L, false, 2025))
            .thenReturn(java.util.List.of(cuota2025));

        List<Cuota> resultado = personasService.obtenerCuotasDePersona(2L, false, false, 2025);

        assertEquals(1, resultado.size());
        assertEquals(2025, ((CostosFijos) resultado.get(0).getCosto()).getAnio());
    }
}
