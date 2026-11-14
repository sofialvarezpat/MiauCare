package com.example.MiauCare.config;

import com.example.MiauCare.Alertas.Service.MotorAlertasService;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class TareasProgramadas {

    private final MotorAlertasService motorAlertasService;

    public TareasProgramadas(MotorAlertasService motorAlertasService) {
        this.motorAlertasService = motorAlertasService;
    }

    @Scheduled(cron = "0 0 6 * * *") // Todos los días a las 6:00 AM
    public void revisarAusenciasProlongadas() {
        motorAlertasService.evaluarAusenciaProlongada();
    }
}