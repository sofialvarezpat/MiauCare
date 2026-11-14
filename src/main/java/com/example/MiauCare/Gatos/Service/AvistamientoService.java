package com.example.MiauCare.Gatos.Service;

import com.example.MiauCare.Gatos.Entity.Avistamiento;

import java.util.List;

public interface AvistamientoService {
    List<Avistamiento> listarPorGato(Long gatoId);
    List<Avistamiento> listarPorZona(Long zonaId);
    Avistamiento registrarAvistamientoNormal(Avistamiento avistamiento);
    Avistamiento registrarGatoNoCensado(Avistamiento avistamiento);
}
