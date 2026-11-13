package com.example.MiauCare.Repository;

import com.example.MiauCare.Entity.Especialidad;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EspecialidadRepository extends
        JpaRepository<Especialidad, Long> {
}