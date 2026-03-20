package com.scout.scoutmanagement.backend.service;

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
    private final PersonasService personasService;

    @Value("${pagos.cuota-mensual.mes-inicio-generacion:3}")
    private Integer mesInicioGeneracion;

    public CostosFijosBootstrap(
        CostosFijosAutomaticosService costosFijosAutomaticosService,
        PersonasService personasService
    ) {
        this.costosFijosAutomaticosService = costosFijosAutomaticosService;
        this.personasService = personasService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void generarCostosIniciales() {
        YearMonth periodoActual = YearMonth.now();
        personasService.obtenerPersonasActivas().forEach(persona ->
            costosFijosAutomaticosService.generarDesdeMesHastaFinDeAnioParaPersona(
                persona,
                periodoActual.getYear(),
                mesInicioGeneracion
            )
        );
    }
}





