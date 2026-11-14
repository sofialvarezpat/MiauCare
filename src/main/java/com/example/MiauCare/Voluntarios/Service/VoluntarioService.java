package com.example.MiauCare.Voluntarios.Service;

import com.example.MiauCare.Voluntarios.Entity.Voluntario;

import java.util.List;

public interface VoluntarioService {
    List<Voluntario> listarTodos();
    Voluntario obtenerPorId(Long id);
    Voluntario registrar(Voluntario voluntario);
    Voluntario actualizar(Long id, Voluntario datosNuevos);
    void cambiarEstado(Long id, boolean activo);
}