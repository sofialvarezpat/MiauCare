package com.example.MiauCare.Auth;

import com.example.MiauCare.Administradores.Entity.Administrador;
import com.example.MiauCare.Administradores.Repository.AdministradorRepository;
import com.example.MiauCare.Exception.CredencialesInvalidasException;
import com.example.MiauCare.Veterinarios.Entity.Veterinario;
import com.example.MiauCare.Veterinarios.Repository.VeterinarioRepository;
import com.example.MiauCare.Veterinarios.enums.EstadoVeterinario;
import com.example.MiauCare.Voluntarios.Entity.Voluntario;
import com.example.MiauCare.Voluntarios.Repository.VoluntarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    private final AdministradorRepository administradorRepository;
    private final VeterinarioRepository veterinarioRepository;
    private final VoluntarioRepository voluntarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(AdministradorRepository administradorRepository,
                           VeterinarioRepository veterinarioRepository,
                           VoluntarioRepository voluntarioRepository,
                           PasswordEncoder passwordEncoder,
                           JwtUtil jwtUtil) {
        this.administradorRepository = administradorRepository;
        this.veterinarioRepository = veterinarioRepository;
        this.voluntarioRepository = voluntarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        Optional<Administrador> admin = administradorRepository.findByCorreo(request.getCorreo());
        if (admin.isPresent() && passwordEncoder.matches(request.getContrasena(), admin.get().getContrasena())) {
            Administrador a = admin.get();
            String token = jwtUtil.generarToken(a.getId(), a.getCorreo(), "ADMINISTRADOR");
            return new LoginResponse(token, a.getId(), a.getNombreCompleto(), "ADMINISTRADOR", a.getDebeCambiarContrasena());
        }

        Optional<Veterinario> vet = veterinarioRepository.findByCorreo(request.getCorreo());
        if (vet.isPresent() && passwordEncoder.matches(request.getContrasena(), vet.get().getContrasena())) {
            Veterinario v = vet.get();
            if (v.getEstado() == EstadoVeterinario.PENDIENTE) {
                throw new IllegalStateException("Tu solicitud aún está pendiente de aprobación.");
            }
            if (v.getEstado() == EstadoVeterinario.RECHAZADO) {
                throw new IllegalStateException("Tu solicitud fue rechazada.");
            }
            if (v.getEstado() == EstadoVeterinario.INACTIVO) {
                throw new IllegalStateException("Tu cuenta está inactiva.");
            }
            String token = jwtUtil.generarToken(v.getId(), v.getCorreo(), "VETERINARIO");
            return new LoginResponse(token, v.getId(), v.getNombreCompleto(), "VETERINARIO", false);
        }

        Optional<Voluntario> vol = voluntarioRepository.findByCorreo(request.getCorreo());
        if (vol.isPresent() && passwordEncoder.matches(request.getContrasena(), vol.get().getContrasena())) {
            Voluntario vo = vol.get();
            String token = jwtUtil.generarToken(vo.getId(), vo.getCorreo(), "VOLUNTARIO");
            return new LoginResponse(token, vo.getId(), vo.getNombreCompleto(), "VOLUNTARIO", false);
        }

        throw new CredencialesInvalidasException("Correo o contraseña incorrectos.");
    }

    // Pantalla "Restablecer contraseña": busca el correo en cualquiera de los 3 roles y actualiza el hash
    @Override
    public void restablecerContrasena(RestablecerContrasenaRequest request) {
        if (request.correo() == null || request.correo().isBlank()) {
            throw new IllegalArgumentException("Debes ingresar tu correo electrónico.");
        }
        if (request.nuevaContrasena() == null || request.nuevaContrasena().length() < 6) {
            throw new IllegalArgumentException("La nueva contraseña debe tener al menos 6 caracteres.");
        }

        String correo = request.correo().trim();
        String hashNuevo = passwordEncoder.encode(request.nuevaContrasena());

        Optional<Voluntario> vol = voluntarioRepository.findByCorreo(correo);
        if (vol.isPresent()) {
            Voluntario vo = vol.get();
            vo.setContrasena(hashNuevo);
            voluntarioRepository.save(vo);
            return;
        }

        Optional<Veterinario> vet = veterinarioRepository.findByCorreo(correo);
        if (vet.isPresent()) {
            Veterinario v = vet.get();
            v.setContrasena(hashNuevo);
            veterinarioRepository.save(v);
            return;
        }

        Optional<Administrador> admin = administradorRepository.findByCorreo(correo);
        if (admin.isPresent()) {
            Administrador a = admin.get();
            a.setContrasena(hashNuevo);
            a.setDebeCambiarContrasena(false);
            administradorRepository.save(a);
            return;
        }

        throw new RuntimeException("No existe ninguna cuenta registrada con el correo: " + correo);
    }
}