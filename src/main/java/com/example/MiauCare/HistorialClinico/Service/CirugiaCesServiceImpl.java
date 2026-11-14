package com.example.MiauCare.HistorialClinico.Service;

import com.example.MiauCare.Administradores.Repository.RondaRepository;
import com.example.MiauCare.Administradores.enums.EstadoAprobacionGato;
import com.example.MiauCare.Administradores.enums.EstadoRonda;
import com.example.MiauCare.Exception.SinPermisosException;
import com.example.MiauCare.Gatos.Entity.Gato;
import com.example.MiauCare.Gatos.Repository.GatoRepository;
import com.example.MiauCare.Gatos.enums.EstadoInternacion;
import com.example.MiauCare.Gatos.enums.EstadoVidaGato;
import com.example.MiauCare.HistorialClinico.Entity.CirugiaCes;
import com.example.MiauCare.HistorialClinico.Entity.HistorialClinico;
import com.example.MiauCare.HistorialClinico.Repository.CirugiaCesRepository;
import com.example.MiauCare.HistorialClinico.Repository.HistorialClinicoRepository;
import com.example.MiauCare.HistorialClinico.enums.EstadoCirugia;
import com.example.MiauCare.HistorialClinico.enums.TipoHistorial;
import com.example.MiauCare.Veterinarios.Entity.Veterinario;
import com.example.MiauCare.Veterinarios.Repository.VeterinarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
public class CirugiaCesServiceImpl implements CirugiaCesService {

    private static final List<EstadoCirugia> ESTADOS_ACTIVOS =
            List.of(EstadoCirugia.PROGRAMADA, EstadoCirugia.EN_CURSO);

    private static final List<EstadoRonda> RONDAS_ACTIVAS =
            List.of(EstadoRonda.PROGRAMADA, EstadoRonda.EN_CURSO);

    private static final ZoneId ZONA = ZoneId.of("America/Bogota");

    private final CirugiaCesRepository cirugiaRepository;
    private final GatoRepository gatoRepository;
    private final VeterinarioRepository veterinarioRepository;
    private final RondaRepository rondaRepository;
    private final HistorialClinicoRepository historialRepository;

    public CirugiaCesServiceImpl(CirugiaCesRepository cirugiaRepository,
                                 GatoRepository gatoRepository,
                                 VeterinarioRepository veterinarioRepository,
                                 RondaRepository rondaRepository,
                                 HistorialClinicoRepository historialRepository) {
        this.cirugiaRepository = cirugiaRepository;
        this.gatoRepository = gatoRepository;
        this.veterinarioRepository = veterinarioRepository;
        this.rondaRepository = rondaRepository;
        this.historialRepository = historialRepository;
    }

    @Override
    public List<CirugiaCes> listarPorFecha(LocalDate fecha) {
        if (fecha == null) {
            return cirugiaRepository.findAll();
        }
        return cirugiaRepository.findByFechaOrderByHoraInicioAsc(fecha);
    }

    @Override
    public List<CirugiaCes> listarPorVeterinario(String correoVeterinario, LocalDate fecha) {
        Veterinario veterinario = obtenerVeterinarioPorCorreo(correoVeterinario);
        if (fecha == null) {
            return cirugiaRepository.findByVeterinarioIdOrderByFechaDescHoraInicioAsc(veterinario.getId());
        }
        return cirugiaRepository.findByVeterinarioIdAndFechaOrderByHoraInicioAsc(veterinario.getId(), fecha);
    }

    @Override
    public List<CirugiaCes> listarPorGato(Long gatoId) {
        return cirugiaRepository.findByGatoIdOrderByFechaDesc(gatoId);
    }

    @Override
    public CirugiaCes obtenerPorId(Long id) {
        return cirugiaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cirugía no encontrada con id: " + id));
    }

