package com.scout.scoutmanagement.backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CostoDTO {

    @NotBlank(message = "el nombre del costo es obligatorio")
    private String nombre;

    @NotNull(message = "el id del que solicita el costo es obligatorio")
    @Positive(message = "el id del que solicita el costo debe ser positivo")
    private Long id_creador;

    // Modo legacy: cantidad + monto uniforme por cuota.
    @Min(value = 1, message = "la cantidad de cuotas debe ser mayor o igual a 1")
    private Integer cantidadCuotas;

    @Positive(message = "el monto de cada cuota debe ser positivo")
    private Long montoDeCadaCuota;

    // Modo nuevo: lista explícita de cuotas con orden y monto.
    @Valid
    private List<CuotaVariableDTO> cuotas;
}


