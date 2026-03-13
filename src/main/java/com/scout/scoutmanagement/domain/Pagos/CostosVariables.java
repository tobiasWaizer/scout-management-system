package com.scout.scoutmanagement.domain.Pagos;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
@Entity
@DiscriminatorValue("VARIABLE")
public class CostosVariables extends Costos {
    @Column(nullable = false)
    private String nombre; // Ejemplo: campamento corto

}
