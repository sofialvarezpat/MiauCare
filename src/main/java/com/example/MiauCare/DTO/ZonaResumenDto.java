package com.example.MiauCare.DTO;

import java.time.LocalTime;

public record ZonaResumenDto(Long id,
                             String nombre,
                             String zonaMacro,
                             String puntoAlimentacion,
                             LocalTime horaAlimentacion,
                             Double latitud,
                             Double longitud,
                             int cantidadGatos,
                             Integer ordenParada,
                             Double distanciaDesdePuntoAnteriorKm) {
}
