package com.scout.scoutmanagement.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "actividades")
@Getter
@Setter
public class Actividad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "educador_id", nullable = false)
    private Persona educadorResponsable;

    @ManyToOne
    @JoinColumn(name = "beneficiario_id", nullable = false)
    private Persona beneficiarioACargo;

    private String titulo;
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
        this.titulo = titulo;
        this.objetivo = objetivo;
        this.desarrollo = desarrollo;
        this.materiales = materiales;
        this.recupero = recupero;
        this.duracionMinutos = duracionMinutos;
        this.educadorResponsable = educadorResponsable;
        this.beneficiarioACargo = beneficiarioACargo;
    }
}
