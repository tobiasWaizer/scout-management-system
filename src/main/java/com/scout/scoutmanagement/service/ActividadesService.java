package com.scout.scoutmanagement.service;

import com.scout.scoutmanagement.DTO.ActividadDTO;
import com.scout.scoutmanagement.domain.Actividad;
import com.scout.scoutmanagement.domain.Persona;
import com.scout.scoutmanagement.domain.Rol;
import com.scout.scoutmanagement.exception.ObjectNotFoundException;
import com.scout.scoutmanagement.repository.ActividadRepository;
import com.scout.scoutmanagement.repository.PersonaRepository;
import org.springframework.stereotype.Service;


@Service
public class ActividadesService {

    private final ActividadRepository actividadRepository;
    private final PersonaRepository personaRepository;

    public ActividadesService(ActividadRepository actividadRepository, PersonaRepository personaRepository) {
        this.actividadRepository = actividadRepository;
        this.personaRepository = personaRepository;
    }

    public Actividad crearActividad(ActividadDTO actividadDTO) {

        Persona educador = personaRepository.findByIdAndRol(actividadDTO.getEducadorResponsableId(), Rol.EDUCADOR)
            .orElseThrow(() -> new ObjectNotFoundException("No existe un educador con ID: " + actividadDTO.getEducadorResponsableId()));

        Persona beneficiario = personaRepository.findByIdAndRol(actividadDTO.getBeneficiarioACargoId(), Rol.BENEFICIARIO)
            .orElseThrow(() -> new ObjectNotFoundException("No existe un beneficiario con ID: " + actividadDTO.getBeneficiarioACargoId()));

        Actividad actividad = new Actividad(
            actividadDTO.getTitulo(),
            actividadDTO.getObjetivo(),
            actividadDTO.getDesarrollo(),
            actividadDTO.getMateriales(),
            actividadDTO.getRecupero(),
            actividadDTO.getDuracionMinutos(),
            educador,
            beneficiario
        );

        return actividadRepository.save(actividad);
    }
}
