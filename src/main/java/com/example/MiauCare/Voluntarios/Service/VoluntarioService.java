package com.example.MiauCare.Voluntarios.Service;

import com.example.MiauCare.Voluntarios.Entity.Voluntario;

import java.util.List;

public interface VoluntarioService {

    // Lista todos los voluntarios registrados
    List<Voluntario> listarTodos();

    // Obtiene un voluntario por su ID
    Voluntario obtenerPorId(Long id);

    // Registra un voluntario; si envía coordenadas GPS, le asigna la colonia más cercana (RN-07)
    Voluntario registrar(Voluntario voluntario);

    // Actualiza el perfil personal del voluntario (RF-01)
    Voluntario actualizar(Long id, Voluntario datosNuevos);

    // Activa o inactiva al voluntario desde "Gestión de usuarios" (Admin)
    void cambiarEstado(Long id, boolean activo);

    // RF-02 y RN-07: Empareja automáticamente al voluntario con la colonia más cercana a su GPS
    Voluntario emparejarPorGps(Long voluntarioId, Double latitud, Double longitud);

    // Permite al voluntario cambiar su zona manualmente desde la pantalla "Ubicar Zona"
    Voluntario asignarZonaManual(Long voluntarioId, Long zonaId);
}