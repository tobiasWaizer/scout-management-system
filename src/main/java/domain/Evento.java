package domain;

import java.util.Date;

public class Evento {
    int id;
    String nombre;
    String descripcion;
    Date fechaInicio;
    Date fechaFin;

    public Evento(int id, String nombre, String descripcion, Date fechaInicio, Date fechaFin) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }
}
