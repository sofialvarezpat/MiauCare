package com.example.MiauCare.Administradores.Controller;

import com.example.MiauCare.Administradores.Entity.Ronda;
import com.example.MiauCare.Administradores.Service.RondaService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/rondas")
public class RondaController {

    private final RondaService rondaService;

    public RondaController(RondaService rondaService) {
        this.rondaService = rondaService;
    }

    // GET /api/rondas  o  GET /api/rondas?fecha=2026-11-14
    @GetMapping
    public List<Ronda> listar(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        if (fecha != null) {
            return rondaService.listarPorFecha(fecha);
        }
        return rondaService.listarTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ronda> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(rondaService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<Ronda> crear(@RequestBody Ronda ronda) {
        return ResponseEntity.ok(rondaService.crear(ronda));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ronda> editar(@PathVariable Long id, @RequestBody Ronda ronda) {
        return ResponseEntity.ok(rondaService.editar(id, ronda));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        rondaService.cancelar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/iniciar")
    public ResponseEntity<Ronda> iniciar(@PathVariable Long id) {
        return ResponseEntity.ok(rondaService.iniciar(id));
    }

    @PatchMapping("/{id}/finalizar")
    public ResponseEntity<Ronda> finalizar(@PathVariable Long id) {
        return ResponseEntity.ok(rondaService.finalizar(id));
    }

    // El voluntario sale del token (no se manda por parámetro), así nadie inscribe a otra persona
    @PatchMapping("/{id}/asistir")
    public ResponseEntity<Ronda> asistir(@PathVariable Long id, Authentication authentication) {
        return ResponseEntity.ok(rondaService.asistir(id, authentication.getName()));
    }
}