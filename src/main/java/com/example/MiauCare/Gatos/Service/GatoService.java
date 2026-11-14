package com.example.MiauCare.Gatos.Service;

import com.example.MiauCare.Gatos.Entity.Gato;
import com.example.MiauCare.Gatos.enums.EstadoVidaGato;

import java.util.List;

public interface GatoService {
    List<Gato> listarTodos();
    List<Gato> buscarPorNombre(String nombre);
    List<Gato> listarPorZona(Long zonaId);
    Gato obtenerPorId(Long id);
    Gato obtenerPorCodigo(String codigo);
    Gato crear(Gato gato);
    Gato actualizar(Long id, Gato datosNuevos);
    Gato cambiarEstadoVida(Long id, EstadoVidaGato nuevoEstado);
    Gato marcarEsterilizado(Long id);
    void eliminar(Long id);
    Gato asignarVeterinarioResponsable(Long gatoId, Long veterinarioId);

}
