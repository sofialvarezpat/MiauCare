package com.example.MiauCare.HistorialClinico.Service;

import com.example.MiauCare.Gatos.enums.EstadoVidaGato;
import com.example.MiauCare.HistorialClinico.Entity.HistorialClinico;
import com.example.MiauCare.HistorialClinico.Repository.HistorialClinicoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HistorialClinicoServiceImpl implements HistorialClinicoService {

    private final HistorialClinicoRepository historialRepository;

    public HistorialClinicoServiceImpl(HistorialClinicoRepository historialRepository) {
        this.historialRepository = historialRepository;
    }

    @Override
    public List<HistorialClinico> listarPorGato(Long gatoId) {
        return historialRepository.findByGatoIdOrderByFechaDesc(gatoId);
    }

    @Override
    public HistorialClinico registrar(HistorialClinico entrada) {
        if (entrada.getGato().getEstadoVida() == EstadoVidaGato.FALLECIDO) {
            throw new IllegalStateException("No se puede registrar atención clínica a un gato fallecido.");
        }
        return historialRepository.save(entrada);
    }
}