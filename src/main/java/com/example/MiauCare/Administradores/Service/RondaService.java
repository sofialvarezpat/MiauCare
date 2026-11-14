package com.example.MiauCare.Administradores.Service;

import com.example.MiauCare.Administradores.Entity.Ronda;

import java.time.LocalDate;
import java.util.List;

public interface RondaService {
    List<Ronda> listarTodas();
    List<Ronda> listarPorFecha(LocalDate fecha);
    Ronda obtenerPorId(Long id);
    Ronda crear(Ronda ronda);
    Ronda editar(Long id, Ronda datosNuevos);
    void cancelar(Long id);
    Ronda iniciar(Long id);
    Ronda finalizar(Long id);
    Ronda asistir(Long rondaId, String correoVoluntario);
}