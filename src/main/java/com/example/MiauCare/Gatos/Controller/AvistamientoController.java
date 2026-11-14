package com.example.MiauCare.Gatos.Controller;

import com.example.MiauCare.Gatos.Entity.Avistamiento;
import com.example.MiauCare.Gatos.Service.AvistamientoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/avistamientos")
public class AvistamientoController {

    private final AvistamientoService avistamientoService;

    public AvistamientoController(AvistamientoService avistamientoService) {
        this.avistamientoService = avistamientoService;
    }

    @GetMapping("/gato/{gatoId}")
    public List<Avistamiento> listarPorGato(@PathVariable Long gatoId) {
        return avistamientoService.listarPorGato(gatoId);
    }

    @GetMapping("/zona/{zonaId}")
    public List<Avistamiento> listarPorZona(@PathVariable Long zonaId) {
        return avistamientoService.listarPorZona(zonaId);
    }

    @PostMapping("/normal")
    public ResponseEntity<Avistamiento> registrarNormal(@RequestBody Avistamiento avistamiento) {
        return ResponseEntity.ok(avistamientoService.registrarAvistamientoNormal(avistamiento));
    }

    @PostMapping("/no-censado")
    public ResponseEntity<Avistamiento> registrarNoCensado(@RequestBody Avistamiento avistamiento) {
        return ResponseEntity.ok(avistamientoService.registrarGatoNoCensado(avistamiento));
    }
}
