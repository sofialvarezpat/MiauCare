package com.example.MiauCare.Veterinarios.Repository;

import com.example.MiauCare.Veterinarios.Entity.Veterinario;
import com.example.MiauCare.Veterinarios.enums.EstadoVeterinario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VeterinarioRepository extends JpaRepository<Veterinario, Long> {
    Optional<Veterinario> findByCorreo(String correo);
    List<Veterinario> findByEstado(EstadoVeterinario estado);
}