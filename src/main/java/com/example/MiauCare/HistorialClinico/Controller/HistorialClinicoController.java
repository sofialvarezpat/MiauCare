package com.example.MiauCare.HistorialClinico.Controller;

import com.example.MiauCare.Exception.SinPermisosException;
import com.example.MiauCare.HistorialClinico.Entity.HistorialClinico;
import com.example.MiauCare.HistorialClinico.Service.HistorialClinicoService;
import com.example.MiauCare.Veterinarios.Entity.Veterinario;
import com.example.MiauCare.Veterinarios.Repository.VeterinarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/historial-clinico")
public class HistorialClinicoController {

    private final HistorialClinicoService historialService;
    private final VeterinarioRepository veterinarioRepository;

    public HistorialClinicoController(HistorialClinicoService historialService,
                                      VeterinarioRepository veterinarioRepository) {
        this.historialService = historialService;
        this.veterinarioRepository = veterinarioRepository;
    }

    @GetMapping("/gato/{gatoId}")
    public List<HistorialClinico> listarPorGato(@PathVariable Long gatoId) {
        return historialService.listarPorGato(gatoId);
    }

    // El veterinario que firma la atención sale del token (no del body), así nadie registra a nombre de otro
    @PostMapping
    public ResponseEntity<HistorialClinico> registrar(@RequestBody HistorialClinico entrada,
                                                      Authentication authentication) {
        Veterinario veterinario = veterinarioRepository.findByCorreo(authentication.getName())
                .orElseThrow(() -> new SinPermisosException("Solo un veterinario registrado puede firmar la atención clínica."));
        entrada.setVeterinario(veterinario);
        return ResponseEntity.ok(historialService.registrar(entrada));
    }
}