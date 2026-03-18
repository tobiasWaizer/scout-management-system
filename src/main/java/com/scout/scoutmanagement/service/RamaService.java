package com.scout.scoutmanagement.service;

import com.scout.scoutmanagement.domain.Rama;
import com.scout.scoutmanagement.exception.ObjectNotFoundException;
import com.scout.scoutmanagement.repository.RamaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RamaService {

    private final RamaRepository ramaRepository;

    public RamaService(RamaRepository ramaRepository) {
        this.ramaRepository = ramaRepository;
    }

    public Rama obtenerRamaPorId(Long ramaId) {
        if (ramaId == null) {
            throw new IllegalArgumentException("El ID de rama es obligatorio");
        }
        if (ramaId <= 0) {
            throw new IllegalArgumentException("El ID de rama debe ser mayor a 0");
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


