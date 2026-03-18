package com.scout.scoutmanagement.repository;

import com.scout.scoutmanagement.domain.Pagos.Cuota;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CuotaRepository extends JpaRepository<Cuota, Long> {
}
