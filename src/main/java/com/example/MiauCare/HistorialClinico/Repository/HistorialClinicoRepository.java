package com.example.MiauCare.HistorialClinico.Repository;

import com.example.MiauCare.HistorialClinico.Entity.HistorialClinico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistorialClinicoRepository extends JpaRepository<HistorialClinico, Long> {
    List<HistorialClinico> findByGatoIdOrderByFechaDesc(Long gatoId);
}
