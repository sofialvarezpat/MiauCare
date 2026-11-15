package com.example.MiauCare.Administradores.Service;

import com.example.MiauCare.Administradores.Entity.RegistroAuditoria;

import java.time.LocalDate;
import java.util.List;

public interface AuditoriaService {

    // Consulta la bitácora de auditoría aplicando filtros opcionales por tipo, usuario, texto libre o fecha
    List<RegistroAuditoria> consultar(String tipoAccion, String usuario, String buscar, LocalDate fecha);

    // Guarda un nuevo evento crítico en la tabla de auditoría (RF39)
    RegistroAuditoria registrarAccion(String tipoAccion, String descripcion, String usuarioResponsable);
}