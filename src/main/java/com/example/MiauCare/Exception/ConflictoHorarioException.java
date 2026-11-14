package com.example.MiauCare.Exception;

import com.example.MiauCare.Administradores.Entity.Ronda;

public class ConflictoHorarioException extends RuntimeException {
    private final Ronda rondaExistente;

    public ConflictoHorarioException(String mensaje, Ronda rondaExistente) {
        super(mensaje);
        this.rondaExistente = rondaExistente;
    }

    public Ronda getRondaExistente() {
        return rondaExistente;
    }
}
