package com.example.MiauCare.Gatos.Controller;

import com.example.MiauCare.Gatos.Entity.Gato;
import com.example.MiauCare.Gatos.Service.GatoService;
import com.example.MiauCare.Gatos.enums.EstadoVidaGato;
import org.springframework.http.ResponseEntity;
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

    @GetMapping
    public List<Gato> listar(@RequestParam(required = false) String nombre,
                             @RequestParam(required = false) Long zonaId) {
        if (nombre != null) return gatoService.buscarPorNombre(nombre);
        if (zonaId != null) return gatoService.listarPorZona(zonaId);
        return gatoService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Gato> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(gatoService.obtenerPorId(id));
    }

    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<Gato> obtenerPorCodigo(@PathVariable String codigo) {
        return ResponseEntity.ok(gatoService.obtenerPorCodigo(codigo));
    }

    @PostMapping
    public ResponseEntity<Gato> crear(@RequestBody Gato gato) {
        return ResponseEntity.ok(gatoService.crear(gato));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Gato> actualizar(@PathVariable Long id, @RequestBody Gato gato) {
        return ResponseEntity.ok(gatoService.actualizar(id, gato));
    }

    @PatchMapping("/{id}/estado-vida")
    public ResponseEntity<Gato> cambiarEstadoVida(@PathVariable Long id, @RequestBody Map<String, String> body) {
        EstadoVidaGato nuevoEstado = EstadoVidaGato.valueOf(body.get("estadoVida"));
        return ResponseEntity.ok(gatoService.cambiarEstadoVida(id, nuevoEstado));
    }

    @PatchMapping("/{id}/esterilizar")
    public ResponseEntity<Gato> marcarEsterilizado(@PathVariable Long id) {
        return ResponseEntity.ok(gatoService.marcarEsterilizado(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        gatoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}