package com.example.MiauCare.Catalogos.Service;

import com.example.MiauCare.Catalogos.Entity.DiagnosticoCatalogo;

import java.util.List;

public interface DiagnosticoCatalogoService {
    List<DiagnosticoCatalogo> listarTodos();
    DiagnosticoCatalogo obtenerPorId(Long id);
    DiagnosticoCatalogo crear(DiagnosticoCatalogo diagnostico);
    DiagnosticoCatalogo actualizar(Long id, DiagnosticoCatalogo datosNuevos);
    void eliminar(Long id);
}