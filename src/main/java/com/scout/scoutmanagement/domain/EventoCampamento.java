package com.scout.scoutmanagement.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "eventos_campamento")
@Getter
@Setter
public class EventoCampamento extends Evento {
    private String lugar;
    private Long contactoLugar;

    public EventoCampamento() {
    }

    public EventoCampamento(AlcanceEvento alcanceEvento) {
        setAlcanceEvento(alcanceEvento);
    }

    public EventoCampamento(AlcanceEvento alcanceEvento, String lugar, Long contactoLugar) {
        setAlcanceEvento(alcanceEvento);
        this.lugar = lugar;
        this.contactoLugar = contactoLugar;
    }


    @Override
    public TipoEvento getTipo() {
        return TipoEvento.CAMPAMENTO;
    }
}

