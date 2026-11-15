package com.example.MiauCare.Administradores.Service;

import com.example.MiauCare.Administradores.Entity.NotificacionGlobal;
import com.example.MiauCare.Administradores.Repository.NotificacionGlobalRepository;
import com.example.MiauCare.Administradores.enums.DestinatarioNotificacion;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
public class NotificacionServiceImpl implements NotificacionService {

    // Zona horaria de Colombia para que las fechas coincidan en Render (que corre en UTC)
    private static final ZoneId ZONA = ZoneId.of("America/Bogota");

    private final NotificacionGlobalRepository notificacionRepository;

    public NotificacionServiceImpl(NotificacionGlobalRepository notificacionRepository) {
        this.notificacionRepository = notificacionRepository;
    }

    @Override
    public List<NotificacionGlobal> listarTodas() {
        return notificacionRepository.findAllByOrderByFechaCreacionDesc();
    }

    // Filtra la bandeja según quién consulta: Veterinario ve VETERINARIOS + AMBOS; Voluntario ve VOLUNTARIOS + AMBOS
    @Override
    public List<NotificacionGlobal> listarParaRol(String rolUsuario) {
        if ("ROLE_VETERINARIO".equals(rolUsuario)) {
            return notificacionRepository.findByDestinatariosInOrderByFechaCreacionDesc(
                    List.of(DestinatarioNotificacion.VETERINARIOS, DestinatarioNotificacion.AMBOS));
        }
        if ("ROLE_VOLUNTARIO".equals(rolUsuario)) {
            return notificacionRepository.findByDestinatariosInOrderByFechaCreacionDesc(
                    List.of(DestinatarioNotificacion.VOLUNTARIOS, DestinatarioNotificacion.AMBOS));
        }
        // Si consulta un Administrador, ve todas
        return notificacionRepository.findAllByOrderByFechaCreacionDesc();
    }

    // Valida los campos del formulario "Notificación global" y registra el anuncio
    @Override
    public NotificacionGlobal crear(NotificacionGlobal notificacion, String correoAdmin) {
        if (notificacion.getTitulo() == null || notificacion.getTitulo().isBlank()) {
            throw new IllegalArgumentException("El título de la notificación es obligatorio.");
        }
        if (notificacion.getMensaje() == null || notificacion.getMensaje().isBlank()) {
            throw new IllegalArgumentException("El mensaje de la notificación es obligatorio.");
        }
        if (notificacion.getDestinatarios() == null) {
            throw new IllegalArgumentException("Debes seleccionar los destinatarios (VETERINARIOS, VOLUNTARIOS o AMBOS).");
        }

        notificacion.setId(null);
        notificacion.setTitulo(notificacion.getTitulo().trim());
        notificacion.setMensaje(notificacion.getMensaje().trim());
        notificacion.setCreadoPor(correoAdmin);
        notificacion.setFechaCreacion(LocalDateTime.now(ZONA));

        return notificacionRepository.save(notificacion);
    }

    @Override
    public void eliminar(Long id) {
        if (!notificacionRepository.existsById(id)) {
            throw new RuntimeException("Notificación no encontrada con id: " + id);
        }
        notificacionRepository.deleteById(id);
    }
}