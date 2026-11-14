package com.example.MiauCare.Catalogos.Service;

import com.example.MiauCare.Catalogos.Entity.Zona;

import java.util.List;

public interface ZonaService {
    List<Zona> listarTodas();
    Zona obtenerPorId(Long id);
    Zona crear(Zona zona);
    Zona actualizar(Long id, Zona datosNuevos);
    void eliminar(Long id);
}