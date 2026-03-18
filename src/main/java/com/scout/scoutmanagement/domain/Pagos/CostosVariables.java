package com.scout.scoutmanagement.domain.Pagos;

import com.scout.scoutmanagement.domain.Persona;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
@Entity
@DiscriminatorValue("VARIABLE")
public class CostosVariables extends Costos {
    @Column
    private String nombre; // Ejemplo: campamento corto

    public CostosVariables() {
        super();
    }

    public CostosVariables(String nombre, Persona personaQuePaga, Persona creadoPor, List<Cuota> cuotas) {
        super(personaQuePaga, creadoPor, BigDecimal.ZERO);
        this.nombre = nombre;
        setCuotas(cuotas);
    }
}
