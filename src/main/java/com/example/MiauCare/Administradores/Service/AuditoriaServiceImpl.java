package com.example.MiauCare.Administradores.Service;

import com.example.MiauCare.Administradores.Entity.RegistroAuditoria;
import com.example.MiauCare.Administradores.Repository.RegistroAuditoriaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
public class AuditoriaServiceImpl implements AuditoriaService {

    private static final ZoneId ZONA = ZoneId.of("America/Bogota");

    private final RegistroAuditoriaRepository auditoriaRepository;

    public AuditoriaServiceImpl(RegistroAuditoriaRepository auditoriaRepository) {
        this.auditoriaRepository = auditoriaRepository;
    }

    // Filtra la lista cronológica por tipo de acción, usuario responsable, texto de búsqueda o fecha
    @Override
    public List<RegistroAuditoria> consultar(String tipoAccion, String usuario, String buscar, LocalDate fecha) {
        List<RegistroAuditoria> todos = auditoriaRepository.findAllByOrderByFechaHoraDesc();
        List<RegistroAuditoria> filtrados = new ArrayList<>();

        for (RegistroAuditoria reg : todos) {
            // Filtro por tipo de acción (si se envió)
            if (tipoAccion != null && !tipoAccion.isBlank()
                    && !reg.getTipoAccion().equalsIgnoreCase(tipoAccion.trim())) {
                continue;
            }
            // Filtro por usuario responsable (si se envió)
            if (usuario != null && !usuario.isBlank()
                    && !reg.getUsuarioResponsable().toLowerCase().contains(usuario.trim().toLowerCase())) {
                continue;
            }
            // Buscador libre en la descripción o tipo de acción (si se envió)
            if (buscar != null && !buscar.isBlank()) {
                String termino = buscar.trim().toLowerCase();
                boolean coincideDesc = reg.getDescripcion() != null && reg.getDescripcion().toLowerCase().contains(termino);
                boolean coincideTipo = reg.getTipoAccion() != null && reg.getTipoAccion().toLowerCase().contains(termino);
                if (!coincideDesc && !coincideTipo) {
                    continue;
                }
            }
            // Filtro por fecha exacta (si se envió)
            if (fecha != null && (reg.getFechaHora() == null || !reg.getFechaHora().toLocalDate().equals(fecha))) {
                continue;
            }
            filtrados.add(reg);
        }

        return filtrados;
    }

    // Registra cualquier acción crítica con fecha/hora de Bogotá
    @Override
    public RegistroAuditoria registrarAccion(String tipoAccion, String descripcion, String usuarioResponsable) {
        RegistroAuditoria registro = new RegistroAuditoria();
        registro.setTipoAccion(tipoAccion != null ? tipoAccion.trim().toUpperCase() : "ACCION_CRITICA");
        registro.setDescripcion(descripcion != null ? descripcion.trim() : "");
        registro.setUsuarioResponsable(
                (usuarioResponsable != null && !usuarioResponsable.isBlank()) ? usuarioResponsable : "SISTEMA");
        registro.setFechaHora(LocalDateTime.now(ZONA));
        return auditoriaRepository.save(registro);
    }
}
