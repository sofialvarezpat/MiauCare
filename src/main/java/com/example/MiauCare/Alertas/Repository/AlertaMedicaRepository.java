package com.example.MiauCare.Alertas.Repository;

import com.example.MiauCare.Alertas.Entity.AlertaMedica;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlertaMedicaRepository extends JpaRepository<AlertaMedica, Long> {
    List<AlertaMedica> findByGatoIdAndEstadoNot(Long gatoId, com.example.MiauCare.Catalogos.enums.EstadoAlerta estadoExcluido);
}