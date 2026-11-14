package com.example.MiauCare.Gatos.Service;

import com.example.MiauCare.Gatos.Entity.Gato;
import com.example.MiauCare.Gatos.Entity.SolicitudGato;
import com.example.MiauCare.Gatos.Repository.GatoRepository;
import com.example.MiauCare.Gatos.Repository.SolicitudGatoRepository;
import com.example.MiauCare.Gatos.enums.EstadoInternacion;
import com.example.MiauCare.Gatos.enums.EstadoSolicitud;
import com.example.MiauCare.Gatos.enums.EstadoVidaGato;
import com.example.MiauCare.Gatos.enums.TipoSolicitudGato;
import com.example.MiauCare.Voluntarios.Entity.Voluntario;
import com.example.MiauCare.Voluntarios.Repository.VoluntarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SolicitudGatoServiceImpl implements SolicitudGatoService {

    private final SolicitudGatoRepository solicitudRepository;
    private final GatoRepository gatoRepository;
    private final VoluntarioRepository voluntarioRepository;

    public SolicitudGatoServiceImpl(SolicitudGatoRepository solicitudRepository,
                                    GatoRepository gatoRepository,
                                    VoluntarioRepository voluntarioRepository) {
        this.solicitudRepository = solicitudRepository;
        this.gatoRepository = gatoRepository;
        this.voluntarioRepository = voluntarioRepository;
    }

    @Override
    public List<SolicitudGato> listarPendientes() {
        return solicitudRepository.findByEstado(EstadoSolicitud.PENDIENTE);
    }

    @Override
    public SolicitudGato crear(Long gatoId, Long voluntarioId, TipoSolicitudGato tipo) {
        Gato gato = gatoRepository.findById(gatoId)
                .orElseThrow(() -> new RuntimeException("Gato no encontrado con id: " + gatoId));
        Voluntario voluntario = voluntarioRepository.findById(voluntarioId)
                .orElseThrow(() -> new RuntimeException("Voluntario no encontrado con id: " + voluntarioId));

        SolicitudGato solicitud = new SolicitudGato();
        solicitud.setGato(gato);
        solicitud.setVoluntario(voluntario);
        solicitud.setTipo(tipo);
        solicitud.setEstado(EstadoSolicitud.PENDIENTE);
        solicitud.setFecha(LocalDateTime.now());

        return solicitudRepository.save(solicitud);
    }

    @Override
    public SolicitudGato aceptar(Long id) {
        SolicitudGato solicitud = obtenerPorId(id);
        solicitud.setEstado(EstadoSolicitud.ACEPTADA);

        Gato gato = solicitud.getGato();
        if (solicitud.getTipo() == TipoSolicitudGato.FALLECIMIENTO) {
            gato.setEstadoVida(EstadoVidaGato.FALLECIDO);
        } else if (solicitud.getTipo() == TipoSolicitudGato.INGRESO_HOSPITALIZACION) {
            gato.setEstadoInternacion(EstadoInternacion.HOSPITALIZADO);
        }
        gatoRepository.save(gato);

        return solicitudRepository.save(solicitud);
    }

    @Override
    public SolicitudGato rechazar(Long id) {
        SolicitudGato solicitud = obtenerPorId(id);
        solicitud.setEstado(EstadoSolicitud.RECHAZADA);
        return solicitudRepository.save(solicitud);
    }

    private SolicitudGato obtenerPorId(Long id) {
        return solicitudRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada con id: " + id));
    }
}