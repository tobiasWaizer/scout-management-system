package com.scout.scoutmanagement.domain;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(
    name = "estados_curso",
    uniqueConstraints = @UniqueConstraint(columnNames = {"persona_id", "rama_id"})
)
public class EstadoCurso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "persona_id", nullable = false)
    private Persona persona;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rama_id", nullable = false)
    private Rama rama;

    @Enumerated(EnumType.STRING)
    @Column(name = "nivel", nullable = false)
    private NivelCurso nivelActual;

    @Column(name = "actual", nullable = false)
    private Boolean actual = true;

    @ElementCollection
    @CollectionTable(
        name = "estado_curso_cursos_completados",
        joinColumns = @JoinColumn(name = "estado_curso_id")
    )
    @Column(name = "curso_id", nullable = false)
    private Set<Long> cursosCompletadosIds = new HashSet<>();


    public void marcarCompletado(EventoCurso curso){
        if(cursosCompletadosIds.contains(curso.getId())){
            throw new IllegalArgumentException("El curso ya se encuentra marcado como completado para esta persona");
        }
        else cursosCompletadosIds.add(curso.getId());
    }

    public static EstadoCurso inicial(Persona persona, Rama rama) {
        EstadoCurso estadoCurso = new EstadoCurso();
        estadoCurso.setPersona(persona);
        estadoCurso.setRama(rama);
        estadoCurso.setNivelActual(NivelCurso.NIVEL_1);
        estadoCurso.setActual(true);
        return estadoCurso;
    }

}


