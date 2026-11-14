package com.example.MiauCare.Administradores.Service;

import com.example.MiauCare.Administradores.Entity.Administrador;

import java.util.List;

public interface AdministradorService {
    List<Administrador> listarTodos();
    Administrador obtenerPorId(Long id);
    Administrador crear(Administrador administrador, String contrasenaTemporal);
    Administrador actualizarPerfil(Long id, Administrador datosNuevos);
    void cambiarContrasena(Long id, String nuevaContrasena);
    void cambiarEstado(Long id, boolean activo);
}
