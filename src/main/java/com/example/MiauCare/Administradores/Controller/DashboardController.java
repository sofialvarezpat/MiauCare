package com.example.MiauCare.Administradores.Controller;

import com.example.MiauCare.Administradores.enums.EstadoAprobacionGato;
import com.example.MiauCare.Alertas.Repository.AlertaMedicaRepository;
import com.example.MiauCare.DTO.DashboardResumenDto;
import com.example.MiauCare.Gatos.Repository.GatoRepository;
import com.example.MiauCare.Gatos.enums.EstadoInternacion;
import com.example.MiauCare.Veterinarios.Repository.VeterinarioRepository;
import com.example.MiauCare.Veterinarios.enums.EstadoVeterinario;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final GatoRepository gatoRepository;
    private final VeterinarioRepository veterinarioRepository;
    private final AlertaMedicaRepository alertaRepository;

    public DashboardController(GatoRepository gatoRepository,
                               VeterinarioRepository veterinarioRepository,
                               AlertaMedicaRepository alertaRepository) {
        this.gatoRepository = gatoRepository;
        this.veterinarioRepository = veterinarioRepository;
        this.alertaRepository = alertaRepository;
    }

    // GET /api/dashboard/resumen -> Devuelve las métricas en tiempo real para las tarjetas de inicio del Admin y Veterinario
    @GetMapping("/resumen")
    public DashboardResumenDto obtenerResumen() {
        long urgencias = alertaRepository.findAll().stream()
                .filter(a -> a.getEstado().name().equals("NUEVA"))
                .count();

        long internados = gatoRepository.findAll().stream()
                .filter(g -> g.getEstadoInternacion() == EstadoInternacion.HOSPITALIZADO)
                .count();

        long activos = gatoRepository.findByEstadoAprobacion(EstadoAprobacionGato.APROBADO).size();

        long vetActivos = veterinarioRepository.findByEstado(EstadoVeterinario.ACTIVO).size();

        long solicitudesVet = veterinarioRepository.findByEstado(EstadoVeterinario.PENDIENTE).size();

        // CORRECCIÓN: Se usa el método del repositorio para filtrar por estado pendiente de aprobación
        long solicitudesGatos = gatoRepository.findByEstadoAprobacion(EstadoAprobacionGato.PENDIENTE).size();

        return new DashboardResumenDto(
                urgencias,
                activos,
                internados,
                activos,
                vetActivos,
                solicitudesVet + solicitudesGatos
        );
    }
}
