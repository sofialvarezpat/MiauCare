package com.example.MiauCare.HistorialClinico.Service;

import com.example.MiauCare.Gatos.Entity.Gato;
import com.example.MiauCare.Gatos.Repository.GatoRepository;
import com.example.MiauCare.Gatos.enums.EstadoInternacion;
import com.example.MiauCare.Gatos.enums.EstadoVidaGato;
import com.example.MiauCare.HistorialClinico.Entity.HistorialClinico;
import com.example.MiauCare.HistorialClinico.Repository.HistorialClinicoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
public class HistorialClinicoServiceImpl implements HistorialClinicoService {

    private static final ZoneId ZONA = ZoneId.of("America/Bogota");

    private final HistorialClinicoRepository historialRepository;
    private final GatoRepository gatoRepository;

    public HistorialClinicoServiceImpl(HistorialClinicoRepository historialRepository,
                                       GatoRepository gatoRepository) {
        this.historialRepository = historialRepository;
        this.gatoRepository = gatoRepository;
    }

    @Override
    public List<HistorialClinico> listarPorGato(Long gatoId) {
        return historialRepository.findByGatoIdOrderByFechaDesc(gatoId);
    }

    // Registra la atención médica y actualiza peso y estado de internación del gato (RF-05, RF-12)
    @Override
    public HistorialClinico registrar(HistorialClinico entrada) {
        if (entrada.getGato() == null || entrada.getGato().getId() == null) {
            throw new IllegalArgumentException("Debes indicar el gato al que pertenece la atención clínica.");
        }

        Gato gatoReal = gatoRepository.findById(entrada.getGato().getId())
                .orElseThrow(() -> new RuntimeException("Gato no encontrado con id: " + entrada.getGato().getId()));

        if (gatoReal.getEstadoVida() == EstadoVidaGato.FALLECIDO) {
            throw new IllegalStateException("No se puede registrar atención clínica a un gato fallecido.");
        }

        // Si en la atención se registró el peso actual del gato, actualiza también su ficha maestra
        if (entrada.getPesoRegistrado() != null && entrada.getPesoRegistrado() > 0) {
            gatoReal.setPeso(entrada.getPesoRegistrado());
        }

        // RF-12: Actualiza el estado de internación según el interruptor "Dar de alta / Dejar hospitalizado"
        if (entrada.getDejarHospitalizado() != null) {
            gatoReal.setEstadoInternacion(
                    entrada.getDejarHospitalizado() ? EstadoInternacion.HOSPITALIZADO : EstadoInternacion.EN_CALLE);
        }

        gatoRepository.save(gatoReal);

        entrada.setGato(gatoReal);
        if (entrada.getFecha() == null) {
            entrada.setFecha(LocalDate.now(ZONA));
        }

        return historialRepository.save(entrada);
    }
}