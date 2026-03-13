package com.scout.scoutmanagement.domain.Pagos;

import com.scout.scoutmanagement.domain.Persona;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_costo")
public abstract class Costos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne
    @JoinColumn(name = "persona_objetivo_id", nullable = false)
    private Persona personaQueTieneQuePagar;

    @Setter
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal importe;

    @Setter
    @ManyToOne
    @JoinColumn(name = "creado_por_id")
    private Persona creadoPor;

}
