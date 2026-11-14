package com.example.MiauCare.HistorialClinico.Repository;

import com.example.MiauCare.HistorialClinico.Entity.CirugiaCes;
import com.example.MiauCare.HistorialClinico.enums.EstadoCirugia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface CirugiaCesRepository extends JpaRepository<CirugiaCes, Long> {

    List<CirugiaCes> findByFechaOrderByHoraInicioAsc(LocalDate fecha);

    List<CirugiaCes> findByVeterinarioIdOrderByFechaDescHoraInicioAsc(Long veterinarioId);

    List<CirugiaCes> findByVeterinarioIdAndFechaOrderByHoraInicioAsc(Long veterinarioId, LocalDate fecha);

    List<CirugiaCes> findByGatoIdOrderByFechaDesc(Long gatoId);

    boolean existsByGatoIdAndEstadoIn(Long gatoId, List<EstadoCirugia> estados);

    // El veterinario no puede tener dos cosas al mismo tiempo
    @Query("SELECT c FROM CirugiaCes c WHERE c.veterinario.id = :veterinarioId " +
            "AND c.fecha = :fecha AND c.estado IN :estados " +
            "AND c.horaInicio < :horaFin AND c.horaFin > :horaInicio")
    List<CirugiaCes> buscarCrucesVeterinario(@Param("veterinarioId") Long veterinarioId,
                                             @Param("fecha") LocalDate fecha,
                                             @Param("horaInicio") LocalTime horaInicio,
                                             @Param("horaFin") LocalTime horaFin,
                                             @Param("estados") List<EstadoCirugia> estados);

    // Un quirófano no puede estar en dos cirugías a la vez
    @Query("SELECT c FROM CirugiaCes c WHERE LOWER(c.quirofano) = LOWER(:quirofano) " +
            "AND c.fecha = :fecha AND c.estado IN :estados " +
            "AND c.horaInicio < :horaFin AND c.horaFin > :horaInicio")
    List<CirugiaCes> buscarCrucesQuirofano(@Param("quirofano") String quirofano,
                                           @Param("fecha") LocalDate fecha,
                                           @Param("horaInicio") LocalTime horaInicio,
                                           @Param("horaFin") LocalTime horaFin,
                                           @Param("estados") List<EstadoCirugia> estados);
}
