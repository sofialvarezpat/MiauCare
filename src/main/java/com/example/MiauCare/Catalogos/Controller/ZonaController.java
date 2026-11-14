package com.example.MiauCare.Catalogos.Controller;

import com.example.MiauCare.Catalogos.Entity.Zona;
import com.example.MiauCare.Catalogos.Service.ZonaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/zonas")
public class ZonaController {

    private final ZonaService zonaService;

    public ZonaController(ZonaService zonaService) {
        this.zonaService = zonaService;
    }

    @GetMapping
    public List<Zona> listar() {
        return zonaService.listarTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Zona> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(zonaService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<Zona> crear(@RequestBody Zona zona) {
        return ResponseEntity.ok(zonaService.crear(zona));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Zona> actualizar(@PathVariable Long id, @RequestBody Zona zona) {
        return ResponseEntity.ok(zonaService.actualizar(id, zona));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        zonaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}