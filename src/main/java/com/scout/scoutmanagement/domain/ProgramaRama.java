package com.scout.scoutmanagement.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

import java.util.List;


@Entity
public class ProgramaRama {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    Rama rama;

    @OneToMany
    @JoinColumn(name = "programa_rama_id")
    List<Actividad> actividades;

    String objetivo;
    String tematica;


    public void agregarActividad(Actividad actividad) {
        actividades.add(actividad);
    }

    public ProgramaRama() {

    }

    public ProgramaRama(Long id, Rama rama, String objetivo, String tematica) {
        this.id = id;
        this.rama = rama;
        this.objetivo = objetivo;
        this.tematica = tematica;
    }
}
