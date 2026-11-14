package com.example.MiauCare.Catalogos.Service;

import com.example.MiauCare.Catalogos.Entity.Zona;
import com.example.MiauCare.Catalogos.Repository.ZonaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ZonaServiceImpl implements ZonaService {

    private final ZonaRepository zonaRepository;

    public ZonaServiceImpl(ZonaRepository zonaRepository) {
        this.zonaRepository = zonaRepository;
    }

    @Override
    public List<Zona> listarTodas() {
        return zonaRepository.findAll();
    }

    @Override
    public Zona obtenerPorId(Long id) {
        return zonaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Zona no encontrada con id: " + id));
    }

    @Override
    public Zona crear(Zona zona) {
        return zonaRepository.save(zona);
    }

    @Override
    public Zona actualizar(Long id, Zona datosNuevos) {
        Zona existente = obtenerPorId(id);
        existente.setNombre(datosNuevos.getNombre());
        existente.setPuntoAlimentacion(datosNuevos.getPuntoAlimentacion());
        existente.setHoraAlimentacion(datosNuevos.getHoraAlimentacion());
        return zonaRepository.save(existente);
    }

    @Override
    public void eliminar(Long id) {
        zonaRepository.deleteById(id);
    }
}