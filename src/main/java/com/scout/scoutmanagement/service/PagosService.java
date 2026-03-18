package com.scout.scoutmanagement.service;

import com.scout.scoutmanagement.DTO.PagoDTO;
import com.scout.scoutmanagement.domain.Pagos.Cuota;
import com.scout.scoutmanagement.domain.Pagos.Pago;
import com.scout.scoutmanagement.domain.Persona;
import com.scout.scoutmanagement.exception.ObjectNotFoundException;
import com.scout.scoutmanagement.repository.PagoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Year;
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
        validarIdsDeCuotasSinRepetidos(pagoDTO.getCuotaIds());

        Persona personaQuePaga = personaService.obtenerPersona(pagoDTO.getPersonaId());
        Persona personaQueRegistra = personaService.obtenerPersona(pagoDTO.getPersonaQueRegistraId());
        //validarPersonasActivas(personaQuePaga, personaQueRegistra); quizas en un futuro lo activo pero no de momento

        List<Cuota> cuotas = costosService.obtenerCuotasPorIds(pagoDTO.getCuotaIds());
        validarQueTodasLasCuotasExistan(pagoDTO.getCuotaIds(), cuotas);
        validarCuotasCorrespondanALaPersona(cuotas, personaQuePaga.getId());
        validarCuotasNoPagadas(cuotas);


        Pago pago = new Pago();
        pago.setPersona(personaQuePaga);
        pago.setPersonaQueRegistra(personaQueRegistra);
        pago.setFecha(pagoDTO.getFecha() != null ? pagoDTO.getFecha() : LocalDate.now());
        pago.setCuotas(cuotas);

        Pago pagoARetornar = pagoRepository.save(pago);
        costosService.marcarCuotasComoPagadas(cuotas, pagoARetornar);


        return pagoARetornar;
    }

    private void validarIdsDeCuotasSinRepetidos(List<Long> cuotaIds) {
        Set<Long> idsSinRepetir = new HashSet<>(cuotaIds);
        if (idsSinRepetir.size() != cuotaIds.size()) {
            throw new IllegalArgumentException("No se permiten IDs de cuotas repetidos en el mismo pago");
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

    private void validarQueTodasLasCuotasExistan(List<Long> cuotaIdsSolicitadas, List<Cuota> cuotasEncontradas) {
        if (cuotasEncontradas.size() != cuotaIdsSolicitadas.size()) {
            throw new ObjectNotFoundException("Una o mas cuotas enviadas no existen");
        }
    }

    private void validarCuotasCorrespondanALaPersona(List<Cuota> cuotas, Long personaId) {
        boolean hayCuotaDeOtraPersona = cuotas.stream()
            .anyMatch(cuota -> !cuota.getCosto().getPersonaQueTieneQuePagar().getId().equals(personaId));

        if (hayCuotaDeOtraPersona) {
            throw new IllegalArgumentException("Todas las cuotas deben pertenecer a la persona que paga");
        }
    }

    private void validarCuotasNoPagadas(List<Cuota> cuotas) {
        cuotas.stream()
            .filter(cuota -> cuota.getPago() != null)
            .findFirst()
            .ifPresent(cuota -> {
                throw new IllegalArgumentException("La cuota con ID " + cuota.getId() + " ya fue pagada");
            });
    }

    public List<Pago> obtenerPagosDePersonaId(Long idPersona) {
        return obtenerPagosDePersonaId(idPersona, Year.now().getValue());
    }

    public List<Pago> obtenerPagosDePersonaId(Long idPersona, Integer anio) {
        personaService.obtenerPersona(idPersona);
        return pagoRepository.obtenerPagosDePersonaDesdeAnioOrdenadasPorFecha(idPersona, anio);
    }
}
