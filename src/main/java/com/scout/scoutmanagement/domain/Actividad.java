package com.scout.scoutmanagement.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "actividades")
@Getter
@Setter
public class Actividad extends Evento {

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

    public Actividad() {
    }

    public Actividad(String titulo, String objetivo, String desarrollo, String materiales,
                     String recupero, Long duracionMinutos, Persona educadorResponsable,
                     Persona beneficiarioACargo) {
        setTitulo(titulo);
        this.objetivo = objetivo;
        this.desarrollo = desarrollo;
        this.materiales = materiales;
        this.recupero = recupero;
        this.duracionMinutos = duracionMinutos;
        this.educadorResponsable = educadorResponsable;
        this.beneficiarioACargo = beneficiarioACargo;
    }
}
