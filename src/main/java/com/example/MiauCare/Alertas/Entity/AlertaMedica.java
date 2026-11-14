package com.example.MiauCare.Alertas.Entity;

import com.example.MiauCare.Catalogos.enums.EstadoAlerta;
import com.example.MiauCare.Gatos.Entity.Gato;
import com.example.MiauCare.Veterinarios.Entity.Veterinario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private String motivo;

    @Column(nullable = false)
    private String zonaDescripcion; // texto libre, ej: "Colonia Centro" (para mostrar "Reportó Ana, Colonia Centro")

    @Column(nullable = false)
    private String reportadoPor; // nombre del voluntario que generó el avistamiento origen

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoAlerta estado;

    @ManyToOne
    @JoinColumn(name = "veterinario_responsable_id")
    private Veterinario veterinarioResponsable;

    @ManyToMany
    @JoinTable(
            name = "alerta_delegados",
            joinColumns = @JoinColumn(name = "alerta_id"),
            inverseJoinColumns = @JoinColumn(name = "veterinario_id")
    )
    private List<Veterinario> delegados = new ArrayList<>();

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaCierre;

    private String resolucion; // Ej: "Resuelto", "Derivado", "Requiere seguimiento"

    @Column(columnDefinition = "TEXT")
    private String observacionCierre;
}