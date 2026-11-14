package com.example.MiauCare.Veterinarios.Repository;

import com.example.MiauCare.Veterinarios.Entity.Veterinario;
import com.example.MiauCare.Veterinarios.enums.EstadoVeterinario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VeterinarioRepository extends JpaRepository<Veterinario, Long> {

    // Busca un veterinario por su correo (usado en Login y validaciones de token)
    Optional<Veterinario> findByCorreo(String correo);

    // Filtra veterinarios por su estado de cuenta (PENDIENTE, ACTIVO, INACTIVO, RECHAZADO)
    List<Veterinario> findByEstado(EstadoVeterinario estado);

    // NUEVO: Lista los veterinarios activos que tienen encendido "Disponible para Emergencias"
    List<Veterinario> findByEstadoAndDisponibleEmergenciasTrue(EstadoVeterinario estado);

    // NUEVO: Filtra el directorio médico por especialidad clínica
    List<Veterinario> findByEstadoAndEspecialidadId(EstadoVeterinario estado, Long especialidadId);
}