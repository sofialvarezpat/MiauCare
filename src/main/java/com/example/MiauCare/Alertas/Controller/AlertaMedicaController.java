package com.example.MiauCare.Alertas.Controller;

import com.example.MiauCare.Alertas.Entity.AlertaMedica;
import com.example.MiauCare.Alertas.Service.AlertaMedicaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/alertas-medicas")
public class AlertaMedicaController {

    private final AlertaMedicaService alertaService;

    public AlertaMedicaController(AlertaMedicaService alertaService) {
        this.alertaService = alertaService;
    }

    // Carrusel de urgencias (para Veterinario)
    @GetMapping("/nuevas")
    public List<AlertaMedica> listarNuevas() {
        return alertaService.listarNuevas();
    }

    // NUEVO: Lista las alertas filtradas por zona (para la pantalla "Alertas de mi Zona" del Voluntario)
    @GetMapping("/zona/{zonaId}")
    public List<AlertaMedica> listarPorZona(@PathVariable Long zonaId) {
        return alertaService.listarPorZona(zonaId);
    }

    // Lista pacientes a cargo / alertas del veterinario
    @GetMapping("/veterinario/{veterinarioId}")
    public List<AlertaMedica> listarPorVeterinario(@PathVariable Long veterinarioId,
                                                   @RequestParam(required = false) String estado) {
        return alertaService.listarPorVeterinario(veterinarioId, estado);
    }

    // Aceptar caso / pasar a en atención
    @PatchMapping("/{id}/aceptar")
    public ResponseEntity<AlertaMedica> aceptarCaso(@PathVariable Long id,
                                                    @RequestBody Map<String, Long> body) {
        return ResponseEntity.ok(alertaService.aceptarCaso(id, body.get("veterinarioId")));
    }

    @PatchMapping("/{id}/en-atencion")
    public ResponseEntity<AlertaMedica> pasarAEnAtencion(@PathVariable Long id,
                                                         @RequestBody Map<String, Long> body) {
        return ResponseEntity.ok(alertaService.pasarAEnAtencion(id, body.get("veterinarioId")));
    }

    // Delegar a colega del directorio
    @PatchMapping("/{id}/delegar")
    public ResponseEntity<AlertaMedica> delegar(@PathVariable Long id,
                                                @RequestBody Map<String, Long> body) {
        return ResponseEntity.ok(alertaService.delegar(id, body.get("veterinarioId"), body.get("destinoId")));
    }

    // Cerrar alerta con resolución y observaciones (RN-22, RN-23)
    @PatchMapping("/{id}/cerrar")
    public ResponseEntity<AlertaMedica> cerrar(@PathVariable Long id,
                                               @RequestBody Map<String, Object> body) {
        Long vetId = Long.valueOf(body.get("veterinarioId").toString());
        String resolucion = (String) body.get("resolucion");
        String observacion = (String) body.get("observacion");
        return ResponseEntity.ok(alertaService.cerrar(id, vetId, resolucion, observacion));
    }
}