package com.scout.scoutmanagement.service;

import com.scout.scoutmanagement.DTO.PersonaDTO;
import com.scout.scoutmanagement.domain.Persona;
import com.scout.scoutmanagement.domain.Rama;
import com.scout.scoutmanagement.domain.Rol;
import com.scout.scoutmanagement.exception.ObjectNotFoundException;
import com.scout.scoutmanagement.repository.PersonaRepository;
import com.scout.scoutmanagement.repository.RamaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PersonaServiceTest {

    @Mock
    private PersonaRepository personaRepository;

    @Mock
    private RamaRepository ramaRepository;

    @Mock
    private CostosFijosAutomaticosService costosFijosAutomaticosService;

    @InjectMocks
    private PersonaService personaService;

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
        when(ramaRepository.findById(dto.getRamaId())).thenReturn(Optional.of(rama));
        when(personaRepository.save(any(Persona.class))).thenReturn(guardada);

        Persona resultado = personaService.crearPersona(dto);

        assertEquals(10L, resultado.getId());
        assertEquals(Rol.BENEFICIARIO, resultado.getRol());
        assertEquals(1L, resultado.getRama().getId());
    }

    @Test
    void realizarPartida_deberiaCambiarRolAEducador() {
        Persona persona = new Persona();
        persona.setId(7L);
        persona.setRol(Rol.BENEFICIARIO);

        Rama ramaDestino = new Rama("Unidad");
        ramaDestino.setId(2L);

        when(personaRepository.findByIdAndRol(7L, Rol.BENEFICIARIO)).thenReturn(Optional.of(persona));
        when(ramaRepository.findById(2L)).thenReturn(Optional.of(ramaDestino));
        when(personaRepository.save(any(Persona.class))).thenAnswer(inv -> inv.getArgument(0));

        Persona resultado = personaService.realizarPartida(7L, 2L);

        assertEquals(Rol.EDUCADOR, resultado.getRol());
        assertEquals(2L, resultado.getRama().getId());
    }

    @Test
    void realizarPartida_deberiaLanzarNotFoundSiNoEsBeneficiario() {
        when(personaRepository.findByIdAndRol(7L, Rol.BENEFICIARIO)).thenReturn(Optional.empty());

        ObjectNotFoundException ex = assertThrows(ObjectNotFoundException.class, () -> personaService.realizarPartida(7L, 2L));

        assertEquals("No existe un beneficiario con ID: 7", ex.getMessage());
    }
}
