package com.example.MiauCare.Administradores.Entity;

import com.example.MiauCare.Administradores.enums.DestinatarioNotificacion;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "notificaciones_globales")
@Getter
@Setter
public class NotificacionGlobal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Título corto del aviso (ej: "Jornada de esterilización este sábado")
    @Column(nullable = false)
    private String titulo;

    // Cuerpo completo del anuncio
    @Column(nullable = false, columnDefinition = "TEXT")
    private String mensaje;

    // A qué grupo va dirigido: VETERINARIOS, VOLUNTARIOS o AMBOS
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DestinatarioNotificacion destinatarios;

    // Correo del administrador que publicó el anuncio
    @Column(nullable = false)
    private String creadoPor;

    // Fecha y hora exacta de creación para ordenar la bandeja (TODAY / YESTERDAY)
    @Column(nullable = false)
    private LocalDateTime fechaCreacion;
}