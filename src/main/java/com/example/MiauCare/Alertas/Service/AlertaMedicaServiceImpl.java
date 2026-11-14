package com.example.MiauCare.Alertas.Service;

import com.example.MiauCare.Alertas.Entity.AlertaMedica;
import com.example.MiauCare.Alertas.Repository.AlertaMedicaRepository;
import com.example.MiauCare.Catalogos.enums.EstadoAlerta;
import com.example.MiauCare.Exception.SinPermisosException;
import com.example.MiauCare.Veterinarios.Entity.Veterinario;
import com.example.MiauCare.Veterinarios.Repository.VeterinarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AlertaMedicaServiceImpl implements AlertaMedicaService {

    private final AlertaMedicaRepository alertaRepository;
    private final VeterinarioRepository veterinarioRepository;

    public AlertaMedicaServiceImpl(AlertaMedicaRepository alertaRepository, VeterinarioRepository veterinarioRepository) {
        this.alertaRepository = alertaRepository;
        this.veterinarioRepository = veterinarioRepository;
    }

    @Override
    public List<AlertaMedica> listarNuevas() {
        return alertaRepository.findByEstado(EstadoAlerta.NUEVA);
    }

    @Override
    public List<AlertaMedica> listarPorVeterinario(Long veterinarioId, String filtroEstado) {
        if (filtroEstado == null) {
            return alertaRepository.findByVeterinarioResponsableIdOrDelegados(veterinarioId);
        }
        EstadoAlerta estado = EstadoAlerta.valueOf(filtroEstado);
        return alertaRepository.findByVeterinarioResponsableIdOrDelegadosAndEstado(veterinarioId, estado);
    }

    @Override
    public AlertaMedica aceptarCaso(Long alertaId, Long veterinarioId) {
        AlertaMedica alerta = obtenerPorId(alertaId);

        if (alerta.getVeterinarioResponsable() != null) {
            throw new IllegalStateException("Esta alerta ya tiene un veterinario responsable asignado.");
        }

        Veterinario veterinario = obtenerVeterinario(veterinarioId);
        alerta.setVeterinarioResponsable(veterinario);
        return alertaRepository.save(alerta);
    }

    @Override
    public AlertaMedica delegar(Long alertaId, Long veterinarioQueDelega, Long veterinarioDestinoId) {
        AlertaMedica alerta = obtenerPorId(alertaId);
        validarPermiso(alerta, veterinarioQueDelega);

        Veterinario destino = obtenerVeterinario(veterinarioDestinoId);
        alerta.getDelegados().add(destino);
        return alertaRepository.save(alerta);
    }

    @Override
    public AlertaMedica pasarAEnAtencion(Long alertaId, Long veterinarioId) {
        AlertaMedica alerta = obtenerPorId(alertaId);
        validarPermiso(alerta, veterinarioId);

        alerta.setEstado(EstadoAlerta.EN_ATENCION);
        return alertaRepository.save(alerta);
    }

    @Override
    public AlertaMedica cerrar(Long alertaId, Long veterinarioId, String resolucion, String observacion) {
        AlertaMedica alerta = obtenerPorId(alertaId);
        validarPermiso(alerta, veterinarioId);

        alerta.setEstado(EstadoAlerta.CERRADA);
        alerta.setResolucion(resolucion);
        alerta.setObservacionCierre(observacion);
        alerta.setFechaCierre(LocalDateTime.now());
        return alertaRepository.save(alerta);
    }

    private void validarPermiso(AlertaMedica alerta, Long veterinarioId) {
        boolean esResponsable = alerta.getVeterinarioResponsable() != null
                && alerta.getVeterinarioResponsable().getId().equals(veterinarioId);

        boolean esDelegado = alerta.getDelegados().stream()
                .anyMatch(v -> v.getId().equals(veterinarioId));

        if (!esResponsable && !esDelegado) {
            throw new SinPermisosException("Este paciente ya tiene un veterinario responsable asignado y no eres delegado autorizado.");
        }
    }

    private AlertaMedica obtenerPorId(Long id) {
        return alertaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alerta no encontrada con id: " + id));
    }

    private Veterinario obtenerVeterinario(Long id) {
        return veterinarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Veterinario no encontrado con id: " + id));
    }
}
