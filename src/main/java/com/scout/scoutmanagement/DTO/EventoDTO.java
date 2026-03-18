package com.scout.scoutmanagement.DTO;

import com.scout.scoutmanagement.domain.AlcanceEvento;
import com.scout.scoutmanagement.domain.NivelCurso;
import com.scout.scoutmanagement.domain.TipoEvento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class EventoDTO {

    @NotNull(message = "El tipo de evento es obligatorio")
    private TipoEvento tipoEvento;

    @NotBlank(message = "El titulo es obligatorio")
    private String titulo;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDateTime fechaInicio;

    private LocalDateTime fechaFin;

    private AlcanceEvento alcanceEvento;

    @Positive(message = "El ID de rama debe ser un numero positivo")
    private Long ramaId;

    @Positive(message = "El ID del educador responsable debe ser un numero positivo")
    private Long educadorResponsableId;

    @Positive(message = "El ID del beneficiario debe ser un numero positivo")
    private Long beneficiarioACargoId;

    @Positive(message = "La duracion debe ser un numero positivo")
    private Long duracionMinutos;

    private String objetivo;
    private String desarrollo;
    private String materiales;
    private String recupero;

    private NivelCurso nivelCurso;

    private List<Long> suscriptosCursoIds;

    private String lugar;

    private Long contactoLugar;

    private List<Long> cocinerosIds;

    private List<Long> cartonerosIds;

    private List<Long> bachaIds;
}

