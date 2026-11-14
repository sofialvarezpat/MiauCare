package com.example.MiauCare.Administradores.Controller;

import com.example.MiauCare.Administradores.Entity.Administrador;
import com.example.MiauCare.Administradores.Service.AdministradorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/administradores")
public class AdministradorController {

    private final AdministradorService administradorService;

    public AdministradorController(AdministradorService administradorService) {
        this.administradorService = administradorService;
    }

    @GetMapping
    public List<Administrador> listar() {
        return administradorService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Administrador> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(administradorService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<Administrador> crear(@RequestBody Map<String, Object> payload) {
        Administrador nuevo = new Administrador();
        nuevo.setNombreCompleto((String) payload.get("nombreCompleto"));
        nuevo.setCorreo((String) payload.get("correo"));
        String contrasenaTemporal = (String) payload.get("contrasenaTemporal");
        return ResponseEntity.ok(administradorService.crear(nuevo, contrasenaTemporal));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Administrador> actualizarPerfil(@PathVariable Long id, @RequestBody Administrador administrador) {
        return ResponseEntity.ok(administradorService.actualizarPerfil(id, administrador));
    }

    @PatchMapping("/{id}/contrasena")
    public ResponseEntity<Void> cambiarContrasena(@PathVariable Long id, @RequestBody Map<String, String> body) {
        administradorService.cambiarContrasena(id, body.get("nuevaContrasena"));
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<Void> cambiarEstado(@PathVariable Long id, @RequestParam boolean activo) {
        administradorService.cambiarEstado(id, activo);
        return ResponseEntity.noContent().build();
    }
}
