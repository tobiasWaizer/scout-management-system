package com.scout.scoutmanagement.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import java.time.LocalDate;

@Entity
public class Pago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    Persona persona;

    @Enumerated(EnumType.STRING)
    Motivo motivo;

    double monto;
    LocalDate fecha;

    public Pago(Long id, Persona persona, double monto, Motivo motivo, LocalDate fecha) {
        this.id = id;
        this.persona = persona;
        this.monto = monto;
        this.motivo = motivo;
        this.fecha = fecha;
    }

    public Pago() {

    }
}
