package com.example.MiauCare.Voluntarios.Entity;

import com.example.MiauCare.Catalogos.Entity.Zona;
import com.example.MiauCare.Voluntarios.enums.EstadoVoluntario;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "voluntarios")
@Getter
@Setter
public class Voluntario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombreCompleto;

    @Column(nullable = false)
    private String numeroCedula;

    @Column(nullable = false, unique = true)
    private String correo;

    @JsonIgnore
    @Column(nullable = false)
    private String contrasena;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoVoluntario estado;

    @ManyToOne
    @JoinColumn(name = "zona_id")
    private Zona zona;

    @Column(nullable = false)
    private Boolean recibiendoAlertas = true;
}