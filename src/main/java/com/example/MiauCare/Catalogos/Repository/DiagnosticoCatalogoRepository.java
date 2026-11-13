package com.example.MiauCare.Catalogos.Repository;

import com.example.MiauCare.Catalogos.Entity.DiagnosticoCatalogo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiagnosticoCatalogoRepository extends
        JpaRepository<DiagnosticoCatalogo, Long> {
}