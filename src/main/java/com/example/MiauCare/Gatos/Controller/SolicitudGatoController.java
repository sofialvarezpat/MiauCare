package com.example.MiauCare.Gatos.Controller;

import com.example.MiauCare.Gatos.Entity.SolicitudGato;
import com.example.MiauCare.Gatos.Service.SolicitudGatoService;
import com.example.MiauCare.Gatos.enums.TipoSolicitudGato;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/solicitudes-gato")
public class SolicitudGatoController {

    private final SolicitudGatoService solicitudService;

    public SolicitudGatoController(SolicitudGatoService solicitudService) {
        this.solicitudService = solicitudService;
    }

    @GetMapping("/pendientes")
    public List<SolicitudGato> listarPendientes() {
        return solicitudService.listarPendientes();
    }

    @PostMapping
    public ResponseEntity<SolicitudGato> crear(@RequestBody Map<String, Object> body) {
        Long gatoId = Long.valueOf(body.get("gatoId").toString());
        Long voluntarioId = Long.valueOf(body.get("voluntarioId").toString());
        TipoSolicitudGato tipo = TipoSolicitudGato.valueOf(body.get("tipo").toString());
        return ResponseEntity.ok(solicitudService.crear(gatoId, voluntarioId, tipo));
    }

    @PatchMapping("/{id}/aceptar")
    public ResponseEntity<SolicitudGato> aceptar(@PathVariable Long id) {
        return ResponseEntity.ok(solicitudService.aceptar(id));
    }

    @PatchMapping("/{id}/rechazar")
    public ResponseEntity<SolicitudGato> rechazar(@PathVariable Long id) {
        return ResponseEntity.ok(solicitudService.rechazar(id));
    }
}