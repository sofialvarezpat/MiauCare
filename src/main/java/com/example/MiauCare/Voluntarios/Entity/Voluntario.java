package com.example.MiauCare.Voluntarios.Entity;

import com.example.MiauCare.Catalogos.Entity.Zona;
import com.example.MiauCare.Voluntarios.enums.EstadoVoluntario;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    // RF-01: Teléfono de contacto del voluntario
    private String telefono;

    // RF-01: Disponibilidad horaria del voluntario (ej: "Mañanas", "Fines de semana")
    private String disponibilidad;

    // RF-01 y RF-02: Coordenada GPS latitud base del voluntario
    private Double latitudBase;

    // RF-01 y RF-02: Coordenada GPS longitud base del voluntario
    private Double longitudBase;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false)
    private String contrasena;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoVoluntario estado;

    // Colonia/zona asignada automáticamente por GPS o elegida manualmente (RN-07)
    @ManyToOne
    @JoinColumn(name = "zona_id")
    private Zona zona;

    @Column(nullable = false)
    private Boolean recibiendoAlertas = true;
}