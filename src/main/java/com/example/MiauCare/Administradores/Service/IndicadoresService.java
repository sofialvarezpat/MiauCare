package com.example.MiauCare.Administradores.Service;

import com.example.MiauCare.DTO.IndicadorZonaDto;

import java.util.List;

public interface IndicadoresService {

    // Devuelve los indicadores de todas las zonas (o de una sola si se envía zonaId)
    List<IndicadorZonaDto> consultarIndicadores(Long zonaId);

    // Genera el contenido en texto formato CSV listo para descargar
    String generarCsv(Long zonaId);
}