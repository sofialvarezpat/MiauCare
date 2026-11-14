package com.example.MiauCare.Alertas.Service;

import com.example.MiauCare.Alertas.Entity.AlertaMedica;
import com.example.MiauCare.Alertas.Repository.AlertaMedicaRepository;
import com.example.MiauCare.Catalogos.enums.EstadoAlerta;
import com.example.MiauCare.Exception.SinPermisosException;
import com.example.MiauCare.Gatos.Entity.Gato;
import com.example.MiauCare.Gatos.Repository.GatoRepository;
import com.example.MiauCare.Veterinarios.Entity.Veterinario;
import com.example.MiauCare.Veterinarios.Repository.VeterinarioRepository;
import com.example.MiauCare.Veterinarios.enums.EstadoVeterinario;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AlertaMedicaServiceImpl implements AlertaMedicaService {

    private final AlertaMedicaRepository alertaRepository;
    private final VeterinarioRepository veterinarioRepository;
    // Repositorio de gatos para vincular al veterinario como responsable del paciente (RN-02)
    private final GatoRepository gatoRepository;

    // Inyección por constructor de los 3 repositorios necesarios
    public AlertaMedicaServiceImpl(AlertaMedicaRepository alertaRepository,
                                   VeterinarioRepository veterinarioRepository,
                                   GatoRepository gatoRepository) {
        this.alertaRepository = alertaRepository;
        this.veterinarioRepository = veterinarioRepository;
        this.gatoRepository = gatoRepository;
    }

    // Lista todas las alertas en estado NUEVA para el carrusel de urgencias del Dashboard
    @Override
    public List<AlertaMedica> listarNuevas() {
        return alertaRepository.findByEstado(EstadoAlerta.NUEVA);
    }

    // Lista las alertas donde el veterinario es responsable directo o fue agregado como delegado
    @Override
    public List<AlertaMedica> listarPorVeterinario(Long veterinarioId, String filtroEstado) {
        if (filtroEstado == null) {
            return alertaRepository.findByVeterinarioResponsableIdOrDelegados(veterinarioId);
        }
        EstadoAlerta estado = EstadoAlerta.valueOf(filtroEstado);
        return alertaRepository.findByVeterinarioResponsableIdOrDelegadosAndEstado(veterinarioId, estado);
    }

    // RN-02: Aceptar una alerta médica vincula al veterinario tanto con la alerta como con la ficha del gato
    @Override
    public AlertaMedica aceptarCaso(Long alertaId, Long veterinarioId) {
        AlertaMedica alerta = obtenerPorId(alertaId);

        // Evita que dos veterinarios tomen la misma alerta al mismo tiempo
        if (alerta.getVeterinarioResponsable() != null) {
            throw new IllegalStateException("Esta alerta ya tiene un veterinario responsable asignado.");
        }

        Veterinario veterinario = obtenerVeterinario(veterinarioId);

        // Verifica que el veterinario esté activo y tenga encendido el interruptor "Disponible para Emergencias"
        validarVeterinarioDisponible(veterinario);

        // 1. Asigna el veterinario a la alerta
        alerta.setVeterinarioResponsable(veterinario);

        // 2. RN-02: Si la alerta pertenece a un gato ya censado, vincula al veterinario como su responsable en la ficha
        Gato gato = alerta.getGato();
        if (gato != null) {
            // Si el gato ya tenía otro veterinario responsable distinto, bloquea con 403
            if (gato.getVeterinarioResponsable() != null
                    && !gato.getVeterinarioResponsable().getId().equals(veterinario.getId())) {
                throw new SinPermisosException(
                        "Este paciente ya tiene un veterinario responsable asignado y no eres delegado autorizado.");
            }
            gato.setVeterinarioResponsable(veterinario);
            gatoRepository.save(gato);
        }

        return alertaRepository.save(alerta);
    }

    // Permite al veterinario responsable compartir permisos de atención con otro colega del directorio
    @Override
    public AlertaMedica delegar(Long alertaId, Long veterinarioQueDelega, Long veterinarioDestinoId) {
        AlertaMedica alerta = obtenerPorId(alertaId);
        // Verifica que quien delega sea realmente el responsable o un delegado autorizado de esta alerta
        validarPermiso(alerta, veterinarioQueDelega);

        Veterinario destino = obtenerVeterinario(veterinarioDestinoId);
        // Verifica que el colega destino esté activo y disponible para emergencias
        validarVeterinarioDisponible(destino);

        alerta.getDelegados().add(destino);
        return alertaRepository.save(alerta);
    }

    // Cambia el estado de la alerta de NUEVA a EN_ATENCION cuando el veterinario inicia la revisión
    @Override
    public AlertaMedica pasarAEnAtencion(Long alertaId, Long veterinarioId) {
        AlertaMedica alerta = obtenerPorId(alertaId);
        validarPermiso(alerta, veterinarioId);

        alerta.setEstado(EstadoAlerta.EN_ATENCION);
        return alertaRepository.save(alerta);
    }

    // Cierra la alerta médica guardando la resolución, observación final y fecha/hora de cierre (RN-22, RN-23)
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

    // Valida que el veterinario sea el responsable del caso o esté en la lista de delegados; si no, lanza 403
    private void validarPermiso(AlertaMedica alerta, Long veterinarioId) {
        boolean esResponsable = alerta.getVeterinarioResponsable() != null
                && alerta.getVeterinarioResponsable().getId().equals(veterinarioId);

        boolean esDelegado = alerta.getDelegados().stream()
                .anyMatch(v -> v.getId().equals(veterinarioId));

        if (!esResponsable && !esDelegado) {
            throw new SinPermisosException("Este paciente ya tiene un veterinario responsable asignado y no eres delegado autorizado.");
        }
    }

    // Verifica que el veterinario tenga cuenta ACTIVA y su interruptor de disponibilidad encendido
    private void validarVeterinarioDisponible(Veterinario veterinario) {
        if (veterinario.getEstado() != EstadoVeterinario.ACTIVO) {
            throw new IllegalStateException("El veterinario no tiene una cuenta activa.");
        }
        if (Boolean.FALSE.equals(veterinario.getDisponibleEmergencias())) {
            throw new IllegalStateException("El veterinario tiene desactivada su disponibilidad para nuevas alertas médicas.");
        }
    }

    // Busca una alerta por su ID o lanza 404 si no existe
    private AlertaMedica obtenerPorId(Long id) {
        return alertaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alerta no encontrada con id: " + id));
    }

    // Busca un veterinario por su ID o lanza 404 si no existe
    private Veterinario obtenerVeterinario(Long id) {
        return veterinarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Veterinario no encontrado con id: " + id));
    }
}
