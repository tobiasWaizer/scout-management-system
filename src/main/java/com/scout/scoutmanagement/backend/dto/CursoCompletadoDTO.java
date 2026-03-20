package com.scout.scoutmanagement.backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CursoCompletadoDTO {

    @NotNull(message = "El id del curso es obligatorio")
    @Positive(message = "El id del curso debe ser un numero positivo")
    private Long idCurso;
}

