package com.scout.scoutmanagement.backend.repository;

import com.scout.scoutmanagement.domain.Pagos.Costos;
import com.scout.scoutmanagement.domain.Pagos.Motivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

@Repository
public interface CostosRepository extends JpaRepository<Costos, Long> {
	@Query("""
		select count(c) > 0
		from CostosFijos c
		where c.personaQueTieneQuePagar.id = :personaId
		  and c.anio = :anio
		  and c.motivo = :motivo
	""")
	boolean existsCostoFijoByPersonaAndAnioAndTipo(
		@Param("personaId") Long personaId,
		@Param("anio") Integer anio,
		@Param("motivo") Motivo motivo
	);
}



