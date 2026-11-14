package com.example.MiauCare.Veterinarios.Entity;

import com.example.MiauCare.Catalogos.Entity.Especialidad;
import com.example.MiauCare.Veterinarios.enums.EstadoVeterinario;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "veterinarios")
@Getter
@Setter
public class Veterinario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombreCompleto;

    @Column(nullable = false, unique = true)
    private String correo;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false)
    private String contrasena;

    @Column(nullable = false)
    private String numeroCedula;

    @Column(nullable = false)
    private String fotoCedulaUrl;

    @ManyToOne
    @JoinColumn(name = "especialidad_id", nullable = false)
    private Especialidad especialidad;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoVeterinario estado;

    @Column(nullable = false)
    private Boolean disponibleEmergencias = true;
}