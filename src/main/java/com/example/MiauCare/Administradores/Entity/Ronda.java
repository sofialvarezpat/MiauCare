package com.example.MiauCare.Administradores.Entity;



import com.example.MiauCare.Administradores.enums.EstadoRonda;
import com.example.MiauCare.Catalogos.Entity.Zona;
import com.example.MiauCare.Veterinarios.Entity.Veterinario;
import com.example.MiauCare.Voluntarios.Entity.Voluntario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


@Entity
    @Table(name = "rondas")
    @Getter
    @Setter
public class Ronda {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne
        @JoinColumn(name = "zona_id", nullable = false)
        private Zona zona;

        @Column(nullable = false)
        private LocalDate fecha;

        @Column(nullable = false)
        private LocalTime horaInicio;

        @Column(nullable = false)
        private LocalTime horaFin;

        @ManyToOne
        @JoinColumn(name = "veterinario_id", nullable = false)
        private Veterinario veterinario;

        @ManyToMany
        @JoinTable(
                name = "ronda_voluntarios",
                joinColumns = @JoinColumn(name = "ronda_id"),
                inverseJoinColumns = @JoinColumn(name = "voluntario_id")
        )
        private List<Voluntario> voluntarios = new ArrayList<>();

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private EstadoRonda estado;
    }

