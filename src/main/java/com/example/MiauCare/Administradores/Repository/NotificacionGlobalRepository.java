package com.example.MiauCare.Administradores.Repository;

import com.example.MiauCare.Administradores.Entity.NotificacionGlobal;
import com.example.MiauCare.Administradores.enums.DestinatarioNotificacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificacionGlobalRepository extends JpaRepository<NotificacionGlobal, Long> {

    // Lista todas las notificaciones de la más reciente a la más antigua (vista Admin)
    List<NotificacionGlobal> findAllByOrderByFechaCreacionDesc();

    // Filtra las notificaciones que coincidan con el rol del usuario o que sean para AMBOS
    List<NotificacionGlobal> findByDestinatariosInOrderByFechaCreacionDesc(List<DestinatarioNotificacion> destinatarios);
}
