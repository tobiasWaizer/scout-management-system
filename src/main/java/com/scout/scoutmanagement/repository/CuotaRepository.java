package com.scout.scoutmanagement.repository;

import com.scout.scoutmanagement.domain.Pagos.Cuota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CuotaRepository extends JpaRepository<Cuota, Long> {
	@Query(
		"SELECT c FROM Cuota c " +
		"WHERE c.costo.personaQueTieneQuePagar.id = :personaId " +
		"AND c.costo.anio >= :anio " +
		"AND (:pendiente = false OR c.pago IS NULL) " +
		"ORDER BY c.costo.anio ASC, c.costo.id ASC, c.ordenCuota ASC"
	)
	List<Cuota> obtenerCuotasDePersonaOrdenadasPorCosto(
		@Param("personaId") Long personaId,
		@Param("pendiente") boolean pendiente,
		@Param("anio") Integer anio
	);
}
