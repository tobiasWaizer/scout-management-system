package com.scout.scoutmanagement.service;

import com.scout.scoutmanagement.DTO.PagoDTO;
import com.scout.scoutmanagement.domain.Pagos.Costos;
import com.scout.scoutmanagement.domain.Pagos.Pago;
import com.scout.scoutmanagement.domain.Persona;
import com.scout.scoutmanagement.exception.ObjectNotFoundException;
import com.scout.scoutmanagement.repository.PagoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PagosService {

    private final PagoRepository pagoRepository;
    private final CostosService costosService;
    private final PersonaService personaService;

    public PagosService(
        PagoRepository pagoRepository,
        CostosService costosService,
        PersonaService personaService
    ) {
        this.pagoRepository = pagoRepository;
        this.costosService = costosService;
        this.personaService = personaService;
    }

    @Transactional
    public Pago generarPago(PagoDTO pagoDTO) {
        validarIdsDeCostosSinRepetidos(pagoDTO.getCostoIds());

        Persona personaQuePaga = personaService.obtenerPersona(pagoDTO.getPersonaId());
        Persona personaQueRegistra = personaService.obtenerPersona(pagoDTO.getPersonaQueRegistraId());
        //validarPersonasActivas(personaQuePaga, personaQueRegistra); quizas en un futuro lo activo pero no de momento

        List<Costos> costos = costosService.obtenerCostosPorIds(pagoDTO.getCostoIds());
        validarQueTodosLosCostosExistan(pagoDTO.getCostoIds(), costos);
        validarCostosCorrespondanALaPersona(costos, personaQuePaga.getId());
        validarCostosNoPagados(costos);


        Pago pago = new Pago();
        pago.setPersona(personaQuePaga);
        pago.setPersonaQueRegistra(personaQueRegistra);
        pago.setFecha(pagoDTO.getFecha() != null ? pagoDTO.getFecha() : LocalDate.now());
        pago.setCostos(costos);

        Pago pagoARetornar = pagoRepository.save(pago);
        costosService.marcarComoPagado(costos, pagoARetornar);


        return pagoARetornar;
    }

    private void validarIdsDeCostosSinRepetidos(List<Long> costoIds) {
        Set<Long> idsSinRepetir = new HashSet<>(costoIds);
        if (idsSinRepetir.size() != costoIds.size()) {
            throw new IllegalArgumentException("No se permiten IDs de costos repetidos en el mismo pago");
        }
    }

    private void validarPersonasActivas(Persona personaQuePaga, Persona personaQueRegistra) {
        /*if (!Boolean.TRUE.equals(personaQuePaga.getActivo())) {
            throw new IllegalArgumentException("La persona que paga debe estar activa");
        }*/
        if (!Boolean.TRUE.equals(personaQueRegistra.getActivo())) {
            throw new IllegalArgumentException("La persona que registra el pago debe estar activa");
        }
    }

    private void validarQueTodosLosCostosExistan(List<Long> costoIdsSolicitados, List<Costos> costosEncontrados) {
        if (costosEncontrados.size() != costoIdsSolicitados.size()) {
            throw new ObjectNotFoundException("Uno o mas costos enviados no existen");
        }
    }

    private void validarCostosCorrespondanALaPersona(List<Costos> costos, Long personaId) {
        boolean hayCostoDeOtraPersona = costos.stream()
            .anyMatch(costo -> !costo.getPersonaQueTieneQuePagar().getId().equals(personaId));

        if (hayCostoDeOtraPersona) {
            throw new IllegalArgumentException("Todos los costos deben pertenecer a la persona que paga");
        }
    }

    private void validarCostosNoPagados(List<Costos> costos) {
        costos.stream()
            .filter(costo -> costo.getCuotas().stream().anyMatch(cuota -> cuota.getPago() != null))
            .findFirst()
            .ifPresent(costo -> {
                throw new IllegalArgumentException("El costo con ID " + costo.getId() + " ya fue pagado");
            });
    }
}
