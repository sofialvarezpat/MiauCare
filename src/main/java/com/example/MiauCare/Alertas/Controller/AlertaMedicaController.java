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

    @GetMapping("/nuevas")
    public List<AlertaMedica> listarNuevas() {
        return alertaService.listarNuevas();
    }

    @GetMapping("/veterinario/{veterinarioId}")
    public List<AlertaMedica> listarPorVeterinario(@PathVariable Long veterinarioId,
                                                   @RequestParam(required = false) String estado) {
        return alertaService.listarPorVeterinario(veterinarioId, estado);
    }

    @PatchMapping("/{id}/aceptar")
    public ResponseEntity<AlertaMedica> aceptar(@PathVariable Long id, @RequestParam Long veterinarioId) {
        return ResponseEntity.ok(alertaService.aceptarCaso(id, veterinarioId));
    }

    @PatchMapping("/{id}/delegar")
    public ResponseEntity<AlertaMedica> delegar(@PathVariable Long id,
                                                @RequestParam Long veterinarioQueDelega,
                                                @RequestParam Long veterinarioDestino) {
        return ResponseEntity.ok(alertaService.delegar(id, veterinarioQueDelega, veterinarioDestino));
    }

    @PatchMapping("/{id}/en-atencion")
    public ResponseEntity<AlertaMedica> pasarAEnAtencion(@PathVariable Long id, @RequestParam Long veterinarioId) {
        return ResponseEntity.ok(alertaService.pasarAEnAtencion(id, veterinarioId));
    }

    @PatchMapping("/{id}/cerrar")
    public ResponseEntity<AlertaMedica> cerrar(@PathVariable Long id, @RequestBody Map<String, String> body) {
        Long veterinarioId = Long.valueOf(body.get("veterinarioId"));
        return ResponseEntity.ok(alertaService.cerrar(id, veterinarioId, body.get("resolucion"), body.get("observacion")));
    }
}
