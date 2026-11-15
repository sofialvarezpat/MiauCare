package com.example.MiauCare.Administradores.Service;

import com.example.MiauCare.Administradores.Entity.ParametrosSistema;
import com.example.MiauCare.Administradores.Repository.ParametrosSistemaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class ParametrosSistemaServiceImpl implements ParametrosSistemaService {

    private static final Long ID_UNICO = 1L;
    private static final ZoneId ZONA = ZoneId.of("America/Bogota");

    private final ParametrosSistemaRepository parametrosRepository;
    private final AuditoriaService auditoriaService;

    public ParametrosSistemaServiceImpl(ParametrosSistemaRepository parametrosRepository,
                                        AuditoriaService auditoriaService) {
        this.parametrosRepository = parametrosRepository;
        this.auditoriaService = auditoriaService;
    }

    @Override
    public ParametrosSistema obtener() {
        return parametrosRepository.findById(ID_UNICO)
                .orElseGet(() -> {
                    ParametrosSistema inicial = new ParametrosSistema();
                    inicial.setId(ID_UNICO);
                    inicial.setDiasAusenciaAlerta(7);
                    inicial.setReportesCriticosConsecutivos(2);
                    inicial.setTiempoMaximoRespuestaHoras(24);
                    inicial.setUltimaActualizacion(LocalDateTime.now(ZONA));
                    inicial.setActualizadoPor("SISTEMA");
                    return parametrosRepository.save(inicial);
                });
    }

    @Override
    public ParametrosSistema actualizar(ParametrosSistema nuevosDatos, String correoAdmin) {
        if (nuevosDatos.getDiasAusenciaAlerta() == null || nuevosDatos.getDiasAusenciaAlerta() < 1) {
            throw new IllegalArgumentException("Los días sin avistamiento (X) deben ser al menos 1.");
        }
        if (nuevosDatos.getReportesCriticosConsecutivos() == null || nuevosDatos.getReportesCriticosConsecutivos() < 1) {
            throw new IllegalArgumentException("Los avistamientos críticos consecutivos (N) deben ser al menos 1.");
        }
        if (nuevosDatos.getTiempoMaximoRespuestaHoras() == null || nuevosDatos.getTiempoMaximoRespuestaHoras() < 1) {
            throw new IllegalArgumentException("El tiempo máximo de respuesta (T) debe ser al menos 1 hora.");
        }

        ParametrosSistema existente = obtener();
        existente.setDiasAusenciaAlerta(nuevosDatos.getDiasAusenciaAlerta());
        existente.setReportesCriticosConsecutivos(nuevosDatos.getReportesCriticosConsecutivos());
        existente.setTiempoMaximoRespuestaHoras(nuevosDatos.getTiempoMaximoRespuestaHoras());
        existente.setUltimaActualizacion(LocalDateTime.now(ZONA));
        existente.setActualizadoPor(correoAdmin);

        ParametrosSistema guardado = parametrosRepository.save(existente);

        // Registra automáticamente el cambio en la tabla de auditoría
        auditoriaService.registrarAccion(
                "ACTUALIZAR_PARAMETROS",
                "Actualizó parámetros del sistema (X=" + guardado.getDiasAusenciaAlerta()
                        + ", N=" + guardado.getReportesCriticosConsecutivos()
                        + ", T=" + guardado.getTiempoMaximoRespuestaHoras() + ")",
                correoAdmin
        );

        return guardado;
    }
}