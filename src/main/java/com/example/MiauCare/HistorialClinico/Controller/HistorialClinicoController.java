package com.example.MiauCare.HistorialClinico.Controller;

import com.example.MiauCare.HistorialClinico.Entity.HistorialClinico;
import com.example.MiauCare.HistorialClinico.Service.HistorialClinicoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/historial-clinico")
public class HistorialClinicoController {

    private final HistorialClinicoService historialService;

    public HistorialClinicoController(HistorialClinicoService historialService) {
        this.historialService = historialService;
    }

    @GetMapping("/gato/{gatoId}")
    public List<HistorialClinico> listarPorGato(@PathVariable Long gatoId) {
        return historialService.listarPorGato(gatoId);
    }

    @PostMapping
    public ResponseEntity<HistorialClinico> registrar(@RequestBody HistorialClinico entrada) {
        return ResponseEntity.ok(historialService.registrar(entrada));
    }
}