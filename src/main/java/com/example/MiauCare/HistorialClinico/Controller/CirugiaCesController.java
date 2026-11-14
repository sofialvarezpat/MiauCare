package com.example.MiauCare.HistorialClinico.Controller;

import com.example.MiauCare.DTO.CompletarCesRequest;
import com.example.MiauCare.HistorialClinico.Entity.CirugiaCes;
import com.example.MiauCare.HistorialClinico.Service.CirugiaCesService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/cirugias-ces")
public class CirugiaCesController {

    private final CirugiaCesService cirugiaService;

    public CirugiaCesController(CirugiaCesService cirugiaService) {
        this.cirugiaService = cirugiaService;
    }

    // GET /api/cirugias-ces  o  GET /api/cirugias-ces?fecha=2026-11-14
    @GetMapping
    public List<CirugiaCes> listar(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return cirugiaService.listarPorFecha(fecha);
    }

    // Agenda clínica del veterinario logueado (el veterinario sale del token)
    @GetMapping("/mias")
    public List<CirugiaCes> mias(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            Authentication authentication) {
        return cirugiaService.listarPorVeterinario(authentication.getName(), fecha);
    }

    @GetMapping("/gato/{gatoId}")
    public List<CirugiaCes> porGato(@PathVariable Long gatoId) {
        return cirugiaService.listarPorGato(gatoId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CirugiaCes> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(cirugiaService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<CirugiaCes> programar(@RequestBody CirugiaCes cirugia,
                                                Authentication authentication) {
        return ResponseEntity.ok(cirugiaService.programar(cirugia, authentication.getName()));
    }

    @PatchMapping("/{id}/iniciar")
    public ResponseEntity<CirugiaCes> iniciar(@PathVariable Long id, Authentication authentication) {
        return ResponseEntity.ok(cirugiaService.iniciar(id, authentication.getName()));
    }

    @PatchMapping("/{id}/completar")
    public ResponseEntity<CirugiaCes> completar(@PathVariable Long id,
                                                @RequestBody CompletarCesRequest body,
                                                Authentication authentication) {
        return ResponseEntity.ok(cirugiaService.completar(
                id, authentication.getName(), body.resultado(), body.darDeAlta()));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<CirugiaCes> cancelar(@PathVariable Long id, Authentication authentication) {
        return ResponseEntity.ok(cirugiaService.cancelar(id, authentication.getName()));
    }
}