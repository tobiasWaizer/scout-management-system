package com.scout.scoutmanagement.service;

import com.scout.scoutmanagement.domain.Pagos.Afiliacion;
import com.scout.scoutmanagement.domain.Pagos.CuotaMensual;
import com.scout.scoutmanagement.domain.Persona;
import com.scout.scoutmanagement.repository.AfiliacionRepository;
import com.scout.scoutmanagement.repository.CuotaMensualRepository;
import com.scout.scoutmanagement.repository.PersonaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class CostosFijosAutomaticosService {

    private final PersonaRepository personaRepository;
    private final AfiliacionRepository afiliacionRepository;
    private final CuotaMensualRepository cuotaMensualRepository;

    @Value("${pagos.importe.afiliacion:5000}")
    private BigDecimal importeAfiliacion;

    @Value("${pagos.importe.cuota-mensual:10000}")
    private BigDecimal importeCuotaMensual;

    public CostosFijosAutomaticosService(
        PersonaRepository personaRepository,
        AfiliacionRepository afiliacionRepository,
        CuotaMensualRepository cuotaMensualRepository
    ) {
        this.personaRepository = personaRepository;
        this.afiliacionRepository = afiliacionRepository;
        this.cuotaMensualRepository = cuotaMensualRepository;
    }

    @Transactional
    public void generarParaTodasLasPersonas(Integer anio, Integer mes) {
        personaRepository.findByActivoTrue().forEach(persona -> generarParaPersona(persona, anio, mes));
    }

    @Transactional
    public void generarDesdeMesHastaFinDeAnioParaTodasLasPersonas(Integer anio, Integer mesInicio) {
        personaRepository.findByActivoTrue()
            .forEach(persona -> generarDesdeMesHastaFinDeAnioParaPersona(persona, anio, mesInicio));
    }

    @Transactional
    public void generarParaPersona(Persona persona, Integer anio, Integer mes) {
        if (!Boolean.TRUE.equals(persona.getActivo())) {
            return;
        }
        generarAfiliacionSiNoExiste(persona, anio);
        generarCuotaMensualSiNoExiste(persona, anio, mes);
    }

    @Transactional
    public void generarDesdeMesHastaFinDeAnioParaPersona(Persona persona, Integer anio, Integer mesInicio) {
        if (!Boolean.TRUE.equals(persona.getActivo())) {
            return;
        }
        generarAfiliacionSiNoExiste(persona, anio);
        for (int mes = mesInicio; mes <= 12; mes++) {
            generarCuotaMensualSiNoExiste(persona, anio, mes);
        }
    }

    private void generarAfiliacionSiNoExiste(Persona persona, Integer anio) {
        if (afiliacionRepository.existsByPersonaQueTieneQuePagar_IdAndAnio(persona.getId(), anio)) {
            return;
        }

        Afiliacion afiliacion = new Afiliacion();
        afiliacion.setPersonaQueTieneQuePagar(persona);
        afiliacion.setAnio(anio);
        afiliacion.setImporte(importeAfiliacion);
        afiliacionRepository.save(afiliacion);
    }

    private void generarCuotaMensualSiNoExiste(Persona persona, Integer anio, Integer mes) {
        if (cuotaMensualRepository.existsByPersonaQueTieneQuePagar_IdAndAnioAndMes(persona.getId(), anio, mes)) {
            return;
        }

        CuotaMensual cuotaMensual = new CuotaMensual();
        cuotaMensual.setPersonaQueTieneQuePagar(persona);
        cuotaMensual.setAnio(anio);
        cuotaMensual.setMes(mes);
        cuotaMensual.setImporte(importeCuotaMensual);
        cuotaMensualRepository.save(cuotaMensual);
    }
}
