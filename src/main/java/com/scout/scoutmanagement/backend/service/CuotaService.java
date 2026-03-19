package com.scout.scoutmanagement.backend.service;

import com.scout.scoutmanagement.domain.Pagos.Cuota;
import com.scout.scoutmanagement.backend.repository.CuotaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CuotaService {

    private final CuotaRepository cuotaRepository;

    public CuotaService(CuotaRepository cuotaRepository) {
        this.cuotaRepository = cuotaRepository;
    }

    public List<Cuota> obtenerCuotasDePersonaOrdenadasPorCosto(Long idPersona, boolean pendiente, Integer anio) {
        return cuotaRepository.obtenerCuotasDePersonaOrdenadasPorCosto(idPersona, pendiente, anio);
    }

    public List<Cuota> obtenerCuotasPorIds(List<Long> cuotaIds) {
        return cuotaRepository.findAllById(cuotaIds);
    }

    public List<Cuota> guardarTodas(List<Cuota> cuotas) {
        return cuotaRepository.saveAll(cuotas);
    }
}



