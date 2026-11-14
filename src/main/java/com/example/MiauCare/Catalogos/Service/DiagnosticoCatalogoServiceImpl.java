package com.example.MiauCare.Catalogos.Service;

import com.example.MiauCare.Catalogos.Entity.DiagnosticoCatalogo;
import com.example.MiauCare.Catalogos.Repository.DiagnosticoCatalogoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiagnosticoCatalogoServiceImpl implements DiagnosticoCatalogoService {

    private final DiagnosticoCatalogoRepository diagnosticoRepository;

    public DiagnosticoCatalogoServiceImpl(DiagnosticoCatalogoRepository diagnosticoRepository) {
        this.diagnosticoRepository = diagnosticoRepository;
    }


    @Override
    public List<DiagnosticoCatalogo> listarTodos() {
        return diagnosticoRepository.findAll();
    }

    @Override
    public DiagnosticoCatalogo obtenerPorId(Long id) {
        return diagnosticoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Diagnóstico no encontrado con id: " + id));
    }

    @Override
    public DiagnosticoCatalogo crear(DiagnosticoCatalogo diagnostico) {
        return diagnosticoRepository.save(diagnostico);
    }

    @Override
    public DiagnosticoCatalogo actualizar(Long id, DiagnosticoCatalogo datosNuevos) {
        DiagnosticoCatalogo existente = obtenerPorId(id);
        existente.setNombre(datosNuevos.getNombre());
        return diagnosticoRepository.save(existente);
    }

    @Override
    public void eliminar(Long id) {
        diagnosticoRepository.deleteById(id);
    }
}
