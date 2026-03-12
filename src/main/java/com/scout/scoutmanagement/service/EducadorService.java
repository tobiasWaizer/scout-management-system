package com.scout.scoutmanagement.service;

import com.scout.scoutmanagement.DTO.EducadorDTO;
import com.scout.scoutmanagement.domain.Educador;
import com.scout.scoutmanagement.domain.Rama;
import com.scout.scoutmanagement.repository.EducadorRepository;
import com.scout.scoutmanagement.repository.RamaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EducadorService {

    @Autowired
    private EducadorRepository educadorRepository;

    @Autowired
    private RamaRepository ramaRepository;

    public Educador crearEducador(EducadorDTO educadorDTO) {

        if (educadorRepository.findByDni(educadorDTO.getDni()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un educador con el DNI: " + educadorDTO.getDni());
        }

        if (educadorRepository.findByMail(educadorDTO.getMail()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un educador con el email: " + educadorDTO.getMail());
        }

        Rama rama = ramaRepository.findById(educadorDTO.getRamaId())
                .orElseThrow(() -> new IllegalArgumentException("La rama con ID " + educadorDTO.getRamaId() + " no existe"));

        Educador educador = new Educador(
                educadorDTO.getNombre(),
                educadorDTO.getApellido(),
                educadorDTO.getDni(),
                rama,
                educadorDTO.getMail()
        );

        return educadorRepository.save(educador);
    }
}

