package com.example.MiauCare.Auth;

import com.example.MiauCare.Administradores.Entity.Administrador;
import com.example.MiauCare.Administradores.Repository.AdministradorRepository;
import com.example.MiauCare.Veterinarios.Entity.Veterinario;
import com.example.MiauCare.Veterinarios.Repository.VeterinarioRepository;
import com.example.MiauCare.Veterinarios.enums.EstadoVeterinario;
import com.example.MiauCare.Voluntarios.Entity.Voluntario;
import com.example.MiauCare.Voluntarios.Repository.VoluntarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    // Pantalla "Restablecer contraseña" (pública)
    // Body esperado: { "correo": "usuario@mail.com", "nuevaContrasena": "NuevaPassword123" }
    @PostMapping("/restablecer-contrasena")
    public ResponseEntity<Void> restablecerContrasena(@RequestBody RestablecerContrasenaRequest request) {
        authService.restablecerContrasena(request);
        return ResponseEntity.noContent().build();
    }
}