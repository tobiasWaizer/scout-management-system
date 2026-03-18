package com.scout.scoutmanagement.repository;

import com.scout.scoutmanagement.domain.Pagos.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {
	@Query(
		"SELECT p FROM Pago p " +
		"WHERE p.persona.id = :personaId " +
		"AND YEAR(p.fecha) >= :anio " +
		"ORDER BY p.fecha DESC, p.id DESC"
	)
	List<Pago> obtenerPagosDePersonaDesdeAnioOrdenadasPorFecha(
		@Param("personaId") Long personaId,
		@Param("anio") Integer anio
	);
}

