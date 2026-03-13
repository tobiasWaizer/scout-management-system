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

    @Value("${pagos.cuota-mensual.mes-inicio-generacion:3}")
    private Integer mesInicioGeneracion;

    public CostosFijosBootstrap(CostosFijosAutomaticosService costosFijosAutomaticosService) {
        this.costosFijosAutomaticosService = costosFijosAutomaticosService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void generarCostosIniciales() {
        YearMonth periodoActual = YearMonth.now();
        costosFijosAutomaticosService.generarDesdeMesHastaFinDeAnioParaTodasLasPersonas(
            periodoActual.getYear(),
            mesInicioGeneracion
        );
    }
}



