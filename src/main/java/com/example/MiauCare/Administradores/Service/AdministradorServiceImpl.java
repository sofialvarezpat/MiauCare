package com.example.MiauCare.Administradores.Service;

import com.example.MiauCare.Administradores.Entity.Administrador;
import com.example.MiauCare.Administradores.Repository.AdministradorRepository;
import com.example.MiauCare.Administradores.enums.EstadoAdministrador;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdministradorServiceImpl implements AdministradorService {

    private final AdministradorRepository administradorRepository;
    private final PasswordEncoder passwordEncoder;

    public AdministradorServiceImpl(AdministradorRepository administradorRepository, PasswordEncoder passwordEncoder) {
        this.administradorRepository = administradorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<Administrador> listarTodos() {
        return administradorRepository.findAll();
    }

    @Override
    public Administrador obtenerPorId(Long id) {
        return administradorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Administrador no encontrado con id: " + id));
    }

    @Override
    public Administrador crear(Administrador administrador, String contrasenaTemporal) {
        administrador.setContrasena(passwordEncoder.encode(contrasenaTemporal));
        administrador.setEstado(EstadoAdministrador.ACTIVO);
        administrador.setDebeCambiarContrasena(true);
        return administradorRepository.save(administrador);
    }

    @Override
    public Administrador actualizarPerfil(Long id, Administrador datosNuevos) {
        Administrador existente = obtenerPorId(id);
        existente.setNombreCompleto(datosNuevos.getNombreCompleto());
        existente.setCorreo(datosNuevos.getCorreo());
        return administradorRepository.save(existente);
    }

    @Override
    public void cambiarContrasena(Long id, String nuevaContrasena) {
        Administrador existente = obtenerPorId(id);
        existente.setContrasena(passwordEncoder.encode(nuevaContrasena));
        existente.setDebeCambiarContrasena(false);
        administradorRepository.save(existente);
    }

    @Override
    public void cambiarEstado(Long id, boolean activo) {
        Administrador existente = obtenerPorId(id);
        existente.setEstado(activo ? EstadoAdministrador.ACTIVO : EstadoAdministrador.INACTIVO);
        administradorRepository.save(existente);
    }
}
