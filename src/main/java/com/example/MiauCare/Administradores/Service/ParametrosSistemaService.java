package com.example.MiauCare.Administradores.Service;

import com.example.MiauCare.Administradores.Entity.ParametrosSistema;

public interface ParametrosSistemaService {

    // Obtiene los parámetros actuales (si no existen aún en la BD, los inicializa con X=7, N=2, T=24)
    ParametrosSistema obtener();

    // Guarda los nuevos valores de X, N y T configurados por el Administrador
    ParametrosSistema actualizar(ParametrosSistema nuevosDatos, String correoAdmin);
}