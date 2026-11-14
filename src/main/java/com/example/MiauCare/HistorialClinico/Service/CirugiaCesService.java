package com.example.MiauCare.HistorialClinico.Service;

import com.example.MiauCare.HistorialClinico.Entity.CirugiaCes;

import java.time.LocalDate;
import java.util.List;

public interface CirugiaCesService {
    List<CirugiaCes> listarPorFecha(LocalDate fecha);
    List<CirugiaCes> listarPorVeterinario(String correoVeterinario, LocalDate fecha);
    List<CirugiaCes> listarPorGato(Long gatoId);
    CirugiaCes obtenerPorId(Long id);
    CirugiaCes programar(CirugiaCes cirugia, String correoVeterinario);
    CirugiaCes iniciar(Long id, String correoVeterinario);
    CirugiaCes completar(Long id, String correoVeterinario, String resultado, boolean darDeAlta);
    CirugiaCes cancelar(Long id, String correoVeterinario);
}