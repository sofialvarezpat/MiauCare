package com.example.MiauCare.Catalogos.Service;

import com.example.MiauCare.Catalogos.Entity.Especialidad;
import com.example.MiauCare.Catalogos.Repository.EspecialidadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EspecialidadServiceImpl implements EspecialidadService {

    private final EspecialidadRepository especialidadRepository;

    public EspecialidadServiceImpl(EspecialidadRepository especialidadRepository) {
        this.especialidadRepository = especialidadRepository;
    }

    @Override
    public List<Especialidad> listarTodas() {
        return especialidadRepository.findAll();
    }

    @Override
    public Especialidad obtenerPorId(Long id) {
        return especialidadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Especialidad no encontrada con id: " + id));
    }

    @Override
    public Especialidad crear(Especialidad especialidad) {
        return especialidadRepository.save(especialidad);
    }

    @Override
    public Especialidad actualizar(Long id, Especialidad datosNuevos) {
        Especialidad existente = obtenerPorId(id);
        existente.setNombre(datosNuevos.getNombre());
        return especialidadRepository.save(existente);
    }

    @Override
    public void eliminar(Long id) {
        especialidadRepository.deleteById(id);
    }
}