package com.example.MiauCare.Administradores.Repository;

import com.example.MiauCare.Administradores.Entity.RegistroAuditoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegistroAuditoriaRepository extends JpaRepository<RegistroAuditoria, Long> {

    // Lista todos los eventos de auditoría del más reciente al más antiguo
    List<RegistroAuditoria> findAllByOrderByFechaHoraDesc();
}