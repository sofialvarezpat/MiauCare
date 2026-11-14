package com.example.MiauCare.Veterinarios.Service;

import com.example.MiauCare.Veterinarios.Entity.Veterinario;

import java.util.List;

public interface VeterinarioService {
    List<Veterinario> listarTodos();
    List<Veterinario> listarPendientes();
    Veterinario obtenerPorId(Long id);
    Veterinario enviarSolicitud(Veterinario veterinario);
    Veterinario aprobar(Long id);
    Veterinario rechazar(Long id);
    void cambiarEstado(Long id, boolean activo);
    Veterinario actualizar(Long id, Veterinario datosNuevos);
    void cambiarDisponibilidad(Long id, boolean disponible);
}