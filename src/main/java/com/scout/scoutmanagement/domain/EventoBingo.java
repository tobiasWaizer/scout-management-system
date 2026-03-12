package com.scout.scoutmanagement.domain;

import java.time.LocalDate;
import java.util.List;


public class EventoBingo extends Evento {
    List<Persona> cocineros;
    List<Persona> cartoneros; //quizas haga falta crear una clase generica para las personas
    List<Persona> bacha;

     public EventoBingo(Long id, String nombre, String descripcion, LocalDate fechaInicio, LocalDate fechaFin, List<Persona> cocineros, List<Persona> cartoneros, List<Persona> bacha) {
        super(id, nombre, descripcion, fechaInicio, fechaFin);
        this.cocineros = cocineros;
        this.cartoneros = cartoneros;
        this.bacha = bacha;
    }
}
