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

    // Nombre de la colonia (ej: "Colonia Centro", "Parque de los Cerezos")
    @Column(nullable = false, unique = true)
    private String nombre;

    // Sector o zona macro a la que pertenece la colonia (ej: "Zona Norte", "Zona Centro") - Mockup Admin
    @Column(name = "zona_macro")
    private String zonaMacro;

    // Dirección o referencia del punto de alimentación (ej: "Plaza Mayor, 3")
    @Column(name = "punto_alimentacion")
    private String puntoAlimentacion;

    // Horario programado de alimentación (ej: 10:00:00)
    @Column(name = "hora_alimentacion")
    private LocalTime horaAlimentacion;

    // Coordenada GPS Latitud para el mapa interactivo y emparejamiento (RF-02, RF-03)
    private Double latitud;

    // Coordenada GPS Longitud para el mapa interactivo y emparejamiento (RF-02, RF-03)
    private Double longitud;
}