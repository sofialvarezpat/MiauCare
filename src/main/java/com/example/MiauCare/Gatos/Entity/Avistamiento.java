package com.example.MiauCare.Gatos.Entity;

import com.example.MiauCare.Catalogos.Entity.Zona;
import com.example.MiauCare.Gatos.enums.EstadoAparente;
import com.example.MiauCare.Voluntarios.Entity.Voluntario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "avistamientos")
@Getter
@Setter
public class Avistamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "gato_id")
    private Gato gato; // null si es un gato no censado

    @ManyToOne
    @JoinColumn(name = "voluntario_id", nullable = false)
    private Voluntario voluntario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoAparente estadoAparente;

    private Boolean comio; // null si no aplica (gato no censado)

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @Column(nullable = false)
    private LocalDateTime fecha;

    // Campos solo para gato NO censado
    @ManyToOne
    @JoinColumn(name = "zona_id")
    private Zona zona;

    private String fotoUrl;

    @Column(nullable = false)
    private Boolean esGatoNoCensado = false;
}
