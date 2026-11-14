package com.example.MiauCare.Alertas.Service;

import com.example.MiauCare.Alertas.Entity.AlertaMedica;
import com.example.MiauCare.Alertas.Repository.AlertaMedicaRepository;
import com.example.MiauCare.Catalogos.enums.EstadoAlerta;
import com.example.MiauCare.Gatos.Entity.Avistamiento;
import com.example.MiauCare.Gatos.Entity.Gato;
import com.example.MiauCare.Gatos.Repository.AvistamientoRepository;
import com.example.MiauCare.Gatos.Repository.GatoRepository;
import com.example.MiauCare.Gatos.enums.EstadoAparente;
import com.example.MiauCare.Gatos.enums.EstadoInternacion;
import com.example.MiauCare.Gatos.enums.EstadoVidaGato;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MotorAlertasServiceImpl implements MotorAlertasService {

    private static final int DIAS_SIN_AVISTAMIENTO = 7; // RN04 - parámetro X (fijo por ahora)
    private static final int REPORTES_CRITICOS_CONSECUTIVOS = 2; // RN03 - parámetro N (fijo por ahora)

    private final AvistamientoRepository avistamientoRepository;
    private final AlertaMedicaRepository alertaRepository;
    private final GatoRepository gatoRepository;

    public MotorAlertasServiceImpl(AvistamientoRepository avistamientoRepository,
                                   AlertaMedicaRepository alertaRepository,
                                   GatoRepository gatoRepository) {
        this.avistamientoRepository = avistamientoRepository;
        this.alertaRepository = alertaRepository;
        this.gatoRepository = gatoRepository;
    }

    @Override
    public void evaluarAvistamientoCritico(Long gatoId) {
        List<Avistamiento> ultimos = avistamientoRepository.findTop2ByGatoIdOrderByFechaDesc(gatoId);

        if (ultimos.size() < REPORTES_CRITICOS_CONSECUTIVOS) return;

        boolean todosCriticos = ultimos.stream().allMatch(a ->
                a.getEstadoAparente() == EstadoAparente.ENFERMO ||
                        a.getEstadoAparente() == EstadoAparente.HERIDO
        );

        if (todosCriticos && !existeAlertaActiva(gatoId)) {
            crearAlerta(gatoId, "Reportes críticos consecutivos (RN03)");
        }
    }

    @Override
    public void evaluarAusenciaProlongada() {
        List<Gato> gatos = gatoRepository.findByEstadoVida(EstadoVidaGato.ACTIVO);

        for (Gato gato : gatos) {
            if (gato.getEstadoInternacion() == EstadoInternacion.HOSPITALIZADO) continue; // RN05

            List<Avistamiento> historial = avistamientoRepository.findByGatoIdOrderByFechaDesc(gato.getId());

            LocalDateTime ultimaFecha = historial.isEmpty() ? null : historial.get(0).getFecha();

            boolean sinReportesRecientes = ultimaFecha == null ||
                    ultimaFecha.isBefore(LocalDateTime.now().minusDays(DIAS_SIN_AVISTAMIENTO));

            if (sinReportesRecientes && !existeAlertaActiva(gato.getId())) {
                crearAlerta(gato.getId(), "Ausencia prolongada sin avistamiento (RN04)");
            }
        }
    }

    private boolean existeAlertaActiva(Long gatoId) {
        return !alertaRepository.findByGatoIdAndEstadoNot(gatoId, EstadoAlerta.CERRADA).isEmpty();
    }

    private void crearAlerta(Long gatoId, String motivo) {
        Gato gato = gatoRepository.findById(gatoId)
                .orElseThrow(() -> new RuntimeException("Gato no encontrado con id: " + gatoId));

        AlertaMedica alerta = new AlertaMedica();
        alerta.setGato(gato);
        alerta.setMotivo(motivo);
        alerta.setEstado(EstadoAlerta.NUEVA);
        alerta.setFechaCreacion(LocalDateTime.now());

        alertaRepository.save(alerta);
    }
}
