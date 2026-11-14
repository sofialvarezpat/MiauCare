package com.example.MiauCare.Alertas.Entity;

import com.example.MiauCare.Catalogos.enums.EstadoAlerta;
import com.example.MiauCare.Gatos.Entity.Gato;
import com.example.MiauCare.Veterinarios.Entity.Veterinario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "alertas_medicas")
@Getter
@Setter
public class AlertaMedica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "gato_id", nullable = false)
    private Gato gato;

    @Column(nullable = false)
    private String motivo; // Ej: "Reportes críticos consecutivos" o "Ausencia prolongada"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoAlerta estado;

    @ManyToOne
    @JoinColumn(name = "veterinario_responsable_id")
    private Veterinario veterinarioResponsable; // null hasta que alguien la acepte

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaCierre;

    @Column(columnDefinition = "TEXT")
    private String observacionCierre;
}
