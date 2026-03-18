package com.scout.scoutmanagement.repository;

import com.scout.scoutmanagement.domain.Pagos.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {
}

