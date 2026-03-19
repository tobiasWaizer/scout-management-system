package com.scout.scoutmanagement.domain.Pagos;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(
    name = "cuota",
    uniqueConstraints = @UniqueConstraint(columnNames = {"costo_id", "orden_cuota"})
)
public class Cuota {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "orden_cuota", nullable = false)
    private Integer ordenCuota;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal monto;

    @ManyToOne(optional = false)
    @JoinColumn(name = "costo_id", nullable = false)
    private Costos costo;

    @ManyToOne
    @JoinColumn(name = "pago_id")
    private Pago pago;
}


