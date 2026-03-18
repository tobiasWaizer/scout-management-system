package com.scout.scoutmanagement.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

import java.util.List;

@Entity
@Table(name = "eventos_bingo")
public class EventoBingo extends Evento {
    @ManyToMany
    List<Persona> cocineros;
    @ManyToMany
    List<Persona> cartoneros;
    @ManyToMany
    List<Persona> bacha;

    public EventoBingo() {
    }

    public EventoBingo(String titulo, List<Persona> cocineros, List<Persona> cartoneros, List<Persona> bacha) {
        setTitulo(titulo);
        this.cocineros = cocineros;
        this.cartoneros = cartoneros;
        this.bacha = bacha;
    }
}
