package com.scout.scoutmanagement.repository;

import com.scout.scoutmanagement.domain.Educador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EducadorRepository extends JpaRepository<Educador, Long> {
    Optional<Educador> findByDni(Long dni);
    Optional<Educador> findByMail(String mail);
}

