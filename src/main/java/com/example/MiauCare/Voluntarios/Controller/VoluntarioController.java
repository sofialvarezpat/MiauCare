package com.example.MiauCare.Voluntarios.Controller;

import com.example.MiauCare.Voluntarios.Entity.Voluntario;
import com.example.MiauCare.Voluntarios.Service.VoluntarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/voluntarios")
public class VoluntarioController {

    private final VoluntarioService voluntarioService;

    public VoluntarioController(VoluntarioService voluntarioService) {
        this.voluntarioService = voluntarioService;
    }

    @GetMapping
    public List<Voluntario> listar() {
        return voluntarioService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Voluntario> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(voluntarioService.obtenerPorId(id));
    }

    @PostMapping("/registro")
    public ResponseEntity<Voluntario> registrar(@RequestBody Voluntario voluntario) {
        return ResponseEntity.ok(voluntarioService.registrar(voluntario));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Voluntario> actualizar(@PathVariable Long id, @RequestBody Voluntario voluntario) {
        return ResponseEntity.ok(voluntarioService.actualizar(id, voluntario));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<Void> cambiarEstado(@PathVariable Long id, @RequestParam boolean activo) {
        voluntarioService.cambiarEstado(id, activo);
        return ResponseEntity.noContent().build();
    }
}