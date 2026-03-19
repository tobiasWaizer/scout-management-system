package com.scout.scoutmanagement.backend.service;

import com.scout.scoutmanagement.domain.Pagos.Afiliacion;
import com.scout.scoutmanagement.domain.Pagos.CostosFijos;
import com.scout.scoutmanagement.domain.Pagos.Cuota;
import com.scout.scoutmanagement.domain.Pagos.Motivo;
import com.scout.scoutmanagement.domain.Persona;
import com.scout.scoutmanagement.backend.repository.CostosRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class CostosFijosAutomaticosService {

    private static final Motivo TIPO_AFILIACION = Motivo.AFILIACION;
    private static final Motivo TIPO_CUOTA_MENSUAL = Motivo.CUOTA_MENSUAL;

    private final CostosRepository costosRepository;

    @Value("${pagos.importe.afiliacion:5000}")
    private BigDecimal importeAfiliacion;

    @Value("${pagos.importe.cuota-mensual:10000}")
    private BigDecimal importeCuotaMensual;

    public CostosFijosAutomaticosService(
        CostosRepository costosRepository
    ) {
        this.costosRepository = costosRepository;
    }


    @Transactional
    public void generarParaPersona(Persona persona, Integer anio, Integer mes) {
        if (!Boolean.TRUE.equals(persona.getActivo())) {
            return;
        }
        generarAfiliacionSiNoExiste(persona, anio);
        generarCuotaMensualSiNoExiste(persona, anio, mes, 1);
    }

    @Transactional
    public void generarDesdeMesHastaFinDeAnioParaPersona(Persona persona, Integer anio, Integer mesInicio) {
        if (!Boolean.TRUE.equals(persona.getActivo())) {
            return;
        }
        generarAfiliacionSiNoExiste(persona, anio);
        int cantidadMeses = (12 - mesInicio) + 1;
        generarCuotaMensualSiNoExiste(persona, anio, mesInicio, cantidadMeses);
    }

    private void generarAfiliacionSiNoExiste(Persona persona, Integer anio) {
        if (costosRepository.existsCostoFijoByPersonaAndAnioAndTipo(persona.getId(), anio, TIPO_AFILIACION)) {
            return;
        }

        Afiliacion afiliacion = new Afiliacion();
        afiliacion.setMotivo(Motivo.AFILIACION);
        afiliacion.setCantidadMeses(1);
        afiliacion.setPersonaQueTieneQuePagar(persona);
        afiliacion.setAnio(anio);
        afiliacion.agregarCuota(crearCuota(1, importeAfiliacion));
        costosRepository.save(afiliacion);
    }

    private void generarCuotaMensualSiNoExiste(Persona persona, Integer anio, Integer mesInicio, Integer cantidadMeses) {
        if (costosRepository.existsCostoFijoByPersonaAndAnioAndTipo(persona.getId(), anio, TIPO_CUOTA_MENSUAL)) {
            return;
        }

        CostosFijos cuotaMensual = new CostosFijos();
        cuotaMensual.setMotivo(Motivo.CUOTA_MENSUAL);
        cuotaMensual.setPersonaQueTieneQuePagar(persona);
        cuotaMensual.setAnio(anio);
        cuotaMensual.setCantidadMeses(cantidadMeses);

        for (int orden = 1; orden <= cantidadMeses; orden++) {
            cuotaMensual.agregarCuota(crearCuota(orden, importeCuotaMensual));
        }

        costosRepository.save(cuotaMensual);
    }

    private Cuota crearCuota(int orden, BigDecimal monto) {
        Cuota cuota = new Cuota();
        cuota.setOrdenCuota(orden);
        cuota.setMonto(monto);
        return cuota;
    }
}


