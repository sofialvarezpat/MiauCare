package com.example.MiauCare.Gatos.Service;

import com.example.MiauCare.Gatos.Entity.Gato;
import com.example.MiauCare.Gatos.Repository.GatoRepository;
import com.example.MiauCare.Gatos.enums.EstadoVidaGato;
import com.example.MiauCare.Veterinarios.Entity.Veterinario;
import com.example.MiauCare.Veterinarios.Repository.VeterinarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GatoServiceImpl implements GatoService {

    private final GatoRepository gatoRepository;
    private final VeterinarioRepository veterinarioRepository;

    public GatoServiceImpl(GatoRepository gatoRepository, VeterinarioRepository veterinarioRepository) {
        this.gatoRepository = gatoRepository;
        this.veterinarioRepository = veterinarioRepository;
    }

    @Override
    public List<Gato> listarTodos() {
        return gatoRepository.findAll();
    }

    @Override
    public List<Gato> buscarPorNombre(String nombre) {
        return gatoRepository.findByNombreContainingIgnoreCase(nombre);
    }

    @Override
    public List<Gato> listarPorZona(Long zonaId) {
        return gatoRepository.findByZonaId(zonaId);
    }

    @Override
    public Gato obtenerPorId(Long id) {
        return gatoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Gato no encontrado con id: " + id));
    }

    @Override
    public Gato obtenerPorCodigo(String codigo) {
        return gatoRepository.findByCodigo(codigo)
                .orElseThrow(() -> new RuntimeException("Gato no encontrado con código: " + codigo));
    }

    @Override
    public Gato crear(Gato gato) {
        gato.setVeterinarioResponsable(null); // Se asigna solo desde el módulo de Alertas Médicas (RN-02)
        return gatoRepository.save(gato);
    }

    @Override
    public Gato actualizar(Long id, Gato datosNuevos) {
        Gato existente = obtenerPorId(id);
        validarNoFallecido(existente);
        existente.setNombre(datosNuevos.getNombre());
        existente.setSexo(datosNuevos.getSexo());
        existente.setEdadEstimada(datosNuevos.getEdadEstimada());
        existente.setRaza(datosNuevos.getRaza());
        existente.setPeso(datosNuevos.getPeso());
        existente.setZona(datosNuevos.getZona());
        existente.setEstadoInternacion(datosNuevos.getEstadoInternacion());
        existente.setFotoUrl(datosNuevos.getFotoUrl());
        return gatoRepository.save(existente);
    }

    @Override
    public Gato cambiarEstadoVida(Long id, EstadoVidaGato nuevoEstado) {
        Gato existente = obtenerPorId(id);
        existente.setEstadoVida(nuevoEstado);
        return gatoRepository.save(existente);
    }

    @Override
    public Gato marcarEsterilizado(Long id) {
        Gato existente = obtenerPorId(id);
        validarNoFallecido(existente);
        existente.setEsterilizado(true);
        return gatoRepository.save(existente);
    }

    @Override
    public Gato asignarVeterinarioResponsable(Long gatoId, Long veterinarioId) {
        Gato gato = obtenerPorId(gatoId);
        Veterinario veterinario = veterinarioRepository.findById(veterinarioId)
                .orElseThrow(() -> new RuntimeException("Veterinario no encontrado con id: " + veterinarioId));
        gato.setVeterinarioResponsable(veterinario);
        return gatoRepository.save(gato);
    }

    @Override
    public void eliminar(Long id) {
        gatoRepository.deleteById(id);
    }

    private void validarNoFallecido(Gato gato) {
        if (gato.getEstadoVida() == EstadoVidaGato.FALLECIDO) {
            throw new IllegalStateException("No se pueden realizar acciones sobre un gato marcado como fallecido.");
        }
    }
}


