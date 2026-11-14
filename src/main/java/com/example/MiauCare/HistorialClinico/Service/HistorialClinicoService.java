package com.example.MiauCare.HistorialClinico.Service;

import com.example.MiauCare.HistorialClinico.Entity.HistorialClinico;

import java.util.List;

public interface HistorialClinicoService {
    List<HistorialClinico> listarPorGato(Long gatoId);
    HistorialClinico registrar(HistorialClinico entrada);
}
