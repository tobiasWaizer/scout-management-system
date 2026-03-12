package com.scout.scoutmanagement.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "beneficiarios")
public class Beneficiario {

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
    @JoinColumn(name = "rama_id")
    private Rama rama;

    public Beneficiario() {
    }

}

