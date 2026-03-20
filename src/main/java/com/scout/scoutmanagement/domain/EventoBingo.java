package com.scout.scoutmanagement.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "eventos_bingo")
@Getter
@Setter
public class EventoBingo extends Evento {
    @ManyToMany
    private List<Persona> cocineros;
    @ManyToMany
    private List<Persona> cartoneros;
    @ManyToMany
    private List<Persona> bacha;

    public EventoBingo() {
        setAlcanceEvento(AlcanceEvento.GENERAL);
    }

    public EventoBingo(String titulo, List<Persona> cocineros, List<Persona> cartoneros, List<Persona> bacha) {
        setTitulo(titulo);
        setAlcanceEvento(AlcanceEvento.GENERAL);
        this.cocineros = cocineros;
        this.cartoneros = cartoneros;
        this.bacha = bacha;
    }

    @Override
    public TipoEvento getTipo() {
        return TipoEvento.BINGO;
    }
}

