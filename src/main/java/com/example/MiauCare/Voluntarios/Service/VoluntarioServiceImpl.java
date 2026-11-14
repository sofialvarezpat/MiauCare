package com.example.MiauCare.Voluntarios.Service;

import com.example.MiauCare.Catalogos.Entity.Zona;
import com.example.MiauCare.Catalogos.Repository.ZonaRepository;
import com.example.MiauCare.Catalogos.Service.ZonaServiceImpl;
import com.example.MiauCare.Voluntarios.Entity.Voluntario;
import com.example.MiauCare.Voluntarios.Repository.VoluntarioRepository;
import com.example.MiauCare.Voluntarios.enums.EstadoVoluntario;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VoluntarioServiceImpl implements VoluntarioService {

    private final VoluntarioRepository voluntarioRepository;
    private final ZonaRepository zonaRepository;
    private final PasswordEncoder passwordEncoder;

    public VoluntarioServiceImpl(VoluntarioRepository voluntarioRepository,
                                 ZonaRepository zonaRepository,
                                 PasswordEncoder passwordEncoder) {
        this.voluntarioRepository = voluntarioRepository;
        this.zonaRepository = zonaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<Voluntario> listarTodos() {
        return voluntarioRepository.findAll();
    }

    @Override
    public Voluntario obtenerPorId(Long id) {
        return voluntarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Voluntario no encontrado con id: " + id));
    }

    // RF-01 y RN-07: Registra al voluntario y, si envió sus coordenadas GPS base, lo vincula con la colonia más cercana
    @Override
    public Voluntario registrar(Voluntario voluntario) {
        voluntario.setContrasena(passwordEncoder.encode(voluntario.getContrasena()));
        voluntario.setEstado(EstadoVoluntario.ACTIVO);
        voluntario.setRecibiendoAlertas(true);

        if (voluntario.getZona() == null
                && voluntario.getLatitudBase() != null
                && voluntario.getLongitudBase() != null) {
            Zona masCercana = buscarZonaMasCercana(voluntario.getLatitudBase(), voluntario.getLongitudBase());
            voluntario.setZona(masCercana);
        }

        return voluntarioRepository.save(voluntario);
    }

    // Actualiza los datos personales y de perfil del voluntario (RF-01)
    @Override
    public Voluntario actualizar(Long id, Voluntario datosNuevos) {
        Voluntario existente = obtenerPorId(id);
        existente.setNombreCompleto(datosNuevos.getNombreCompleto());
        existente.setCorreo(datosNuevos.getCorreo());
        existente.setTelefono(datosNuevos.getTelefono());
        existente.setDisponibilidad(datosNuevos.getDisponibilidad());
        if (datosNuevos.getLatitudBase() != null) {
            existente.setLatitudBase(datosNuevos.getLatitudBase());
        }
        if (datosNuevos.getLongitudBase() != null) {
            existente.setLongitudBase(datosNuevos.getLongitudBase());
        }
        if (datosNuevos.getZona() != null) {
            existente.setZona(datosNuevos.getZona());
        }
        if (datosNuevos.getRecibiendoAlertas() != null) {
            existente.setRecibiendoAlertas(datosNuevos.getRecibiendoAlertas());
        }
        return voluntarioRepository.save(existente);
    }

    @Override
    public void cambiarEstado(Long id, boolean activo) {
        Voluntario existente = obtenerPorId(id);
        existente.setEstado(activo ? EstadoVoluntario.ACTIVO : EstadoVoluntario.INACTIVO);
        voluntarioRepository.save(existente);
    }

    // RF-02 y RN-07: Botón "Usar mi ubicación GPS" -> guarda coordenadas y asigna la colonia más cercana
    @Override
    public Voluntario emparejarPorGps(Long voluntarioId, Double latitud, Double longitud) {
        if (latitud == null || longitud == null) {
            throw new IllegalArgumentException("Debes enviar latitud y longitud para el emparejamiento por GPS.");
        }
        Voluntario voluntario = obtenerPorId(voluntarioId);
        voluntario.setLatitudBase(latitud);
        voluntario.setLongitudBase(longitud);

        Zona masCercana = buscarZonaMasCercana(latitud, longitud);
        if (masCercana == null) {
            throw new IllegalStateException("No hay zonas registradas para realizar el emparejamiento.");
        }
        voluntario.setZona(masCercana);
        return voluntarioRepository.save(voluntario);
    }

    // Opción "Seleccionar zona manualmente" de la pantalla "Ubicar Zona"
    @Override
    public Voluntario asignarZonaManual(Long voluntarioId, Long zonaId) {
        if (zonaId == null) {
            throw new IllegalArgumentException("Debes enviar el zonaId.");
        }
        Voluntario voluntario = obtenerPorId(voluntarioId);
        Zona zona = zonaRepository.findById(zonaId)
                .orElseThrow(() -> new RuntimeException("Zona no encontrada con id: " + zonaId));
        voluntario.setZona(zona);
        return voluntarioRepository.save(voluntario);
    }

    // Calcula cuál de todas las colonias registradas está a menor distancia en km del voluntario
    private Zona buscarZonaMasCercana(double latVoluntario, double lonVoluntario) {
        List<Zona> zonas = zonaRepository.findAll();
        if (zonas.isEmpty()) {
            return null;
        }

        Zona mejorZona = zonas.get(0);
        double menorDistancia = Double.MAX_VALUE;

        for (Zona z : zonas) {
            if (z.getLatitud() != null && z.getLongitud() != null) {
                double dist = ZonaServiceImpl.calcularDistanciaKm(
                        latVoluntario, lonVoluntario, z.getLatitud(), z.getLongitud());
                if (dist < menorDistancia) {
                    menorDistancia = dist;
                    mejorZona = z;
                }
            }
        }
        return mejorZona;
    }
}