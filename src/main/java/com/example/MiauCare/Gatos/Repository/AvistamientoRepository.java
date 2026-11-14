package com.example.MiauCare.Gatos.Repository;

import com.example.MiauCare.Gatos.Entity.Avistamiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AvistamientoRepository extends JpaRepository<Avistamiento, Long> {
    List<Avistamiento> findByGatoIdOrderByFechaDesc(Long gatoId);
    List<Avistamiento> findByZonaIdOrderByFechaDesc(Long zonaId);
    List<Avistamiento> findTop2ByGatoIdOrderByFechaDesc(Long gatoId); // para el motor de alertas (RN03)
}