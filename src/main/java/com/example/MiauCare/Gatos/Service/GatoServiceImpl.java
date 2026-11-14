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

    // Devuelve solo los gatos aprobados para el censo general
    @Override
    public List<Gato> listarTodos() {
        return gatoRepository.findByEstadoAprobacion(EstadoAprobacionGato.APROBADO);
    }

    // Devuelve los gatos registrados por veterinarios que esperan aprobación del admin
    @Override
    public List<Gato> listarPendientes() {
        return gatoRepository.findByEstadoAprobacion(EstadoAprobacionGato.PENDIENTE);
    }

    // Busca por nombre dentro de los gatos aprobados
    @Override
    public List<Gato> buscarPorNombre(String nombre) {
        return gatoRepository.findByNombreContainingIgnoreCaseAndEstadoAprobacion(
                nombre, EstadoAprobacionGato.APROBADO);
    }

    // Filtra por zona dentro de los gatos aprobados
    @Override
    public List<Gato> listarPorZona(Long zonaId) {
        return gatoRepository.findByZonaIdAndEstadoAprobacion(
                zonaId, EstadoAprobacionGato.APROBADO);
    }

    // NUEVO: Devuelve la lista de pacientes a cargo de un veterinario ("Tus pacientes" / "Directorio médico")
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

    // Crea el gato sin veterinario responsable inicial (RN-02) y define si entra APROBADO o PENDIENTE
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

    // Cambia el estado de aprobación de PENDIENTE a APROBADO
    @Override
    public Gato aprobar(Long id) {
        Gato existente = obtenerPorId(id);
        if (existente.getEstadoAprobacion() != EstadoAprobacionGato.PENDIENTE) {
            throw new IllegalStateException("Solo se pueden aprobar registros de gatos en estado pendiente.");
        }
        existente.setEstadoAprobacion(EstadoAprobacionGato.APROBADO);
        return gatoRepository.save(existente);
    }

    // Cambia el estado de aprobación de PENDIENTE a RECHAZADO
    @Override
    public Gato rechazar(Long id) {
        Gato existente = obtenerPorId(id);
        if (existente.getEstadoAprobacion() != EstadoAprobacionGato.PENDIENTE) {
            throw new IllegalStateException("Solo se pueden rechazar registros de gatos en estado pendiente.");
        }
        existente.setEstadoAprobacion(EstadoAprobacionGato.RECHAZADO);
        return gatoRepository.save(existente);
    }

    // Actualiza datos generales del gato, validando primero que no esté fallecido
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

    // Cambia el estado de vida (sin bloqueo de fallecido para permitir revertir errores)
    @Override
    public Gato cambiarEstadoVida(Long id, EstadoVidaGato nuevoEstado) {
        Gato existente = obtenerPorId(id);
        existente.setEstadoVida(nuevoEstado);
        return gatoRepository.save(existente);
    }

    // Marca al gato como esterilizado (RN-06)
    @Override
    public Gato marcarEsterilizado(Long id) {
        Gato existente = obtenerPorId(id);
        validarNoFallecido(existente);
        existente.setEsterilizado(true);
        return gatoRepository.save(existente);
    }

    // Asigna o reasigna un veterinario activo a un gato individual (usado por Admin en "Directorio médico")
    @Override
    public Gato asignarVeterinarioResponsable(Long gatoId, Long veterinarioId) {
        Gato gato = obtenerPorId(gatoId);
        validarNoFallecido(gato);
        Veterinario veterinario = obtenerVeterinarioActivo(veterinarioId);
        gato.setVeterinarioResponsable(veterinario);
        return gatoRepository.save(gato);
    }

    // NUEVO: Mueve todos los gatos de un veterinario origen a un veterinario destino (modal "Reasignar pacientes")
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

    // Bloquea cualquier modificación si el gato está marcado como fallecido
    private void validarNoFallecido(Gato gato) {
        if (gato.getEstadoVida() == EstadoVidaGato.FALLECIDO) {
            throw new IllegalStateException("No se pueden realizar acciones sobre un gato marcado como fallecido.");
        }
    }

    // Verifica que el veterinario exista y tenga estado ACTIVO antes de asignarle pacientes
    private Veterinario obtenerVeterinarioActivo(Long veterinarioId) {
        Veterinario veterinario = veterinarioRepository.findById(veterinarioId)
                .orElseThrow(() -> new RuntimeException("Veterinario no encontrado con id: " + veterinarioId));
        if (veterinario.getEstado() != EstadoVeterinario.ACTIVO) {
            throw new IllegalStateException("Solo se pueden asignar pacientes a un veterinario en estado ACTIVO.");
        }
        return veterinario;
    }
}