    @Override
    public CirugiaCes programar(CirugiaCes cirugia, String correoVeterinario) {
        Veterinario veterinario = obtenerVeterinarioPorCorreo(correoVeterinario);
        validarDatos(cirugia);

        Gato gato = gatoRepository.findById(cirugia.getGato().getId())
                .orElseThrow(() -> new RuntimeException("Gato no encontrado con id: " + cirugia.getGato().getId()));

        validarNoFallecido(gato);

        if (gato.getEstadoAprobacion() != null && gato.getEstadoAprobacion() != EstadoAprobacionGato.APROBADO) {
            throw new IllegalStateException("El registro de este gato aún no ha sido aprobado por un administrador.");
        }

        if (gato.getEstadoVida() != EstadoVidaGato.ACTIVO) {
            throw new IllegalStateException("Solo se puede programar una cirugía CES a gatos con estado de vida Activo.");
        }

        if (Boolean.TRUE.equals(gato.getEsterilizado())) {
            throw new IllegalStateException("Este gato ya está esterilizado, no puede programarse otra cirugía CES (RN-06).");
        }

        if (gato.getVeterinarioResponsable() != null
                && !gato.getVeterinarioResponsable().getId().equals(veterinario.getId())) {
            throw new SinPermisosException(
                    "Este paciente ya tiene un veterinario responsable asignado y no eres delegado autorizado.");
        }

        if (cirugiaRepository.existsByGatoIdAndEstadoIn(gato.getId(), ESTADOS_ACTIVOS)) {
            throw new IllegalStateException("Este gato ya tiene una cirugía CES pendiente.");
        }

        cirugia.setQuirofano(cirugia.getQuirofano().trim());
        validarSinCruces(cirugia, veterinario.getId());

        cirugia.setId(null);
        cirugia.setGato(gato);
        cirugia.setVeterinario(veterinario);
        cirugia.setEstado(EstadoCirugia.PROGRAMADA);
        cirugia.setResultado(null);

        CirugiaCes guardada = cirugiaRepository.save(cirugia);

        if (gato.getVeterinarioResponsable() == null) {
            gato.setVeterinarioResponsable(veterinario);
            gatoRepository.save(gato);
        }

        return guardada;
    }

    @Override
    public CirugiaCes iniciar(Long id, String correoVeterinario) {
        CirugiaCes cirugia = obtenerPorId(id);
        validarPropietario(cirugia, correoVeterinario);

        Gato gato = cirugia.getGato();
        validarNoFallecido(gato);

        if (cirugia.getEstado() != EstadoCirugia.PROGRAMADA) {
            throw new IllegalStateException("Solo se puede iniciar una cirugía programada.");
        }

        if (Boolean.TRUE.equals(gato.getEsterilizado())) {
            throw new IllegalStateException("Este gato ya está esterilizado, no puede realizarse otra cirugía CES (RN-06).");
        }

        gato.setEstadoInternacion(EstadoInternacion.HOSPITALIZADO);
        gatoRepository.save(gato);

        cirugia.setEstado(EstadoCirugia.EN_CURSO);
        return cirugiaRepository.save(cirugia);
    }

    @Override
    public CirugiaCes completar(Long id, String correoVeterinario, String resultado, boolean darDeAlta) {
        CirugiaCes cirugia = obtenerPorId(id);
        validarPropietario(cirugia, correoVeterinario);

        Gato gato = cirugia.getGato();
        validarNoFallecido(gato);

        if (cirugia.getEstado() != EstadoCirugia.EN_CURSO) {
            throw new IllegalStateException("Solo se puede completar una cirugía que está en curso.");
        }

        if (resultado == null || resultado.isBlank()) {
            throw new IllegalArgumentException("Debes registrar el resultado de la cirugía.");
        }

        gato.setEsterilizado(true);
        gato.setEstadoInternacion(darDeAlta ? EstadoInternacion.EN_CALLE : EstadoInternacion.HOSPITALIZADO);
        gatoRepository.save(gato);

        HistorialClinico entrada = new HistorialClinico();
        entrada.setGato(gato);
        entrada.setTipo(TipoHistorial.PROCEDIMIENTO);
        entrada.setDescripcion("Cirugía de esterilización (CES)");
        entrada.setNotas(resultado.trim());
        entrada.setVeterinario(cirugia.getVeterinario());
        entrada.setFecha(LocalDate.now(ZONA));
        historialRepository.save(entrada);

        cirugia.setEstado(EstadoCirugia.REALIZADA);
        cirugia.setResultado(resultado.trim());
        return cirugiaRepository.save(cirugia);
    }

