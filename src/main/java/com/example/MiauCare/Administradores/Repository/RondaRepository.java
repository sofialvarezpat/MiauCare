package com.example.MiauCare.Administradores.Repository;

import com.example.MiauCare.Administradores.Entity.Ronda;
import com.example.MiauCare.Administradores.enums.EstadoRonda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface RondaRepository extends JpaRepository<Ronda, Long> {

    // Para el selector "Horario de la ronda" y la agenda por día
    List<Ronda> findByFechaOrderByHoraInicioAsc(LocalDate fecha);

    // RN-01: rondas activas que se cruzan con el horario pedido
    @Query("SELECT r FROM Ronda r WHERE r.fecha = :fecha " +
            "AND r.estado IN :estados " +
            "AND r.horaInicio < :horaFin AND r.horaFin > :horaInicio " +
            "AND r.id <> :rondaIdExcluir")
    List<Ronda> buscarCruces(@Param("fecha") LocalDate fecha,
                             @Param("horaInicio") LocalTime horaInicio,
                             @Param("horaFin") LocalTime horaFin,
                             @Param("rondaIdExcluir") Long rondaIdExcluir,
                             @Param("estados") List<EstadoRonda> estados);

    // NUEVO: rondas activas en las que el veterinario ya está asignado en ese horario
    @Query("SELECT r FROM Ronda r WHERE r.veterinario.id = :veterinarioId " +
            "AND r.fecha = :fecha AND r.estado IN :estados " +
            "AND r.horaInicio < :horaFin AND r.horaFin > :horaInicio")
    List<Ronda> buscarCrucesVeterinario(@Param("veterinarioId") Long veterinarioId,
                                        @Param("fecha") LocalDate fecha,
                                        @Param("horaInicio") LocalTime horaInicio,
                                        @Param("horaFin") LocalTime horaFin,
                                        @Param("estados") List<EstadoRonda> estados);
}