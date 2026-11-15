package com.example.MiauCare.DTO;

// DTO para alimentar las tarjetas de estadísticas rápidas en los Dashboards de inicio
public record DashboardResumenDto(
        long urgenciasHoy,
        long casosActivos,
        long pacientesInternados,
        long gatosCensadosTotales,
        long veterinariosActivos,
        long solicitudesPendientes
) {
}
