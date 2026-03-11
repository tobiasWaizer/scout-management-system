package domain;

import java.util.Date;
import java.util.List;

public class EventoBingo extends Evento{
    List<Educador> cocineros;
    List<Educador> cartoneros; //quizas haga falta crear una clase generica para las personas
    List<Educador> bacha;

     public EventoBingo(int id, String nombre, String descripcion, Date fechaInicio, Date fechaFin, List<Educador> cocineros, List<Educador> cartoneros, List<Educador> bacha) {
        super(id, nombre, descripcion, fechaInicio, fechaFin);
        this.cocineros = cocineros;
        this.cartoneros = cartoneros;
        this.bacha = bacha;
    }
}
