package com.example.MiauCare.Administradores.Service;

import com.example.MiauCare.Administradores.Entity.NotificacionGlobal;

import java.util.List;

public interface NotificacionService {

    // Lista el historial completo de anuncios enviados (para el Administrador)
    List<NotificacionGlobal> listarTodas();

    // Devuelve la bandeja de anuncios que le corresponden al usuario según su rol
    List<NotificacionGlobal> listarParaRol(String rolUsuario);

    // Crea y publica un nuevo anuncio global
    NotificacionGlobal crear(NotificacionGlobal notificacion, String correoAdmin);

    // Elimina un anuncio global por su ID
    void eliminar(Long id);
}