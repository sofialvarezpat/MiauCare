package com.example.MiauCare.Catalogos.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Entity
@Table(name = "zonas")
@Getter
@Setter
public class Zona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(name = "punto_alimentacion")
    private String puntoAlimentacion;

    @Column(name = "hora_alimentacion")
    private LocalTime horaAlimentacion;
}
