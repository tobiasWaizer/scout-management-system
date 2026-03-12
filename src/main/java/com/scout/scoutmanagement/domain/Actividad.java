package com.scout.scoutmanagement.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.Duration;
import java.util.List;

@Entity
@Table(name = "actividades")
public class Actividad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    Educador educadorResponsable;

    @ManyToOne
    Beneficiario beneficiarioACargo;

    String titulo;
    String materiales; //quizas solo string
    String desarrollo;
    String objetivo;
    String recupero;
    Duration duracion;
}
