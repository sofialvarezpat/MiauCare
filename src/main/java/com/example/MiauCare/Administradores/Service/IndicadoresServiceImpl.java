package com.example.MiauCare.Administradores.Service;

import com.example.MiauCare.Administradores.Entity.Ronda;
import com.example.MiauCare.Administradores.Repository.RondaRepository;
import com.example.MiauCare.Administradores.enums.EstadoAprobacionGato;
import com.example.MiauCare.Administradores.enums.EstadoRonda;
import com.example.MiauCare.Alertas.Entity.AlertaMedica;
import com.example.MiauCare.Alertas.Repository.AlertaMedicaRepository;
import com.example.MiauCare.Catalogos.Entity.Zona;
import com.example.MiauCare.Catalogos.Repository.ZonaRepository;
import com.example.MiauCare.Catalogos.enums.EstadoAlerta;
import com.example.MiauCare.DTO.IndicadorZonaDto;
import com.example.MiauCare.Gatos.Entity.Gato;
import com.example.MiauCare.Gatos.Repository.GatoRepository;
import com.example.MiauCare.Gatos.enums.EstadoInternacion;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class IndicadoresServiceImpl implements IndicadoresService {

    private final ZonaRepository zonaRepository;
    private final GatoRepository gatoRepository;
    private final AlertaMedicaRepository alertaRepository;
    private final RondaRepository rondaRepository;

    // Inyección por constructor de los repositorios necesarios para consolidar las métricas
    public IndicadoresServiceImpl(ZonaRepository zonaRepository,
                                  GatoRepository gatoRepository,
                                  AlertaMedicaRepository alertaRepository,
                                  RondaRepository rondaRepository) {
        this.zonaRepository = zonaRepository;
        this.gatoRepository = gatoRepository;
        this.alertaRepository = alertaRepository;
        this.rondaRepository = rondaRepository;
    }

    // Calcula las estadísticas de cada zona para las tarjetas y gráficos del mockup "Indicadores por Zona"
    @Override
    public List<IndicadorZonaDto> consultarIndicadores(Long zonaId) {
        List<Zona> zonas = new ArrayList<>();
        if (zonaId != null) {
            Zona unica = zonaRepository.findById(zonaId)
                    .orElseThrow(() -> new RuntimeException("Zona no encontrada con id: " + zonaId));
            zonas.add(unica);
        } else {
            zonas.addAll(zonaRepository.findAll());
        }

        List<AlertaMedica> todasLasAlertas = alertaRepository.findAll();
        List<Ronda> todasLasRondas = rondaRepository.findAll();

        List<IndicadorZonaDto> resultado = new ArrayList<>();

        for (Zona z : zonas) {
            // 1. Gatos aprobados en esta zona
            List<Gato> gatosZona = gatoRepository.findByZonaIdAndEstadoAprobacion(
                    z.getId(), EstadoAprobacionGato.APROBADO);

            int censados = gatosZona.size();
            int esterilizados = 0;
            int hospitalizados = 0;

            for (Gato g : gatosZona) {
                if (Boolean.TRUE.equals(g.getEsterilizado())) {
                    esterilizados++;
                }
                if (g.getEstadoInternacion() == EstadoInternacion.HOSPITALIZADO) {
                    hospitalizados++;
                }
            }

            // 2. Alertas médicas activas (NUEVA o EN_ATENCION) de gatos pertenecientes a esta zona
            int alertasActivas = 0;
            for (AlertaMedica a : todasLasAlertas) {
                boolean activa = (a.getEstado() == EstadoAlerta.NUEVA || a.getEstado() == EstadoAlerta.EN_ATENCION);
                boolean mismaZona = (a.getGato() != null
                        && a.getGato().getZona() != null
                        && a.getGato().getZona().getId().equals(z.getId()));
                if (activa && mismaZona) {
                    alertasActivas++;
                }
            }

            // 3. Rondas completadas en esta zona
            int rondasCompletadas = 0;
            for (Ronda r : todasLasRondas) {
                boolean completada = (r.getEstado() == EstadoRonda.COMPLETADA);
                boolean mismaZona = (r.getZona() != null && r.getZona().getId().equals(z.getId()));
                if (completada && mismaZona) {
                    rondasCompletadas++;
                }
            }

            resultado.add(new IndicadorZonaDto(
                    z.getId(),
                    z.getNombre(),
                    z.getZonaMacro(),
                    censados,
                    esterilizados,
                    hospitalizados,
                    alertasActivas,
                    rondasCompletadas
            ));
        }

        return resultado;
    }

    // Construye el archivo CSV separado por comas para el botón "Exportar CSV" del Administrador
    @Override
    public String generarCsv(Long zonaId) {
        List<IndicadorZonaDto> indicadores = consultarIndicadores(zonaId);
        StringBuilder sb = new StringBuilder();

        // Encabezado de las columnas del CSV
        sb.append("ID Zona,Colonia,Sector,Gatos Censados,Gatos Esterilizados,Gatos Hospitalizados,Alertas Activas,Rondas Completadas\n");

        // Filas con los datos de cada zona
        for (IndicadorZonaDto item : indicadores) {
            sb.append(item.zonaId()).append(",")
                    .append(escaparCsv(item.nombreZona())).append(",")
                    .append(escaparCsv(item.zonaMacro())).append(",")
                    .append(item.gatosCensados()).append(",")
                    .append(item.gatosEsterilizados()).append(",")
                    .append(item.gatosHospitalizados()).append(",")
                    .append(item.alertasActivas()).append(",")
                    .append(item.rondasCompletadas()).append("\n");
        }

        return sb.toString();
    }

    // Envuelve en comillas si un texto tiene comas para no descuadrar las columnas del CSV
    private String escaparCsv(String texto) {
        if (texto == null) {
            return "";
        }
        if (texto.contains(",") || texto.contains("\"")) {
            return "\"" + texto.replace("\"", "\"\"") + "\"";
        }
        return texto;
    }
}