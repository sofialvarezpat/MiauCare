package com.example.MiauCare.Alertas.Repository;

import com.example.MiauCare.Alertas.Entity.AlertaMedica;
import com.example.MiauCare.Catalogos.enums.EstadoAlerta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AlertaMedicaRepository extends JpaRepository<AlertaMedica, Long> {

    List<AlertaMedica> findByEstado(EstadoAlerta estado);

    List<AlertaMedica> findByGatoIdAndEstadoNot(Long gatoId, EstadoAlerta estadoExcluido);

    @Query("SELECT a FROM AlertaMedica a WHERE a.veterinarioResponsable.id = :vetId OR :vetId IN (SELECT d.id FROM a.delegados d)")
    List<AlertaMedica> findByVeterinarioResponsableIdOrDelegados(@Param("vetId") Long vetId);

    @Query("SELECT a FROM AlertaMedica a WHERE (a.veterinarioResponsable.id = :vetId OR :vetId IN (SELECT d.id FROM a.delegados d)) AND a.estado = :estado")
    List<AlertaMedica> findByVeterinarioResponsableIdOrDelegadosAndEstado(@Param("vetId") Long vetId, @Param("estado") EstadoAlerta estado);
}