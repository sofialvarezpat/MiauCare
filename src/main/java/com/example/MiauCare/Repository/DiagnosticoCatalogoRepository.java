package com.example.MiauCare.Repository;

import com.example.MiauCare.Entity.DiagnosticoCatalogo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiagnosticoCatalogoRepository extends
        JpaRepository<DiagnosticoCatalogo, Long> {
}