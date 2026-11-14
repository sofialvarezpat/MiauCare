package com.example.MiauCare.Voluntarios.Service;

import com.example.MiauCare.Voluntarios.Entity.Voluntario;
import com.example.MiauCare.Voluntarios.Repository.VoluntarioRepository;
import com.example.MiauCare.Voluntarios.enums.EstadoVoluntario;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VoluntarioServiceImpl implements VoluntarioService {

    private final VoluntarioRepository voluntarioRepository;
    private final PasswordEncoder passwordEncoder;

    public VoluntarioServiceImpl(VoluntarioRepository voluntarioRepository, PasswordEncoder passwordEncoder) {
        this.voluntarioRepository = voluntarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<Voluntario> listarTodos() {
        return voluntarioRepository.findAll();
    }

    @Override
    public Voluntario obtenerPorId(Long id) {
        return voluntarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Voluntario no encontrado con id: " + id));
    }

    @Override
    public Voluntario registrar(Voluntario voluntario) {
        voluntario.setContrasena(passwordEncoder.encode(voluntario.getContrasena())); //PasswordEncoder, que nunca guarda la contraseña tal cual la escribe el usuario, sino un “hash” irreversible (ni tú ni nadie con acceso a la base de datos puede ver la contraseña real, solo verificar si una coincide)
        voluntario.setEstado(EstadoVoluntario.ACTIVO);
        voluntario.setRecibiendoAlertas(true);
        return voluntarioRepository.save(voluntario);
    }

    @Override
    public Voluntario actualizar(Long id, Voluntario datosNuevos) {
        Voluntario existente = obtenerPorId(id);
        existente.setNombreCompleto(datosNuevos.getNombreCompleto());
        existente.setCorreo(datosNuevos.getCorreo());
        existente.setZona(datosNuevos.getZona());
        existente.setRecibiendoAlertas(datosNuevos.getRecibiendoAlertas());
        return voluntarioRepository.save(existente);
    }

    @Override
    public void cambiarEstado(Long id, boolean activo) {
        Voluntario existente = obtenerPorId(id);
        existente.setEstado(activo ? EstadoVoluntario.ACTIVO : EstadoVoluntario.INACTIVO);
        voluntarioRepository.save(existente);
    }
}