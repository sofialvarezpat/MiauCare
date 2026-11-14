package com.example.MiauCare.Catalogos.Controller;

import com.example.MiauCare.Catalogos.Entity.DiagnosticoCatalogo;
import com.example.MiauCare.Catalogos.Service.DiagnosticoCatalogoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/diagnosticos")
public class DiagnosticoCatalogoController {

    private final DiagnosticoCatalogoService diagnosticoService;

    public DiagnosticoCatalogoController(DiagnosticoCatalogoService diagnosticoService) {
        this.diagnosticoService = diagnosticoService;
    }

    @GetMapping
    public List<DiagnosticoCatalogo> listar() {
        return diagnosticoService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DiagnosticoCatalogo> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(diagnosticoService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<DiagnosticoCatalogo> crear(@RequestBody DiagnosticoCatalogo diagnostico) {
        return ResponseEntity.ok(diagnosticoService.crear(diagnostico));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DiagnosticoCatalogo> actualizar(@PathVariable Long id, @RequestBody DiagnosticoCatalogo diagnostico) {
        return ResponseEntity.ok(diagnosticoService.actualizar(id, diagnostico));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        diagnosticoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
