package com.scout.scoutmanagement.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CuotaVariableDTO {
    @NotNull(message = "el orden de la cuota es obligatorio")
    @Min(value = 1, message = "el orden de la cuota debe comenzar en 1")
    private Integer ordenCuota;

    @NotNull(message = "el monto de la cuota es obligatorio")
    @Positive(message = "el monto de la cuota debe ser positivo")
    private Long monto;
}



