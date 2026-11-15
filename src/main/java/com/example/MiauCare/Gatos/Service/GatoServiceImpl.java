package com.example.MiauCare.Gatos.Service;

import com.example.MiauCare.Administradores.enums.EstadoAprobacionGato;
import com.example.MiauCare.Gatos.Entity.Gato;
import com.example.MiauCare.Gatos.Repository.GatoRepository;
import com.example.MiauCare.Gatos.enums.EstadoVidaGato;
import com.example.MiauCare.Veterinarios.Entity.Veterinario;
import com.example.MiauCare.Veterinarios.Repository.VeterinarioRepository;
import com.example.MiauCare.Veterinarios.enums.EstadoVeterinario;
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
        return gatoRepository.findByEstadoAprobacion(EstadoAprobacionGato.APROBADO);
    }

    @Override
    public List<Gato> listarPendientes() {
        return gatoRepository.findByEstadoAprobacion(EstadoAprobacionGato.PENDIENTE);
    }

    @Override
    public List<Gato> buscarPorNombre(String nombre) {
        return gatoRepository.findByNombreContainingIgnoreCaseAndEstadoAprobacion(
                nombre, EstadoAprobacionGato.APROBADO);
    }

    @Override
    public List<Gato> listarPorZona(Long zonaId) {
        return gatoRepository.findByZonaIdAndEstadoAprobacion(
                zonaId, EstadoAprobacionGato.APROBADO);
    }

    @Override
    public List<Gato> listarPorVeterinario(Long veterinarioId) {
        return gatoRepository.findByVeterinarioResponsableId(veterinarioId);
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
    public Gato crear(Gato gato, boolean esAdministrador) {
        gato.setId(null);
        gato.setVeterinarioResponsable(null);
        if (esAdministrador) {
            gato.setEstadoAprobacion(EstadoAprobacionGato.APROBADO);
        } else {
            gato.setEstadoAprobacion(EstadoAprobacionGato.PENDIENTE);
        }
        return gatoRepository.save(gato);
    }

    @Override
    public Gato aprobar(Long id) {
        Gato existente = obtenerPorId(id);
        if (existente.getEstadoAprobacion() != EstadoAprobacionGato.PENDIENTE) {
            throw new IllegalStateException("Solo se pueden aprobar registros de gatos en estado pendiente.");
        }
        existente.setEstadoAprobacion(EstadoAprobacionGato.APROBADO);
        return gatoRepository.save(existente);
    }

    @Override
    public Gato rechazar(Long id) {
        Gato existente = obtenerPorId(id);
        if (existente.getEstadoAprobacion() != EstadoAprobacionGato.PENDIENTE) {
            throw new IllegalStateException("Solo se pueden rechazar registros de gatos en estado pendiente.");
        }
        existente.setEstadoAprobacion(EstadoAprobacionGato.RECHAZADO);
        return gatoRepository.save(existente);
    }

    // Actualiza datos maestros del gato incluyendo condicionesEspeciales (RF-05)
    @Override
    public Gato actualizar(Long id, Gato datosNuevos) {
        Gato existente = obtenerPorId(id);
        validarNoFallecido(existente);
        existente.setNombre(datosNuevos.getNombre());
        existente.setSexo(datosNuevos.getSexo());
        existente.setEdadEstimada(datosNuevos.getEdadEstimada());
        existente.setRaza(datosNuevos.getRaza());
        existente.setPeso(datosNuevos.getPeso());
        existente.setCondicionesEspeciales(datosNuevos.getCondicionesEspeciales());
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
        validarNoFallecido(gato);
        Veterinario veterinario = obtenerVeterinarioActivo(veterinarioId);
        gato.setVeterinarioResponsable(veterinario);
        return gatoRepository.save(gato);
    }

    @Override
    public List<Gato> reasignarPacientesDeVeterinario(Long veterinarioOrigenId, Long veterinarioDestinoId) {
        if (veterinarioOrigenId.equals(veterinarioDestinoId)) {
            throw new IllegalArgumentException("El veterinario destino debe ser distinto al veterinario actual.");
        }
        Veterinario destino = obtenerVeterinarioActivo(veterinarioDestinoId);
        List<Gato> pacientes = gatoRepository.findByVeterinarioResponsableId(veterinarioOrigenId);

        for (Gato gato : pacientes) {
            if (gato.getEstadoVida() != EstadoVidaGato.FALLECIDO) {
                gato.setVeterinarioResponsable(destino);
            }
        }
        return gatoRepository.saveAll(pacientes);
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

    private Veterinario obtenerVeterinarioActivo(Long veterinarioId) {
        Veterinario veterinario = veterinarioRepository.findById(veterinarioId)
                .orElseThrow(() -> new RuntimeException("Veterinario no encontrado con id: " + veterinarioId));
        if (veterinario.getEstado() != EstadoVeterinario.ACTIVO) {
            throw new IllegalStateException("Solo se pueden asignar pacientes a un veterinario en estado ACTIVO.");
        }
        return veterinario;
    }
}