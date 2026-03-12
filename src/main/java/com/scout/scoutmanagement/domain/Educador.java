package com.scout.scoutmanagement.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "educadores")
public class Educador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    @Column(nullable = false, unique = true)
    private Long dni;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rama_id", nullable = false)
    private Rama ramaQueDirige;

    @Column(nullable = false, unique = true)
    private String mail;

    public Educador() {
    }

    public Educador(String nombre, String apellido, Long dni, Rama ramaQueDirige, String mail) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.ramaQueDirige = ramaQueDirige;
        this.mail = mail;
    }

    public void cambiarDeRama(Rama ramaDestino) {
        this.ramaQueDirige = ramaDestino;
    }
}

