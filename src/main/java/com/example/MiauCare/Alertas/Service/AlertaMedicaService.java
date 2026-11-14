package com.example.MiauCare.Alertas.Service;

import com.example.MiauCare.Alertas.Entity.AlertaMedica;

import java.util.List;

public interface AlertaMedicaService {
    List<AlertaMedica> listarNuevas();
    List<AlertaMedica> listarPorVeterinario(Long veterinarioId, String filtroEstado);
    AlertaMedica aceptarCaso(Long alertaId, Long veterinarioId);
    AlertaMedica delegar(Long alertaId, Long veterinarioQueDelega, Long veterinarioDestino);
    AlertaMedica pasarAEnAtencion(Long alertaId, Long veterinarioId);
    AlertaMedica cerrar(Long alertaId, Long veterinarioId, String resolucion, String observacion);
}