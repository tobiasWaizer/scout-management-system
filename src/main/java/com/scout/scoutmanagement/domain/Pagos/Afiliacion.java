package com.scout.scoutmanagement.domain.Pagos;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("AFILIACION")
public class Afiliacion extends CostosFijos {
}
