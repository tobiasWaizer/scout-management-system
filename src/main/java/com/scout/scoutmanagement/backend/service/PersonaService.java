package com.scout.scoutmanagement.backend.service;

import com.scout.scoutmanagement.backend.dto.PersonaDTO;
import com.scout.scoutmanagement.domain.Pagos.Cuota;
import com.scout.scoutmanagement.domain.Persona;
import com.scout.scoutmanagement.domain.Rama;
import com.scout.scoutmanagement.domain.Rol;
import com.scout.scoutmanagement.backend.exception.ObjectNotFoundException;
import com.scout.scoutmanagement.backend.repository.PersonaRepository;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.time.YearMonth;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class PersonaService {

    private final PersonaRepository personaRepository;
    private final RamaService ramaService;
    private final CuotaService cuotaService;
    private final CostosFijosAutomaticosService costosFijosAutomaticosService;

    public PersonaService(
        PersonaRepository personaRepository,
        RamaService ramaService,
        CuotaService cuotaService,
        CostosFijosAutomaticosService costosFijosAutomaticosService) {
        this.personaRepository = personaRepository;
        this.ramaService = ramaService;
        this.cuotaService = cuotaService;
        this.costosFijosAutomaticosService = costosFijosAutomaticosService;
    }

    public Persona crearPersona(PersonaDTO personaDTO) {
        validarDniYMailUnicos(personaDTO.getDni(), personaDTO.getMail());
        Rama rama = ramaService.obtenerRamaPorId(personaDTO.getRamaId());

        Persona persona = new Persona(
            personaDTO.getNombre(),
            personaDTO.getApellido(),
            personaDTO.getDni(),
            personaDTO.getMail(),
            rama,
            personaDTO.getRol()
        );

        //potencial observer, si se quiere desacoplar la logica de costos fijos automaticos del servicio de personas, se podria implementar un evento de "PersonaCreada" y que el servicio de costos fijos automaticos escuche ese evento para generar los costos fijos correspondientes
        Persona personaGuardada = personaRepository.save(persona);
        YearMonth periodoActual = YearMonth.now();
        costosFijosAutomaticosService.generarDesdeMesHastaFinDeAnioParaPersona(
            personaGuardada,
            periodoActual.getYear(),
            periodoActual.getMonthValue()
        );

        return personaGuardada;
    }

    public Persona modificarPersona(PersonaDTO personaDTO, Long idPersona) {
        Persona persona = obtenerPersona(idPersona);
        Rama rama = ramaService.obtenerRamaPorId(personaDTO.getRamaId());
        validarDniYMailUnicosEnActualizacion(personaDTO.getDni(), personaDTO.getMail(), idPersona);

        persona.setNombre(personaDTO.getNombre());
        persona.setApellido(personaDTO.getApellido());
        persona.setDni(personaDTO.getDni());
        persona.setMail(personaDTO.getMail());
        persona.setRama(rama);
        persona.setRol(personaDTO.getRol());

        return personaRepository.save(persona);
    }

    public Persona obtenerPersona(Long idPersona) {
        return personaRepository.findById(idPersona)
            .orElseThrow(() -> new ObjectNotFoundException("No existe una persona con ID: " + idPersona));
    }

    public Persona inhabilitarPersona(Long idPersona) {
        Persona persona = obtenerPersona(idPersona);
        persona.setActivo(false);
        return personaRepository.save(persona);
    }

    public List<Persona> obtenerPersonasActivas() {
        return personaRepository.findByActivoTrue();
    }

    public Persona obtenerEducador(Long idEducador) {
        if (idEducador == null) {
            throw new IllegalArgumentException("El ID del educador es obligatorio");
        }
        return personaRepository.findByIdAndRol(idEducador, Rol.EDUCADOR)
            .orElseThrow(() -> new ObjectNotFoundException("No existe un educador con ID: " + idEducador));
    }

    public List<Persona> obtenerPersonasPorIds(List<Long> ids, String campo) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }

        Set<Long> idsUnicos = new LinkedHashSet<>(ids);
        List<Persona> personas = personaRepository.findAllById(idsUnicos);

        if (personas.size() != idsUnicos.size()) {
            throw new ObjectNotFoundException("Al menos una persona enviada en " + campo + " no existe");
        }

        return personas;
    }

    public Persona realizarPartida(Long idBeneficiario, Long rama_id) {
        Persona persona = personaRepository.findByIdAndRol(idBeneficiario, Rol.BENEFICIARIO)
                .orElseThrow(() -> new ObjectNotFoundException("No existe un beneficiario con ID: " + idBeneficiario));

        persona.hacerPartida();
        Rama ramaDestino = ramaService.obtenerRamaPorId(rama_id);
        persona.setRama(ramaDestino);
        return personaRepository.save(persona);
    }

    public Rama establecerJefeDeRama(Long idEducador) {
        Persona jefe = personaRepository.findByIdAndRol(idEducador, Rol.EDUCADOR)
            .orElseThrow(() -> new ObjectNotFoundException("No existe un educador con ID: " + idEducador));

        if (jefe.getRama() == null) {
            throw new IllegalArgumentException("El educador con ID " + idEducador + " no tiene rama asignada");
        }
        Rama ramaDelEducador = ramaService.obtenerRamaPorId(jefe.getRama().getId());

        ramaService.obtenerRamaDondeEsJefe(idEducador)
            .filter(rama -> !rama.getId().equals(ramaDelEducador.getId()))
            .ifPresent(rama -> {
                throw new IllegalArgumentException("La persona ya es jefe de la rama " + rama.getNombre());
            });

        ramaDelEducador.setJefeDeRama(jefe);
        return ramaService.guardar(ramaDelEducador);
    }

    public List<Cuota> obtenerCuotasDePersona(Long idPersona, boolean pendiente, boolean activo) {
        return obtenerCuotasDePersona(idPersona, pendiente, activo, Year.now().getValue());
    }

    public List<Cuota> obtenerCuotasDePersona(Long idPersona, boolean pendiente, boolean activo, Integer anio) {
        Persona persona = obtenerPersona(idPersona);

        if (activo && !Boolean.TRUE.equals(persona.getActivo())) {
            return List.of();
        }

        return cuotaService.obtenerCuotasDePersonaOrdenadasPorCosto(idPersona, pendiente, anio);
    }


    private void validarDniYMailUnicos(Long dni, String mail) {
        if (personaRepository.findByDni(dni).isPresent()) {
            throw new IllegalArgumentException("Ya existe una persona con el DNI: " + dni);
        }

        if (personaRepository.findByMail(mail).isPresent()) {
            throw new IllegalArgumentException("Ya existe una persona con el email: " + mail);
        }
    }

    private void validarDniYMailUnicosEnActualizacion(Long dni, String mail, Long idActual) {
        personaRepository.findByDni(dni)
            .filter(p -> !p.getId().equals(idActual))
            .ifPresent(p -> {
                throw new IllegalArgumentException("Ya existe una persona con el DNI: " + dni);
            });

        personaRepository.findByMail(mail)
            .filter(p -> !p.getId().equals(idActual))
            .ifPresent(p -> {
                throw new IllegalArgumentException("Ya existe una persona con el email: " + mail);
            });
    }

}


