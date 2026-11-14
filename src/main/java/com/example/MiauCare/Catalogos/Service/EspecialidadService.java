package com.example.MiauCare.Catalogos.Service;

import com.example.MiauCare.Catalogos.Entity.Especialidad;

import java.util.List;

public interface EspecialidadService {
    List<Especialidad> listarTodas();
    Especialidad obtenerPorId(Long id);
    Especialidad crear(Especialidad especialidad);
    Especialidad actualizar(Long id, Especialidad datosNuevos);
    void eliminar(Long id);
}
