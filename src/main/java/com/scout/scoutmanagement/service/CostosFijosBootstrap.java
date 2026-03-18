package com.scout.scoutmanagement.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.YearMonth;

@Component
@ConditionalOnProperty(name = "pagos.bootstrap.enabled", havingValue = "true", matchIfMissing = true)
public class CostosFijosBootstrap {

    private final CostosFijosAutomaticosService costosFijosAutomaticosService;
    private final PersonaService personaService;

    @Value("${pagos.cuota-mensual.mes-inicio-generacion:3}")
    private Integer mesInicioGeneracion;

    public CostosFijosBootstrap(
        CostosFijosAutomaticosService costosFijosAutomaticosService,
        PersonaService personaService
    ) {
        this.costosFijosAutomaticosService = costosFijosAutomaticosService;
        this.personaService = personaService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void generarCostosIniciales() {
        YearMonth periodoActual = YearMonth.now();
        personaService.obtenerPersonasActivas().forEach(persona ->
            costosFijosAutomaticosService.generarDesdeMesHastaFinDeAnioParaPersona(
                persona,
                periodoActual.getYear(),
                mesInicioGeneracion
            )
        );
    }
}



