package com.example.MiauCare.Veterinarios.Service;

import com.example.MiauCare.Veterinarios.Entity.Veterinario;

import java.util.List;

public interface VeterinarioService {

    // Lista todos los veterinarios registrados
    List<Veterinario> listarTodos();

    // Lista solicitudes de registro de veterinarios en estado PENDIENTE
    List<Veterinario> listarPendientes();

    // NUEVO: Lista solo los veterinarios activos y disponibles para emergencias/delegaciones
    List<Veterinario> listarDisponibles();

    // NUEVO: Lista veterinarios activos filtrados por especialidad para el Directorio Médico
    List<Veterinario> listarPorEspecialidad(Long especialidadId);

    // Obtiene un veterinario por su ID
    Veterinario obtenerPorId(Long id);

    // Registra la solicitud inicial de un veterinario (queda en estado PENDIENTE)
    Veterinario enviarSolicitud(Veterinario veterinario);

    // Admin aprueba la solicitud y activa la cuenta del veterinario
    Veterinario aprobar(Long id);

    // Admin rechaza la solicitud del veterinario
    Veterinario rechazar(Long id);

    // Admin activa o inactiva la cuenta de un veterinario desde "Gestión de usuarios"
    void cambiarEstado(Long id, boolean activo);

    // Actualiza datos del perfil del veterinario
    Veterinario actualizar(Long id, Veterinario datosNuevos);

    // Enciende o apaga el interruptor maestro "Disponible para Emergencias"
    void cambiarDisponibilidad(Long id, boolean disponible);
}