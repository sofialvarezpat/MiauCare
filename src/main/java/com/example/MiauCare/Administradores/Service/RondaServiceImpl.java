package com.example.MiauCare.Administradores.Service;

import com.example.MiauCare.Administradores.Entity.Ronda;
import com.example.MiauCare.Administradores.Repository.RondaRepository;
import com.example.MiauCare.Administradores.enums.EstadoRonda;
import com.example.MiauCare.Exception.ConflictoHorarioException;
import com.example.MiauCare.HistorialClinico.Repository.CirugiaCesRepository;
import com.example.MiauCare.HistorialClinico.enums.EstadoCirugia;
import com.example.MiauCare.Voluntarios.Entity.Voluntario;
import com.example.MiauCare.Voluntarios.Repository.VoluntarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class RondaServiceImpl implements RondaService {

    // Estados que bloquean el horario (RN-01)
    private static final List<EstadoRonda> ESTADOS_ACTIVOS =
            List.of(EstadoRonda.PROGRAMADA, EstadoRonda.EN_CURSO);
    private static final List<EstadoCirugia> CIRUGIAS_ACTIVAS =
            List.of(EstadoCirugia.PROGRAMADA, EstadoCirugia.EN_CURSO);

    private final RondaRepository rondaRepository;
    private final VoluntarioRepository voluntarioRepository;
    private final CirugiaCesRepository cirugiaRepository;

    public RondaServiceImpl(RondaRepository rondaRepository,
                            VoluntarioRepository voluntarioRepository,
                            CirugiaCesRepository cirugiaRepository) {
        this.rondaRepository = rondaRepository;
        this.voluntarioRepository = voluntarioRepository;
        this.cirugiaRepository = cirugiaRepository;
    }

    @Override
    public List<Ronda> listarTodas() {
        return rondaRepository.findAll();
    }

    @Override
    public List<Ronda> listarPorFecha(LocalDate fecha) {
        return rondaRepository.findByFechaOrderByHoraInicioAsc(fecha);
    }

    @Override
    public Ronda obtenerPorId(Long id) {
        return rondaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ronda no encontrada con id: " + id));
    }

    @Override
    public Ronda crear(Ronda ronda) {
        validarDatos(ronda);
        validarSinCruce(ronda, null);
        validarVeterinarioLibre(ronda);
        ronda.setId(null); // evita que un id enviado en el JSON sobrescriba otra ronda
        ronda.setEstado(EstadoRonda.PROGRAMADA);
        return rondaRepository.save(ronda);
    }

    @Override
    public Ronda editar(Long id, Ronda datosNuevos) {
        Ronda existente = obtenerPorId(id);

        if (existente.getEstado() != EstadoRonda.PROGRAMADA) {
            throw new IllegalStateException("Solo se pueden editar rondas programadas.");
        }

        validarDatos(datosNuevos);
        validarSinCruce(datosNuevos, id);
        validarVeterinarioLibre(datosNuevos);

        existente.setZona(datosNuevos.getZona());
        existente.setFecha(datosNuevos.getFecha());
        existente.setHoraInicio(datosNuevos.getHoraInicio());
        existente.setHoraFin(datosNuevos.getHoraFin());
        existente.setVeterinario(datosNuevos.getVeterinario());
        existente.setVoluntarios(datosNuevos.getVoluntarios());
        return rondaRepository.save(existente);
    }

    @Override
    public void cancelar(Long id) {
        Ronda ronda = obtenerPorId(id);

        if (ronda.getEstado() != EstadoRonda.PROGRAMADA) {
            throw new IllegalStateException("Solo se pueden cancelar rondas programadas.");
        }

        ronda.setEstado(EstadoRonda.CANCELADA);
        rondaRepository.save(ronda);
    }

    @Override
    public Ronda iniciar(Long id) {
        Ronda ronda = obtenerPorId(id);

        if (ronda.getEstado() != EstadoRonda.PROGRAMADA) {
            throw new IllegalStateException("Solo se puede iniciar una ronda programada.");
        }

        ronda.setEstado(EstadoRonda.EN_CURSO);
        return rondaRepository.save(ronda);
    }

    @Override
    public Ronda finalizar(Long id) {
        Ronda ronda = obtenerPorId(id);

        if (ronda.getEstado() != EstadoRonda.EN_CURSO) {
            throw new IllegalStateException("Solo se puede finalizar una ronda que está en curso.");
        }

        ronda.setEstado(EstadoRonda.COMPLETADA);
        return rondaRepository.save(ronda);
    }

    @Override
    public Ronda asistir(Long rondaId, String correoVoluntario) {
        Ronda ronda = obtenerPorId(rondaId);

        if (ronda.getEstado() != EstadoRonda.PROGRAMADA) {
            throw new IllegalStateException("Solo puedes inscribirte a rondas programadas.");
        }

        Voluntario voluntario = voluntarioRepository.findByCorreo(correoVoluntario)
                .orElseThrow(() -> new RuntimeException("Voluntario no encontrado"));

        boolean yaInscrito = ronda.getVoluntarios().stream()
                .anyMatch(v -> v.getId().equals(voluntario.getId()));

        if (yaInscrito) {
            throw new IllegalStateException("Ya te inscribiste en esta ronda.");
        }

        ronda.getVoluntarios().add(voluntario);
        return rondaRepository.save(ronda);
    }

    // Validaciones básicas (devuelven 400 por el manejador de IllegalArgumentException)
    private void validarDatos(Ronda ronda) {
        if (ronda.getZona() == null) {
            throw new IllegalArgumentException("Debes seleccionar una zona.");
        }
        if (ronda.getFecha() == null || ronda.getHoraInicio() == null || ronda.getHoraFin() == null) {
            throw new IllegalArgumentException("La fecha y el horario son obligatorios.");
        }
        if (!ronda.getHoraFin().isAfter(ronda.getHoraInicio())) {
            throw new IllegalArgumentException("La hora de fin debe ser posterior a la hora de inicio.");
        }
        if (ronda.getVeterinario() == null) {
            throw new IllegalArgumentException("La ronda debe contar con al menos un veterinario (RN08).");
        }
    }

    // RN-01: exclusividad horaria global (devuelve 409 con la ronda que choca)
    private void validarSinCruce(Ronda ronda, Long idExcluir) {
        Long excluir = (idExcluir == null) ? -1L : idExcluir;

        var cruces = rondaRepository.buscarCruces(
                ronda.getFecha(),
                ronda.getHoraInicio(),
                ronda.getHoraFin(),
                excluir,
                ESTADOS_ACTIVOS);

        if (!cruces.isEmpty()) {
            throw new ConflictoHorarioException(
                    "Ya existe una ronda programada o en curso dentro de este horario (Código 409).",
                    cruces.get(0));
        }
    }

    // El veterinario no puede estar en una ronda y en una cirugía CES al mismo tiempo
    private void validarVeterinarioLibre(Ronda ronda) {
        var cirugias = cirugiaRepository.buscarCrucesVeterinario(
                ronda.getVeterinario().getId(),
                ronda.getFecha(),
                ronda.getHoraInicio(),
                ronda.getHoraFin(),
                CIRUGIAS_ACTIVAS);

        if (!cirugias.isEmpty()) {
            throw new IllegalStateException("El veterinario ya tiene una cirugía CES programada en ese horario.");
        }
    }
}