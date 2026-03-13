package com.scout.scoutmanagement.domain.Pagos;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@DiscriminatorValue("CUOTA_MENSUAL")
public class CuotaMensual extends CostosFijos{
    @Min(1)
    @Max(12)
    private Integer mes;

}