    @Override
    public CirugiaCes cancelar(Long id, String correoVeterinario) {
        CirugiaCes cirugia = obtenerPorId(id);
        validarPropietario(cirugia, correoVeterinario);

        if (cirugia.getEstado() != EstadoCirugia.PROGRAMADA) {
            throw new IllegalStateException("Solo se pueden cancelar cirugías programadas.");
        }

        cirugia.setEstado(EstadoCirugia.CANCELADA);
        return cirugiaRepository.save(cirugia);
    }

    private void validarDatos(CirugiaCes cirugia) {
        if (cirugia.getGato() == null || cirugia.getGato().getId() == null) {
            throw new IllegalArgumentException("Debes seleccionar un gato.");
        }
        if (cirugia.getFecha() == null || cirugia.getHoraInicio() == null || cirugia.getHoraFin() == null) {
            throw new IllegalArgumentException("La fecha y el horario son obligatorios.");
        }
        if (!cirugia.getHoraFin().isAfter(cirugia.getHoraInicio())) {
            throw new IllegalArgumentException("La hora de fin debe ser posterior a la hora de inicio.");
        }
        if (cirugia.getQuirofano() == null || cirugia.getQuirofano().isBlank()) {
            throw new IllegalArgumentException("Debes indicar el quirófano.");
        }
        if (cirugia.getFecha().isBefore(LocalDate.now(ZONA))) {
            throw new IllegalArgumentException("No se puede programar una cirugía en una fecha pasada.");
        }
    }

    private void validarSinCruces(CirugiaCes cirugia, Long veterinarioId) {
        if (!cirugiaRepository.buscarCrucesVeterinario(veterinarioId, cirugia.getFecha(),
                cirugia.getHoraInicio(), cirugia.getHoraFin(), ESTADOS_ACTIVOS).isEmpty()) {
            throw new IllegalStateException("Ya tienes una cirugía CES programada en ese horario.");
        }
        if (!rondaRepository.buscarCrucesVeterinario(veterinarioId, cirugia.getFecha(),
                cirugia.getHoraInicio(), cirugia.getHoraFin(), RONDAS_ACTIVAS).isEmpty()) {
            throw new IllegalStateException("Ya tienes una ronda de revisión asignada en ese horario.");
        }
        if (!cirugiaRepository.buscarCrucesQuirofano(cirugia.getQuirofano(), cirugia.getFecha(),
                cirugia.getHoraInicio(), cirugia.getHoraFin(), ESTADOS_ACTIVOS).isEmpty()) {
            throw new IllegalStateException("El quirófano seleccionado ya está ocupado en ese horario.");
        }
    }

    private void validarPropietario(CirugiaCes cirugia, String correoVeterinario) {
        Veterinario veterinario = obtenerVeterinarioPorCorreo(correoVeterinario);
        if (!cirugia.getVeterinario().getId().equals(veterinario.getId())) {
            throw new SinPermisosException("Solo el veterinario que programó la cirugía puede gestionarla.");
        }
    }

    private void validarNoFallecido(Gato gato) {
        if (gato.getEstadoVida() == EstadoVidaGato.FALLECIDO) {
            throw new IllegalStateException("No se pueden realizar acciones sobre un gato marcado como fallecido.");
        }
    }

    private Veterinario obtenerVeterinarioPorCorreo(String correo) {
        return veterinarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Veterinario no encontrado"));
    }
}