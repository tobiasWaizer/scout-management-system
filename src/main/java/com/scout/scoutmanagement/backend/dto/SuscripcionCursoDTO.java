package com.scout.scoutmanagement.backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SuscripcionCursoDTO {

    @NotNull(message = "El id de la persona es obligatorio")
    @Positive(message = "El id de la persona debe ser un numero positivo")
    private Long personaId;
}

