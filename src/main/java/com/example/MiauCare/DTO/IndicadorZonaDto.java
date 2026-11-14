package com.example.MiauCare.DTO;

// DTO que agrupa las métricas clave de una colonia/zona para el panel del Administrador
public record IndicadorZonaDto(
        Long zonaId,
        String nombreZona,
        String zonaMacro,
        int gatosCensados,
        int gatosEsterilizados,
        int gatosHospitalizados,
        int alertasActivas,
        int rondasCompletadas
) {
}