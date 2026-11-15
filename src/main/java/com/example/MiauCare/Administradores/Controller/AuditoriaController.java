package com.example.MiauCare.Administradores.Controller;

import com.example.MiauCare.Administradores.Entity.RegistroAuditoria;
import com.example.MiauCare.Administradores.Service.AuditoriaService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/auditoria")
public class AuditoriaController {

    private final AuditoriaService auditoriaService;

    public AuditoriaController(AuditoriaService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    // GET /api/auditoria (con filtros opcionales: ?tipoAccion=ELIMINAR_GATO&usuario=admin&buscar=Manchas&fecha=2026-11-14)
    @GetMapping
    public List<RegistroAuditoria> consultar(
            @RequestParam(required = false) String tipoAccion,
            @RequestParam(required = false) String usuario,
            @RequestParam(required = false) String buscar,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return auditoriaService.consultar(tipoAccion, usuario, buscar, fecha);
    }

    // POST /api/auditoria -> Permite registrar manualmente un evento de auditoría o probar la bandeja
    @PostMapping
    public ResponseEntity<RegistroAuditoria> registrar(@RequestBody RegistroAuditoria payload,
                                                       Authentication authentication) {
        String usuario = (payload.getUsuarioResponsable() != null && !payload.getUsuarioResponsable().isBlank())
                ? payload.getUsuarioResponsable()
                : authentication.getName();
        return ResponseEntity.ok(
                auditoriaService.registrarAccion(payload.getTipoAccion(), payload.getDescripcion(), usuario));
    }
}
