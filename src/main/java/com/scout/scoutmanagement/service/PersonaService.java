package com.scout.scoutmanagement.service;

import com.scout.scoutmanagement.DTO.PersonaDTO;
import com.scout.scoutmanagement.domain.Persona;
import com.scout.scoutmanagement.domain.Rama;
import com.scout.scoutmanagement.domain.Rol;
import com.scout.scoutmanagement.exception.ObjectNotFoundException;
import com.scout.scoutmanagement.repository.PersonaRepository;
import com.scout.scoutmanagement.repository.RamaRepository;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.List;

@Service
public class PersonaService {

    private final PersonaRepository personaRepository;
    private final RamaRepository ramaRepository;
    private final CostosFijosAutomaticosService costosFijosAutomaticosService;

    public PersonaService(
        PersonaRepository personaRepository,
        RamaRepository ramaRepository,
        CostosFijosAutomaticosService costosFijosAutomaticosService
    ) {
        this.personaRepository = personaRepository;
        this.ramaRepository = ramaRepository;
        this.costosFijosAutomaticosService = costosFijosAutomaticosService;
    }

    public Persona crearPersona(PersonaDTO personaDTO) {
        validarDniYMailUnicos(personaDTO.getDni(), personaDTO.getMail());
        Rama rama = obtenerRamaExistente(personaDTO.getRamaId());

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
        Rama rama = obtenerRamaExistente(personaDTO.getRamaId());
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

    public Persona realizarPartida(Long idBeneficiario, Long rama_id) {
        Persona persona = personaRepository.findByIdAndRol(idBeneficiario, Rol.BENEFICIARIO)
                .orElseThrow(() -> new ObjectNotFoundException("No existe un beneficiario con ID: " + idBeneficiario));

        persona.hacerPartida();
        Rama ramaDestino = ramaRepository.findById(rama_id)
                .orElseThrow(() -> new ObjectNotFoundException("No existe una rama con ID: " + rama_id));
        persona.setRama(ramaDestino);
        return personaRepository.save(persona);
    }

    public Rama establecerJefeDeRama(Long idEducador) {
        Persona jefe = personaRepository.findByIdAndRol(idEducador, Rol.EDUCADOR)
            .orElseThrow(() -> new ObjectNotFoundException("No existe un educador con ID: " + idEducador));

        Rama ramaDelEducador = obtenerRamaExistente(jefe.getRama().getId());

        ramaRepository.findByJefeDeRama_Id(idEducador)
            .filter(rama -> !rama.getId().equals(ramaDelEducador.getId()))
            .ifPresent(rama -> {
                throw new IllegalArgumentException("La persona ya es jefe de la rama " + rama.getNombre());
            });

        ramaDelEducador.setJefeDeRama(jefe);
        return ramaRepository.save(ramaDelEducador);
    }

    private Rama obtenerRamaExistente(Long ramaId) {
        return ramaRepository.findById(ramaId)
            .orElseThrow(() -> new ObjectNotFoundException("La rama con ID " + ramaId + " no existe"));
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
