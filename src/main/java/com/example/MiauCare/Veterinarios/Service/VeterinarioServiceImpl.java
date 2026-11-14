package com.example.MiauCare.Veterinarios.Service;

import com.example.MiauCare.Veterinarios.Entity.Veterinario;
import com.example.MiauCare.Veterinarios.Repository.VeterinarioRepository;
import com.example.MiauCare.Veterinarios.enums.EstadoVeterinario;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VeterinarioServiceImpl implements VeterinarioService {

    private final VeterinarioRepository veterinarioRepository;
    private final PasswordEncoder passwordEncoder;

    public VeterinarioServiceImpl(VeterinarioRepository veterinarioRepository, PasswordEncoder passwordEncoder) {
        this.veterinarioRepository = veterinarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<Veterinario> listarTodos() {
        return veterinarioRepository.findAll();
    }

    @Override
    public List<Veterinario> listarPendientes() {
        return veterinarioRepository.findByEstado(EstadoVeterinario.PENDIENTE);
    }

    @Override
    public Veterinario obtenerPorId(Long id) {
        return veterinarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Veterinario no encontrado con id: " + id));
    }

    @Override
    public Veterinario enviarSolicitud(Veterinario veterinario) {
        veterinario.setContrasena(passwordEncoder.encode(veterinario.getContrasena()));
        veterinario.setEstado(EstadoVeterinario.PENDIENTE);
        return veterinarioRepository.save(veterinario);
    }

    @Override
    public Veterinario aprobar(Long id) {
        Veterinario existente = obtenerPorId(id);
        existente.setEstado(EstadoVeterinario.ACTIVO);
        return veterinarioRepository.save(existente);
    }

    @Override
    public Veterinario rechazar(Long id) {
        Veterinario existente = obtenerPorId(id);
        existente.setEstado(EstadoVeterinario.RECHAZADO);
        return veterinarioRepository.save(existente);
    }

    @Override
    public void cambiarEstado(Long id, boolean activo) {
        Veterinario existente = obtenerPorId(id);
        existente.setEstado(activo ? EstadoVeterinario.ACTIVO : EstadoVeterinario.INACTIVO);
        veterinarioRepository.save(existente);
    }

    @Override
    public Veterinario actualizar(Long id, Veterinario datosNuevos) {
        Veterinario existente = obtenerPorId(id);
        existente.setNombreCompleto(datosNuevos.getNombreCompleto());
        existente.setCorreo(datosNuevos.getCorreo());
        existente.setEspecialidad(datosNuevos.getEspecialidad());
        return veterinarioRepository.save(existente);
    }

    @Override
    public void cambiarDisponibilidad(Long id, boolean disponible) {
        Veterinario existente = obtenerPorId(id);
        existente.setDisponibleEmergencias(disponible);
        veterinarioRepository.save(existente);
    }
}
