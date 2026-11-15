package com.example.MiauCare.Administradores.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "parametros_sistema")
@Getter
@Setter
public class ParametrosSistema {

    // Usamos siempre el ID fijo 1 porque solo existe una configuración global en todo el sistema
    @Id
    private Long id = 1L;

    // Parámetro X (RN-04): Días consecutivos sin avistamiento para disparar alerta de búsqueda/rescate
    @Column(nullable = false)
    private Integer diasAusenciaAlerta = 7;

    // Parámetro N (RN-03): Cantidad de avistamientos críticos consecutivos (Enfermo/Herido) para disparar alerta médica
    @Column(nullable = false)
    private Integer reportesCriticosConsecutivos = 2;

    // Parámetro T: Tiempo máximo de respuesta en horas para atender una alerta activa
    @Column(nullable = false)
    private Integer tiempoMaximoRespuestaHoras = 24;

    // Fecha y hora de la última vez que el Administrador modificó los parámetros
    private LocalDateTime ultimaActualizacion;

    // Correo del administrador que guardó el último cambio
    private String actualizadoPor;
}