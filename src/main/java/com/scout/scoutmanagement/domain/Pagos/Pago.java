package com.scout.scoutmanagement.domain.Pagos;

import com.scout.scoutmanagement.domain.Persona;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
public class Pago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne
    @JoinColumn(name = "persona_id", nullable = false)
    private Persona persona;

    @Setter
    @ManyToOne
    @JoinColumn(name = "persona_que_registra_id", nullable = false)
    private Persona personaQueRegistra;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "pago_id")
    private List<Costos> costos = new ArrayList<>();

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal montoTotal = BigDecimal.ZERO;

    @Setter
    @Column(nullable = false)
    private LocalDate fecha;

    public Pago(Long id, Persona persona, Persona personaQueRegistra, LocalDate fecha) {
        this.id = id;
        this.persona = persona;
        this.personaQueRegistra = personaQueRegistra;
        this.fecha = fecha;
        recalcularMontoTotal();
    }

    public Pago() {

    }

    public void addCosto(Costos costo) {
        this.costos.add(costo);
        recalcularMontoTotal();
    }

    public void recalcularMontoTotal() {
        this.montoTotal = this.costos.stream()
                .map(Costos::getImporte)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void setCostos(List<Costos> costos) {
        this.costos = costos != null ? costos : new ArrayList<>();
        recalcularMontoTotal();
    }

}


