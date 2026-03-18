package com.scout.scoutmanagement.DTO;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class PagoDTO {

    @NotNull(message = "El ID de la persona que paga es obligatorio")
    @Positive(message = "El ID de la persona que paga debe ser positivo")
    private Long personaId;

    @NotNull(message = "El ID de la persona que registra el pago es obligatorio")
    @Positive(message = "El ID de la persona que registra el pago debe ser positivo")
    private Long personaQueRegistraId;

    @NotEmpty(message = "Debe enviar al menos una cuota a pagar")
    private List<@NotNull(message = "Cada cuota debe tener ID")
            @Positive(message = "Cada ID de cuota debe ser positivo") Long> cuotaIds;

    private LocalDate fecha;

    public PagoDTO() {
    }
}

