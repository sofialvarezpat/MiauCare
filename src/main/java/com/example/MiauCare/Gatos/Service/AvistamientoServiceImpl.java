package com.example.MiauCare.Gatos.Service;

import com.example.MiauCare.Alertas.Service.MotorAlertasService;
import com.example.MiauCare.Gatos.Entity.Avistamiento;
import com.example.MiauCare.Gatos.Repository.AvistamientoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AvistamientoServiceImpl implements AvistamientoService {

    private final AvistamientoRepository avistamientoRepository;
    private final MotorAlertasService motorAlertasService;

    public AvistamientoServiceImpl(AvistamientoRepository avistamientoRepository,
                                   MotorAlertasService motorAlertasService) {
        this.avistamientoRepository = avistamientoRepository;
        this.motorAlertasService = motorAlertasService;
    }

    @Override
    public List<Avistamiento> listarPorGato(Long gatoId) {
        return avistamientoRepository.findByGatoIdOrderByFechaDesc(gatoId);
    }

    @Override
    public List<Avistamiento> listarPorZona(Long zonaId) {
        return avistamientoRepository.findByZonaIdOrderByFechaDesc(zonaId);
    }

    @Override
    public Avistamiento registrarAvistamientoNormal(Avistamiento avistamiento) {
        avistamiento.setEsGatoNoCensado(false);
        avistamiento.setFecha(LocalDateTime.now());
        Avistamiento guardado = avistamientoRepository.save(avistamiento);

        motorAlertasService.evaluarAvistamientoCritico(guardado.getGato().getId());

        return guardado;
    }

    @Override
    public Avistamiento registrarGatoNoCensado(Avistamiento avistamiento) {
        avistamiento.setGato(null);
        avistamiento.setEsGatoNoCensado(true);
        avistamiento.setComio(null);
        avistamiento.setFecha(LocalDateTime.now());
        return avistamientoRepository.save(avistamiento);
    }
}