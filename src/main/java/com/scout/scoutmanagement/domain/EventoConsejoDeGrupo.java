package com.scout.scoutmanagement.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "eventos_consejo")
public class EventoConsejoDeGrupo extends Evento {


    @Override
    public TipoEvento getTipo() {
        return TipoEvento.CONSEJO;
    }

    public EventoConsejoDeGrupo() {
        setAlcanceEvento(AlcanceEvento.EDUCADORES);
    }
}
