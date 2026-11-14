package com.example.MiauCare.Catalogos.Service;

import com.example.MiauCare.Catalogos.Entity.Zona;
import com.example.MiauCare.DTO.ZonaResumenDto;

import java.util.List;

public interface ZonaService {

    // Lista todas las colonias/zonas registradas
    List<Zona> listarTodas();

    // Devuelve las colonias con la cantidad de gatos censados en cada una (Mockup "Colonias y zonas")
    List<ZonaResumenDto> listarConConteoGatos();

    // RF-03: Calcula y devuelve una ruta optimizada de alimentación ordenada por cercanía GPS
    List<ZonaResumenDto> obtenerRutaAlimentacion(Double latitudOrigen, Double longitudOrigen, String zonaMacro);

    // Obtiene una zona por su ID
    Zona obtenerPorId(Long id);

    // Crea una nueva colonia/punto de alimentación
    Zona crear(Zona zona);

    // Actualiza los datos de una colonia/punto de alimentación
    Zona actualizar(Long id, Zona datosNuevos);

    // Elimina una colonia
    void eliminar(Long id);
}