package com.example.MiauCare.Administradores.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "auditoria")
@Getter
@Setter
public class RegistroAuditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Tipo o categoría de la acción crítica (ej: "ELIMINAR_GATO", "CREAR_RONDA", "APROBAR_VETERINARIO", "PARAMETROS")
    @Column(nullable = false)
    private String tipoAccion;

    // Descripción legible para la tarjeta del mockup (ej: "admin@miaucare.com eliminó a Manchas del censo")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    // Correo o nombre del usuario que ejecutó la acción
    @Column(nullable = false)
    private String usuarioResponsable;

    // Fecha y hora exacta en que ocurrió la acción
    @Column(nullable = false)
    private LocalDateTime fechaHora;
}