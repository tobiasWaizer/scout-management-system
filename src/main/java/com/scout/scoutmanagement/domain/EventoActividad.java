package com.scout.scoutmanagement.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "actividades")
@Getter
@Setter
public class EventoActividad extends Evento {

    @ManyToOne
    @JoinColumn(name = "educador_id", nullable = false)
    private Persona educadorResponsable;

    @ManyToOne
    @JoinColumn(name = "beneficiario_id")
    private Persona beneficiarioACargo;


    private String materiales;
    private String desarrollo;
    private String objetivo;
    private String recupero;

    @Column(name = "duracion_minutos")
    private Long duracionMinutos;

    public EventoActividad() {
    }

    public EventoActividad(String titulo, String objetivo, String desarrollo, String materiales,
                           String recupero, Long duracionMinutos, Persona educadorResponsable,
                           Persona beneficiarioACargo) {
        setTitulo(titulo);
        setAlcanceEvento(AlcanceEvento.RAMA);
        this.objetivo = objetivo;
        this.desarrollo = desarrollo;
        this.materiales = materiales;
        this.recupero = recupero;
        this.duracionMinutos = duracionMinutos;
        this.educadorResponsable = educadorResponsable;
        this.beneficiarioACargo = beneficiarioACargo;
    }

    @Override
    public TipoEvento getTipo() {
        return TipoEvento.ACTIVIDAD;
    }
}

