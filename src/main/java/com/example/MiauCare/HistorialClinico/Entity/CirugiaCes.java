package com.example.MiauCare.HistorialClinico.Entity;

import com.example.MiauCare.Gatos.Entity.Gato;
import com.example.MiauCare.HistorialClinico.enums.EstadoCirugia;
import com.example.MiauCare.Veterinarios.Entity.Veterinario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;


    @Entity
    @Table(name = "cirugias_ces")
    @Getter
    @Setter
    public class CirugiaCes {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne
        @JoinColumn(name = "gato_id", nullable = false)
        private Gato gato;

        @ManyToOne
        @JoinColumn(name = "veterinario_id", nullable = false)
        private Veterinario veterinario;

        @Column(nullable = false)
        private LocalDate fecha;

        @Column(nullable = false)
        private LocalTime horaInicio;

        @Column(nullable = false)
        private LocalTime horaFin;

        @Column(nullable = false)
        private String quirofano; // Ej: "Quirófano 1"

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private EstadoCirugia estado;

        @Column(columnDefinition = "TEXT")
        private String resultado; // se llena al completar la cirugía
    }
