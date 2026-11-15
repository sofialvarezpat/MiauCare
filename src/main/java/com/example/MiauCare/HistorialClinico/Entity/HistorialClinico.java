package com.example.MiauCare.HistorialClinico.Entity;

import com.example.MiauCare.Catalogos.Entity.DiagnosticoCatalogo;
import com.example.MiauCare.Gatos.Entity.Gato;
import com.example.MiauCare.HistorialClinico.enums.NivelGravedad;
import com.example.MiauCare.HistorialClinico.enums.TipoHistorial;
import com.example.MiauCare.Veterinarios.Entity.Veterinario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "historial_clinico")
@Getter
@Setter
public class HistorialClinico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "gato_id", nullable = false)
    private Gato gato;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoHistorial tipo;

    @ManyToOne
    @JoinColumn(name = "diagnostico_id")
    private DiagnosticoCatalogo diagnostico;

    @Enumerated(EnumType.STRING)
    private NivelGravedad nivelGravedad;

    @Column(nullable = false)
    private String descripcion;

    @Column(columnDefinition = "TEXT")
    private String notas;

    // Peso tomado durante la consulta clínica (ej: 3.8 kg en el mockup de Manchas)
    private Double pesoRegistrado;

    // Resolución del caso en "Registrar atención": true = Dejar hospitalizado, false = Dar de alta (En calle)
    private Boolean dejarHospitalizado;

    @ManyToOne
    @JoinColumn(name = "veterinario_id", nullable = false)
    private Veterinario veterinario;

    @Column(nullable = false)
    private LocalDate fecha;
}