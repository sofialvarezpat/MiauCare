package com.example.MiauCare.Gatos.Repository;

import com.example.MiauCare.Administradores.enums.EstadoAprobacionGato;
import com.example.MiauCare.Gatos.Entity.Gato;
import com.example.MiauCare.Gatos.enums.EstadoVidaGato;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GatoRepository extends JpaRepository<Gato, Long> {

    // Busca un gato por su código único (ej: MC-024)
    Optional<Gato> findByCodigo(String codigo);

    // Filtra gatos según su estado de vida (ACTIVO, FALLECIDO, etc.)
    List<Gato> findByEstadoVida(EstadoVidaGato estadoVida);

    // Filtra gatos según su estado de aprobación (PENDIENTE, APROBADO, RECHAZADO)
    List<Gato> findByEstadoAprobacion(EstadoAprobacionGato estadoAprobacion);

    // Lista todos los gatos de una zona sin filtrar aprobación (uso interno/reportes)
    List<Gato> findByZonaId(Long zonaId);

    // Lista los gatos aprobados de una zona específica para el censo
    List<Gato> findByZonaIdAndEstadoAprobacion(Long zonaId, EstadoAprobacionGato estadoAprobacion);

    // Buscador por nombre general
    List<Gato> findByNombreContainingIgnoreCase(String nombre);

    // Buscador por nombre solo entre gatos aprobados en el censo
    List<Gato> findByNombreContainingIgnoreCaseAndEstadoAprobacion(String nombre, EstadoAprobacionGato estadoAprobacion);

    // NUEVO: Lista todos los gatos asignados a un veterinario responsable ("Tus pacientes" y "Directorio médico")
    List<Gato> findByVeterinarioResponsableId(Long veterinarioId);
}