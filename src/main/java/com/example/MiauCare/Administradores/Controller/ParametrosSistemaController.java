package com.example.MiauCare.Administradores.Controller;

import com.example.MiauCare.Administradores.Entity.ParametrosSistema;
import com.example.MiauCare.Administradores.Service.ParametrosSistemaService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/parametros")
public class ParametrosSistemaController {

    private final ParametrosSistemaService parametrosService;

    public ParametrosSistemaController(ParametrosSistemaService parametrosService) {
        this.parametrosService = parametrosService;
    }

    // GET /api/parametros -> Muestra los valores actuales de X, N y T en la pantalla "Parámetros del sistema"
    @GetMapping
    public ResponseEntity<ParametrosSistema> obtener() {
        return ResponseEntity.ok(parametrosService.obtener());
    }

    // PUT /api/parametros -> Botón coral "Guardar parámetros" del Administrador
    // Body esperado: { "diasAusenciaAlerta": 7, "reportesCriticosConsecutivos": 2, "tiempoMaximoRespuestaHoras": 24 }
    @PutMapping
    public ResponseEntity<ParametrosSistema> actualizar(@RequestBody ParametrosSistema parametros,
                                                        Authentication authentication) {
        return ResponseEntity.ok(parametrosService.actualizar(parametros, authentication.getName()));
    }
}
