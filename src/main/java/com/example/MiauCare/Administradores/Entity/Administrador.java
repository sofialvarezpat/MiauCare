package com.example.MiauCare.Administradores.Entity;

import com.example.MiauCare.Administradores.enums.EstadoAdministrador;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "administradores")
@Getter
@Setter
public class Administrador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombreCompleto;

    @Column(nullable = false, unique = true)
    private String correo;

    @JsonIgnore
    @Column(nullable = false)
    private String contrasena;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoAdministrador estado;

    @Column(nullable = false)
    private Boolean debeCambiarContrasena = false; //esto activa la contraseña temporal para un admiistrador nuevo. Por ahora no se necesita, se deja para una actualizacion futura
}