package com.scout.scoutmanagement.repository;

import com.scout.scoutmanagement.domain.Rama;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RamaRepository extends JpaRepository<Rama, Long> {
}

