package com.example.MiauCare.Gatos.Entity;

import com.example.MiauCare.Administradores.enums.EstadoAprobacionGato;
import com.example.MiauCare.Catalogos.Entity.Zona;
import com.example.MiauCare.Gatos.enums.EstadoInternacion;
import com.example.MiauCare.Gatos.enums.EstadoVidaGato;
import com.example.MiauCare.Gatos.enums.SexoGato;
import com.example.MiauCare.Veterinarios.Entity.Veterinario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "gatos")
@Getter
@Setter
public class Gato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String codigo; // Ej: MC-024

    @Column(nullable = false)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SexoGato sexo;

    private String edadEstimada; // Ej: "3 años", "2 meses" (texto libre como en los mockups)

    private String raza;

    private Double peso;

    @ManyToOne
    @JoinColumn(name = "zona_id")
    private Zona zona;

    @Column(nullable = false)
    private Boolean esterilizado = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoVidaGato estadoVida;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoInternacion estadoInternacion;

    @Enumerated(EnumType.STRING)
    private EstadoAprobacionGato estadoAprobacion = EstadoAprobacionGato.APROBADO;

    private String fotoUrl;

    @ManyToOne
    @JoinColumn(name = "veterinario_responsable_id")
    private Veterinario veterinarioResponsable; // null hasta que se asigne (RN02)
}