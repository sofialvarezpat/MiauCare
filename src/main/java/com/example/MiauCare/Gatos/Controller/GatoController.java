package com.example.MiauCare.Gatos.Controller;

import com.example.MiauCare.Gatos.Entity.Gato;
import com.example.MiauCare.Gatos.Service.GatoService;
import com.example.MiauCare.Gatos.enums.EstadoVidaGato;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/gatos")
public class GatoController {

    private final GatoService gatoService;

    public GatoController(GatoService gatoService) {
        this.gatoService = gatoService;
    }

    // Lista gatos aprobados (permite filtrar opcionalmente por nombre o por zonaId)
    @GetMapping
    public List<Gato> listar(@RequestParam(required = false) String nombre,
                             @RequestParam(required = false) Long zonaId) {
        if (nombre != null) return gatoService.buscarPorNombre(nombre);
        if (zonaId != null) return gatoService.listarPorZona(zonaId);
        return gatoService.listarTodos();
    }

    // Bandeja del Admin: lista gatos nuevos pendientes de aprobación
    @GetMapping("/pendientes")
    public List<Gato> listarPendientes() {
        return gatoService.listarPendientes();
    }

    // NUEVO: Lista los pacientes asignados a un veterinario ("Tus pacientes" en Vet y "Directorio médico" en Admin)
    @GetMapping("/veterinario/{veterinarioId}")
    public List<Gato> listarPorVeterinario(@PathVariable Long veterinarioId) {
        return gatoService.listarPorVeterinario(veterinarioId);
    }

    // Obtiene el detalle de un gato por su ID
    @GetMapping("/{id}")
    public ResponseEntity<Gato> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(gatoService.obtenerPorId(id));
    }

    // Obtiene el detalle de un gato por su código (ej: MC-024)
    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<Gato> obtenerPorCodigo(@PathVariable String codigo) {
        return ResponseEntity.ok(gatoService.obtenerPorCodigo(codigo));
    }

    // Crea un gato: si quien llama es Admin queda APROBADO; si es Veterinario queda PENDIENTE
    @PostMapping
    public ResponseEntity<Gato> crear(@RequestBody Gato gato, Authentication authentication) {
        boolean esAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRADOR"));
        return ResponseEntity.ok(gatoService.crear(gato, esAdmin));
    }

    // Admin aprueba el registro de un gato nuevo
    @PatchMapping("/{id}/aprobar")
    public ResponseEntity<Gato> aprobar(@PathVariable Long id) {
        return ResponseEntity.ok(gatoService.aprobar(id));
    }

    // Admin rechaza el registro de un gato nuevo
    @PatchMapping("/{id}/rechazar")
    public ResponseEntity<Gato> rechazar(@PathVariable Long id) {
        return ResponseEntity.ok(gatoService.rechazar(id));
    }

    // NUEVO: Admin reasigna un gato puntual a otro veterinario desde "Directorio médico y Reasignación"
    // Body esperado: { "veterinarioId": 2 }
    @PatchMapping("/{id}/reasignar-veterinario")
    public ResponseEntity<Gato> reasignarVeterinario(@PathVariable Long id,
                                                     @RequestBody Map<String, Long> body) {
        Long veterinarioId = body.get("veterinarioId");
        if (veterinarioId == null) {
            throw new IllegalArgumentException("Debes indicar el veterinarioId destino.");
        }
        return ResponseEntity.ok(gatoService.asignarVeterinarioResponsable(id, veterinarioId));
    }

    // NUEVO: Admin reasigna todos los pacientes de un veterinario a otro de una sola vez
    // Body esperado: { "veterinarioOrigenId": 1, "veterinarioDestinoId": 2 }
    @PostMapping("/reasignar-masivo")
    public ResponseEntity<List<Gato>> reasignarMasivo(@RequestBody Map<String, Long> body) {
        Long origenId = body.get("veterinarioOrigenId");
        Long destinoId = body.get("veterinarioDestinoId");
        if (origenId == null || destinoId == null) {
            throw new IllegalArgumentException("Debes indicar veterinarioOrigenId y veterinarioDestinoId.");
        }
        return ResponseEntity.ok(gatoService.reasignarPacientesDeVeterinario(origenId, destinoId));
    }

    // Actualiza datos maestros del gato
    @PutMapping("/{id}")
    public ResponseEntity<Gato> actualizar(@PathVariable Long id, @RequestBody Gato gato) {
        return ResponseEntity.ok(gatoService.actualizar(id, gato));
    }

    // Cambia el estado de vida del gato (ACTIVO, FALLECIDO, ADOPTADO, TRASLADADO)
    @PatchMapping("/{id}/estado-vida")
    public ResponseEntity<Gato> cambiarEstadoVida(@PathVariable Long id,
                                                  @RequestBody Map<String, String> body) {
        EstadoVidaGato nuevoEstado = EstadoVidaGato.valueOf(body.get("estadoVida"));
        return ResponseEntity.ok(gatoService.cambiarEstadoVida(id, nuevoEstado));
    }

    // Marca al gato como esterilizado
    @PatchMapping("/{id}/esterilizar")
    public ResponseEntity<Gato> marcarEsterilizado(@PathVariable Long id) {
        return ResponseEntity.ok(gatoService.marcarEsterilizado(id));
    }

    // Elimina un gato del sistema (solo Admin)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        gatoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}