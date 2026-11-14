package com.example.MiauCare.Catalogos.Service;

import com.example.MiauCare.Administradores.enums.EstadoAprobacionGato;
import com.example.MiauCare.Catalogos.Entity.Zona;
import com.example.MiauCare.Catalogos.Repository.ZonaRepository;
import com.example.MiauCare.DTO.ZonaResumenDto;
import com.example.MiauCare.Gatos.Repository.GatoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ZonaServiceImpl implements ZonaService {

    private final ZonaRepository zonaRepository;
    private final GatoRepository gatoRepository;

    public ZonaServiceImpl(ZonaRepository zonaRepository, GatoRepository gatoRepository) {
        this.zonaRepository = zonaRepository;
        this.gatoRepository = gatoRepository;
    }

    @Override
    public List<Zona> listarTodas() {
        return zonaRepository.findAll();
    }

    // Devuelve cada colonia junto con su cantidad de gatos censados para la pantalla "Colonias y zonas" del Admin
    @Override
    public List<ZonaResumenDto> listarConConteoGatos() {
        List<Zona> zonas = zonaRepository.findAll();
        List<ZonaResumenDto> respuesta = new ArrayList<>();

        for (Zona z : zonas) {
            int cantidadGatos = gatoRepository
                    .findByZonaIdAndEstadoAprobacion(z.getId(), EstadoAprobacionGato.APROBADO)
                    .size();

            respuesta.add(new ZonaResumenDto(
                    z.getId(),
                    z.getNombre(),
                    z.getZonaMacro(),
                    z.getPuntoAlimentacion(),
                    z.getHoraAlimentacion(),
                    z.getLatitud(),
                    z.getLongitud(),
                    cantidadGatos,
                    null,
                    null
            ));
        }
        return respuesta;
    }

    // RF-03: Ordena los puntos de alimentación trazando una ruta optimizada por cercanía (vecino más cercano)
    @Override
    public List<ZonaResumenDto> obtenerRutaAlimentacion(Double latitudOrigen, Double longitudOrigen, String zonaMacro) {
        List<Zona> candidatas = new ArrayList<>(zonaRepository.findAll());

        if (zonaMacro != null && !zonaMacro.isBlank()) {
            candidatas.removeIf(z -> z.getZonaMacro() == null || !z.getZonaMacro().equalsIgnoreCase(zonaMacro.trim()));
        }

        List<ZonaResumenDto> rutaOrdenada = new ArrayList<>();
        double latActual = (latitudOrigen != null) ? latitudOrigen : 4.6097;
        double lonActual = (longitudOrigen != null) ? longitudOrigen : -74.0817;
        int orden = 1;

        while (!candidatas.isEmpty()) {
            Zona masCercana = null;
            double menorDistanciaKm = Double.MAX_VALUE;

            for (Zona z : candidatas) {
                double dist = (z.getLatitud() != null && z.getLongitud() != null)
                        ? calcularDistanciaKm(latActual, lonActual, z.getLatitud(), z.getLongitud())
                        : 9999.0;
                if (dist < menorDistanciaKm) {
                    menorDistanciaKm = dist;
                    masCercana = z;
                }
            }

            candidatas.remove(masCercana);
            if (masCercana.getLatitud() != null && masCercana.getLongitud() != null) {
                latActual = masCercana.getLatitud();
                lonActual = masCercana.getLongitud();
            }

            int cantidadGatos = gatoRepository
                    .findByZonaIdAndEstadoAprobacion(masCercana.getId(), EstadoAprobacionGato.APROBADO)
                    .size();

            Double distanciaRedondeada = (menorDistanciaKm == 9999.0)
                    ? null
                    : Math.round(menorDistanciaKm * 100.0) / 100.0;

            rutaOrdenada.add(new ZonaResumenDto(
                    masCercana.getId(),
                    masCercana.getNombre(),
                    masCercana.getZonaMacro(),
                    masCercana.getPuntoAlimentacion(),
                    masCercana.getHoraAlimentacion(),
                    masCercana.getLatitud(),
                    masCercana.getLongitud(),
                    cantidadGatos,
                    orden++,
                    distanciaRedondeada
            ));
        }

        return rutaOrdenada;
    }

    @Override
    public Zona obtenerPorId(Long id) {
        return zonaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Zona no encontrada con id: " + id));
    }

    @Override
    public Zona crear(Zona zona) {
        zona.setId(null);
        return zonaRepository.save(zona);
    }

    @Override
    public Zona actualizar(Long id, Zona datosNuevos) {
        Zona existente = obtenerPorId(id);
        existente.setNombre(datosNuevos.getNombre());
        existente.setZonaMacro(datosNuevos.getZonaMacro());
        existente.setPuntoAlimentacion(datosNuevos.getPuntoAlimentacion());
        existente.setHoraAlimentacion(datosNuevos.getHoraAlimentacion());
        existente.setLatitud(datosNuevos.getLatitud());
        existente.setLongitud(datosNuevos.getLongitud());
        return zonaRepository.save(existente);
    }

    @Override
    public void eliminar(Long id) {
        zonaRepository.deleteById(id);
    }

    public static double calcularDistanciaKm(double lat1, double lon1, double lat2, double lon2) {
        final double RADIO_TIERRA_KM = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return RADIO_TIERRA_KM * c;
    }
}