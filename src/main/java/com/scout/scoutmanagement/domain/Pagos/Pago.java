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
import java.util.stream.Collectors;

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

    @OneToMany(mappedBy = "pago", cascade = CascadeType.ALL)
    private List<Cuota> cuotas = new ArrayList<>();

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

    public void addCuota(Cuota cuota) {
        cuota.setPago(this);
        this.cuotas.add(cuota);
        recalcularMontoTotal();
    }

    public void recalcularMontoTotal() {
        this.montoTotal = this.cuotas.stream()
                .map(Cuota::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void setCuotas(List<Cuota> cuotas) {
        this.cuotas = cuotas != null ? cuotas : new ArrayList<>();
        this.cuotas.forEach(cuota -> cuota.setPago(this));
        recalcularMontoTotal();
    }

    public void setCostos(List<Costos> costos) {
        List<Cuota> cuotasDesdeCostos = costos == null
            ? new ArrayList<>()
            : costos.stream()
                .flatMap(costo -> costo.getCuotas().stream())
                .collect(Collectors.toCollection(ArrayList::new));

        setCuotas(cuotasDesdeCostos);
    }

    public List<Costos> getCostos() {
        return cuotas.stream()
            .map(Cuota::getCosto)
            .distinct()
            .collect(Collectors.toList());
    }

    public int getCantidadCuotasPagadas() {
        return cuotas.size();
    }

    public void clearCuotas() {
        this.cuotas.clear();
        recalcularMontoTotal();
    }

}



