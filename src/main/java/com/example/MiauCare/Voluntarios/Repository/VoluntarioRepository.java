package com.example.MiauCare.Voluntarios.Repository;

import com.example.MiauCare.Voluntarios.Entity.Voluntario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VoluntarioRepository extends JpaRepository<Voluntario, Long> {
    Optional<Voluntario> findByCorreo(String correo);
}
