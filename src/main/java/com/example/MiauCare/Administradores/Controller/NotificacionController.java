package com.example.MiauCare.Administradores.Controller;

import com.example.MiauCare.Administradores.Entity.NotificacionGlobal;
import com.example.MiauCare.Administradores.Service.NotificacionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {

    private final NotificacionService notificacionService;

    public NotificacionController(NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }

    // Historial completo de notificaciones (vista Administrador)
    @GetMapping
    public List<NotificacionGlobal> listarTodas() {
        return notificacionService.listarTodas();
    }

    // Bandeja del usuario logueado ("Tus notificaciones y alertas"): filtra automáticamente según el rol de su token
    @GetMapping("/mias")
    public List<NotificacionGlobal> listarMias(Authentication authentication) {
        String rol = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("");
        return notificacionService.listarParaRol(rol);
    }

    // Pantalla "Notificación global" del Admin: publica un aviso para Veterinarios, Voluntarios o Ambos
    // Body esperado: { "titulo": "Aviso importante", "mensaje": "...", "destinatarios": "AMBOS" }
    @PostMapping
    public ResponseEntity<NotificacionGlobal> crear(@RequestBody NotificacionGlobal notificacion,
                                                    Authentication authentication) {
        return ResponseEntity.ok(notificacionService.crear(notificacion, authentication.getName()));
    }

    // Permite al Administrador borrar un anuncio
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        notificacionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}