package com.scout.scoutmanagement.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ActividadDTO {

    @NotBlank(message = "El título es obligatorio")
    private String titulo;

    @NotBlank(message = "El objetivo es obligatorio")
    private String objetivo;

    @NotBlank(message = "El desarrollo es obligatorio")
    private String desarrollo;

    private String materiales;

    private String recupero;

    @NotNull(message = "La duración en minutos es obligatoria")
    @Positive(message = "La duración debe ser un número positivo")
    private Long duracionMinutos;

    @NotNull(message = "El ID del educador responsable es obligatorio")
    @Positive(message = "El ID del educador debe ser un número positivo")
    private Long educadorResponsableId;

    @NotNull(message = "El ID del beneficiario a cargo es obligatorio")
    @Positive(message = "El ID del beneficiario debe ser un número positivo")
    private Long beneficiarioACargoId;

    public ActividadDTO() {
    }
}

