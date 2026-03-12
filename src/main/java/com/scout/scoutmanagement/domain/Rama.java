package com.scout.scoutmanagement.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "ramas")
public class Rama {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    @OneToOne
    @JoinColumn(name = "jefe_de_rama_id")
    private Educador jefeDeRama;

    public Rama() {
    }

    public Rama(String nombre) {
        this.nombre = nombre;
    }


}

