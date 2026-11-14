package com.example.MiauCare.Gatos.Repository;

import com.example.MiauCare.Gatos.Entity.SolicitudGato;
import com.example.MiauCare.Gatos.enums.EstadoSolicitud;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SolicitudGatoRepository extends JpaRepository<SolicitudGato, Long> {
    List<SolicitudGato> findByEstado(EstadoSolicitud estado);
}