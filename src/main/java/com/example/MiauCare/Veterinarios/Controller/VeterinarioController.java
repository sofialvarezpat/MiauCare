package com.example.MiauCare.Veterinarios.Controller;

import com.example.MiauCare.Veterinarios.Entity.Veterinario;
import com.example.MiauCare.Veterinarios.Service.VeterinarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/veterinarios")
public class VeterinarioController {

    private final VeterinarioService veterinarioService;

    public VeterinarioController(VeterinarioService veterinarioService) {
        this.veterinarioService = veterinarioService;
    }

    @GetMapping
    public List<Veterinario> listar() {
        return veterinarioService.listarTodos();
    }

    @GetMapping("/pendientes")
    public List<Veterinario> listarPendientes() {
        return veterinarioService.listarPendientes();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Veterinario> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(veterinarioService.obtenerPorId(id));
    }

    @PostMapping("/solicitud")
    public ResponseEntity<Veterinario> enviarSolicitud(@RequestBody Veterinario veterinario) {
        return ResponseEntity.ok(veterinarioService.enviarSolicitud(veterinario));
    }

    @PatchMapping("/{id}/aprobar")
    public ResponseEntity<Veterinario> aprobar(@PathVariable Long id) {
        return ResponseEntity.ok(veterinarioService.aprobar(id));
    }

    @PatchMapping("/{id}/rechazar")
    public ResponseEntity<Veterinario> rechazar(@PathVariable Long id) {
        return ResponseEntity.ok(veterinarioService.rechazar(id));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<Void> cambiarEstado(@PathVariable Long id, @RequestParam boolean activo) {
        veterinarioService.cambiarEstado(id, activo);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/disponibilidad")
    public ResponseEntity<Void> cambiarDisponibilidad(@PathVariable Long id, @RequestParam boolean disponible) {
        veterinarioService.cambiarDisponibilidad(id, disponible);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Veterinario> actualizar(@PathVariable Long id, @RequestBody Veterinario veterinario) {
        return ResponseEntity.ok(veterinarioService.actualizar(id, veterinario));
    }
}
