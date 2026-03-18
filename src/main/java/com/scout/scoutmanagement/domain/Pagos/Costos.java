package com.scout.scoutmanagement.domain.Pagos;

import com.scout.scoutmanagement.domain.Persona;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Setter
@Getter
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_costo")
public abstract class Costos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Motivo motivo;

    @ManyToOne
    @JoinColumn(name = "persona_objetivo_id", nullable = false)
    private Persona personaQueTieneQuePagar;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal importe;

    @ManyToOne
    @JoinColumn(name = "creado_por_id")
    private Persona creadoPor;

    @OneToMany(mappedBy = "costo", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    private List<Cuota> cuotas = new ArrayList<>();

    protected Costos() {
        // Constructor requerido por JPA.
    }

    public Costos(Persona personaQuePaga, Persona creador, BigDecimal importe) {
        this.personaQueTieneQuePagar = personaQuePaga;
        this.creadoPor = creador;
        this.importe = importe;
    }

    public void setCuotas(List<Cuota> cuotas) {
        this.cuotas.clear();
        if (cuotas != null) {
            cuotas.forEach(this::agregarCuota);
        }
        recalcularImporteTotal();
    }

    public void agregarCuota(Cuota cuota) {
        cuota.setCosto(this);
        this.cuotas.add(cuota);
        this.cuotas.sort(Comparator.comparingInt(Cuota::getOrdenCuota));
        recalcularImporteTotal();
    }

    public int getCantidadCuotas() {
        return cuotas.size();
    }

    public void recalcularImporteTotal() {
        BigDecimal total = cuotas.stream()
            .map(Cuota::getMonto)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        setImporte(total);
    }
}
