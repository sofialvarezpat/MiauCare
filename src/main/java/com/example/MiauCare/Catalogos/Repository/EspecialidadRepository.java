package com.example.MiauCare.Catalogos.Repository;

import com.example.MiauCare.Catalogos.Entity.Especialidad;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EspecialidadRepository extends
        JpaRepository<Especialidad, Long> {
}