package com.example.MiauCare.Gatos.Repository;

import com.example.MiauCare.Gatos.Entity.Gato;
import com.example.MiauCare.Gatos.enums.EstadoVidaGato;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GatoRepository extends JpaRepository<Gato, Long> {
    Optional<Gato> findByCodigo(String codigo);
    List<Gato> findByEstadoVida(EstadoVidaGato estadoVida);
    List<Gato> findByZonaId(Long zonaId);
    List<Gato> findByNombreContainingIgnoreCase(String nombre);
}
