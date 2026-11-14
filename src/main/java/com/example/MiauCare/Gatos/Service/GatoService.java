package com.example.MiauCare.Gatos.Service;

import com.example.MiauCare.Gatos.Entity.Gato;
import com.example.MiauCare.Gatos.enums.EstadoVidaGato;

import java.util.List;

public interface GatoService {

    // Lista todos los gatos aprobados en el censo
    List<Gato> listarTodos();

    // Lista las solicitudes de gatos nuevos pendientes de aprobación por el Admin
    List<Gato> listarPendientes();

    // Busca gatos aprobados cuyo nombre contenga el texto buscado
    List<Gato> buscarPorNombre(String nombre);

    // Lista gatos aprobados pertenecientes a una colonia/zona
    List<Gato> listarPorZona(Long zonaId);

    // NUEVO: Lista los pacientes asignados a un veterinario específico
    List<Gato> listarPorVeterinario(Long veterinarioId);

    // Obtiene un gato por su ID de base de datos
    Gato obtenerPorId(Long id);

    // Obtiene un gato por su código MC-xxx
    Gato obtenerPorCodigo(String codigo);

    // Crea un gato (APROBADO si lo crea Admin, PENDIENTE si lo registra Veterinario)
    Gato crear(Gato gato, boolean esAdministrador);

    // Aprueba el registro de un gato nuevo (solo Admin)
    Gato aprobar(Long id);

    // Rechaza el registro de un gato nuevo (solo Admin)
    Gato rechazar(Long id);

    // Actualiza los datos maestros del gato (bloqueado si está FALLECIDO)
    Gato actualizar(Long id, Gato datosNuevos);

    // Cambia el estado de vida (permite revertir si se marcó fallecido por error)
    Gato cambiarEstadoVida(Long id, EstadoVidaGato nuevoEstado);

    // Marca al gato como esterilizado (RN-06)
    Gato marcarEsterilizado(Long id);

    // Asigna o reasigna el veterinario responsable de un gato puntual
    Gato asignarVeterinarioResponsable(Long gatoId, Long veterinarioId);

    // NUEVO: Reasigna en bloque todos los pacientes de un veterinario origen a otro veterinario destino
    List<Gato> reasignarPacientesDeVeterinario(Long veterinarioOrigenId, Long veterinarioDestinoId);

    // Elimina un gato del sistema (solo Admin)
    void eliminar(Long id);
}