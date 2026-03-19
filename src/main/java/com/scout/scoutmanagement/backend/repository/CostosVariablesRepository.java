package com.scout.scoutmanagement.backend.repository;

import com.scout.scoutmanagement.domain.Pagos.CostosVariables;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CostosVariablesRepository extends JpaRepository<CostosVariables, Long> {
}



