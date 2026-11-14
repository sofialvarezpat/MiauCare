package com.example.MiauCare.Gatos.Service;

import com.example.MiauCare.Gatos.Entity.SolicitudGato;
import com.example.MiauCare.Gatos.enums.TipoSolicitudGato;

import java.util.List;

public interface SolicitudGatoService {
    List<SolicitudGato> listarPendientes();
    SolicitudGato crear(Long gatoId, Long voluntarioId, TipoSolicitudGato tipo);
    SolicitudGato aceptar(Long id);
    SolicitudGato rechazar(Long id);
}