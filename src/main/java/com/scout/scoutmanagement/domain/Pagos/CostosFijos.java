package com.scout.scoutmanagement.domain.Pagos;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@DiscriminatorValue("FIJO")
public class CostosFijos extends Costos {
    @Column(name = "anio")
    private Integer anio;

    @Column(name = "cantidad_meses")
    private Integer cantidadMeses;

}

