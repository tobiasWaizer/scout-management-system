package com.scout.scoutmanagement.repository;

import com.scout.scoutmanagement.domain.Persona;
import com.scout.scoutmanagement.domain.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonaRepository extends JpaRepository<Persona, Long> {
    Optional<Persona> findByDni(Long dni);
    Optional<Persona> findByMail(String mail);
    Optional<Persona> findByIdAndRol(Long id, Rol rol);
}
