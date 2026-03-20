package com.scout.scoutmanagement.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cursos")
@Getter
@Setter
public class EventoCurso extends Evento {

    public EventoCurso() {
        setAlcanceEvento(AlcanceEvento.EDUCADORES);
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "nivel", nullable = false)
    private NivelCurso nivel;

    @ManyToMany
    @JoinTable(
        name = "curso_suscriptos",
        joinColumns = @JoinColumn(name = "curso_id"),
        inverseJoinColumns = @JoinColumn(name = "persona_id")
    )
    private List<Persona> suscriptos = new ArrayList<>();

    
    @Override
    public TipoEvento getTipo() {
        return TipoEvento.CURSO;
    }

    public void agregarSuscripto(Persona persona) {
        if (suscriptos.contains(persona)) {
            throw new RuntimeException("Suscripto ya existente");
        }
        else suscriptos.add(persona);
    }
}


