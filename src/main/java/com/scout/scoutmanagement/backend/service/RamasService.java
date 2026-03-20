package com.scout.scoutmanagement.backend.service;

import com.scout.scoutmanagement.domain.Rama;
import com.scout.scoutmanagement.backend.exception.ObjectNotFoundException;
import com.scout.scoutmanagement.backend.repository.RamaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RamasService {

    private final RamaRepository ramaRepository;

    public RamasService(RamaRepository ramaRepository) {
        this.ramaRepository = ramaRepository;
    }

    public Rama obtenerRamaPorId(Long ramaId) {
        if (ramaId == null) {
            throw new IllegalArgumentException("El ID de rama es obligatorio");
        }

        return ramaRepository.findById(ramaId)
            .orElseThrow(() -> new ObjectNotFoundException("No existe una rama con ID: " + ramaId));
    }

    public Optional<Rama> obtenerRamaDondeEsJefe(Long idEducador) {
        return ramaRepository.findByJefeDeRama_Id(idEducador);
    }

    public Rama guardar(Rama rama) {
        return ramaRepository.save(rama);
    }

    public List<Rama> obtenerTodasLasRamas() {
        return ramaRepository.findAll();
    }
}




