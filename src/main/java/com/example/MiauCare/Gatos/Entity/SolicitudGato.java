package com.example.MiauCare.Gatos.Entity;

import com.example.MiauCare.Gatos.enums.EstadoSolicitud;
import com.example.MiauCare.Gatos.enums.TipoSolicitudGato;
import com.example.MiauCare.Voluntarios.Entity.Voluntario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "solicitudes_gato")
@Getter
@Setter
public class SolicitudGato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "gato_id", nullable = false)
    private Gato gato;

    @ManyToOne
    @JoinColumn(name = "voluntario_id", nullable = false)
    private Voluntario voluntario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoSolicitudGato tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoSolicitud estado;

    @Column(nullable = false)
    private LocalDateTime fecha;
}