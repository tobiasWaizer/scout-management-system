package com.scout.scoutmanagement.service;

import com.scout.scoutmanagement.DTO.CostoDTO;
import com.scout.scoutmanagement.DTO.CuotaVariableDTO;
import com.scout.scoutmanagement.domain.Pagos.Costos;
import com.scout.scoutmanagement.domain.Pagos.Cuota;
import com.scout.scoutmanagement.domain.Pagos.CostosVariables;
import com.scout.scoutmanagement.domain.Pagos.Pago;
import com.scout.scoutmanagement.domain.Persona;
import com.scout.scoutmanagement.repository.CostosRepository;
import com.scout.scoutmanagement.repository.CostosVariablesRepository;
import com.scout.scoutmanagement.repository.CuotaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class CostosService {

    private final CostosVariablesRepository costosVariablesRepository;
    private final CostosRepository costosRepository;
    private final CuotaRepository cuotaRepository;
    private final PersonaService personaService;

    public CostosService(
        CostosVariablesRepository costosVariablesRepository,
        CostosRepository costosRepository,
        CuotaRepository cuotaRepository,
        PersonaService personaService
    ) {
        this.costosVariablesRepository = costosVariablesRepository;
        this.costosRepository = costosRepository;
        this.cuotaRepository = cuotaRepository;
        this.personaService = personaService;
    }

    @Transactional(readOnly = true)
    public List<Costos> obtenerCostosPorIds(List<Long> costoIds) {
        return costosRepository.findAllById(costoIds);
    }

    @Transactional(readOnly = true)
    public List<Cuota> obtenerCuotasPorIds(List<Long> cuotaIds) {
        return cuotaRepository.findAllById(cuotaIds);
    }

    @Transactional
    public void marcarComoPagado(List<Costos> costos, Pago pago) {
        if (costos == null || costos.isEmpty()) {
            throw new IllegalArgumentException("Debes enviar al menos un costo para marcar como pagado");
        }
        if (pago == null || pago.getId() == null) {
            throw new IllegalArgumentException("El pago debe existir antes de asociarlo a los costos");
        }

        costos.forEach(costo -> asociarPago(costo, pago));
        costosRepository.saveAll(costos);
    }

    @Transactional
    public void marcarCuotasComoPagadas(List<Cuota> cuotas, Pago pago) {
        if (cuotas == null || cuotas.isEmpty()) {
            throw new IllegalArgumentException("Debes enviar al menos una cuota para marcar como pagada");
        }
        if (pago == null || pago.getId() == null) {
            throw new IllegalArgumentException("El pago debe existir antes de asociarlo a las cuotas");
        }

        cuotas.forEach(cuota -> cuota.setPago(pago));
        cuotaRepository.saveAll(cuotas);
    }

    private void asociarPago(Costos costo, Pago pago) {
        costo.getCuotas().forEach(cuota -> cuota.setPago(pago));
    }

    @Transactional //solo creamos costos variables ya que los fijos se autogeneran automaticamente
    public CostosVariables crearCosto(CostoDTO costoDTO) {
        Persona creadora = personaService.obtenerPersona(costoDTO.getId_creador());

        List<Persona> personasActivas = personaService.obtenerPersonasActivas();
        if (personasActivas.isEmpty()) {
            throw new IllegalArgumentException("No hay personas activas para asignar el costo");
        }

        List<CostosVariables> costosAGuardar = construirCostosPorPersonaYCuotas(costoDTO, creadora, personasActivas);

        List<CostosVariables> costosGuardados = costosVariablesRepository.saveAll(costosAGuardar);
        return costosGuardados.get(0); //devuelve el costo creado
    }

    private List<CostosVariables> construirCostosPorPersonaYCuotas(
        CostoDTO costoDTO,
        Persona creadora,
        List<Persona> personasActivas
    ) {
        List<CostosVariables> costosAGuardar = new ArrayList<>();
        List<CuotaVariableDTO> cuotas = resolverCuotas(costoDTO);
        for (Persona personaActiva : personasActivas) {
            costosAGuardar.add(crearCostoParaPersona(costoDTO, creadora, personaActiva, cuotas));
        }
        return costosAGuardar;
    }

    private CostosVariables crearCostoParaPersona(
        CostoDTO costoDTO,
        Persona creadora,
        Persona personaActiva,
        List<CuotaVariableDTO> cuotas
    ) {
        CostosVariables costo = new CostosVariables();
        costo.setNombre(costoDTO.getNombre());
        costo.setPersonaQueTieneQuePagar(personaActiva);
        costo.setCreadoPor(creadora);
        for (CuotaVariableDTO cuotaDTO : cuotas) {
            costo.agregarCuota(crearCuota(cuotaDTO));
        }
        return costo;
    }

    private Cuota crearCuota(CuotaVariableDTO cuotaDTO) {
        Cuota cuota = new Cuota();
        cuota.setOrdenCuota(cuotaDTO.getOrdenCuota());
        cuota.setMonto(BigDecimal.valueOf(cuotaDTO.getMonto()));
        return cuota;
    }

    private List<CuotaVariableDTO> resolverCuotas(CostoDTO costoDTO) {
        if (costoDTO.getCuotas() != null && !costoDTO.getCuotas().isEmpty()) {
            validarOrdenesUnicos(costoDTO.getCuotas());
            return costoDTO.getCuotas().stream()
                .sorted(Comparator.comparingInt(CuotaVariableDTO::getOrdenCuota))
                .toList();
        }

        validarCamposLegacy(costoDTO);
        List<CuotaVariableDTO> cuotasLegacy = new ArrayList<>();
        for (int orden = 1; orden <= costoDTO.getCantidadCuotas(); orden++) {
            CuotaVariableDTO cuota = new CuotaVariableDTO();
            cuota.setOrdenCuota(orden);
            cuota.setMonto(costoDTO.getMontoDeCadaCuota());
            cuotasLegacy.add(cuota);
        }
        return cuotasLegacy;
    }

    private void validarOrdenesUnicos(List<CuotaVariableDTO> cuotas) {
        Set<Integer> ordenes = new HashSet<>();
        for (CuotaVariableDTO cuota : cuotas) {
            if (!ordenes.add(cuota.getOrdenCuota())) {
                throw new IllegalArgumentException("No se permiten cuotas con el mismo orden");
            }
        }
    }

    private void validarCamposLegacy(CostoDTO costoDTO) {
        if (costoDTO.getCantidadCuotas() == null || costoDTO.getMontoDeCadaCuota() == null) {
            throw new IllegalArgumentException(
                "Debes enviar 'cuotas' o bien 'cantidadCuotas' + 'montoDeCadaCuota'"
            );
        }
    }
}
