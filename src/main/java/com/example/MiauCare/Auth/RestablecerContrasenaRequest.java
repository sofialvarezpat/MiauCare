package com.example.MiauCare.Auth;

// DTO para la pantalla "¿Olvidó la contraseña? / Restablecer contraseña"
public record RestablecerContrasenaRequest(
        String correo,
        String nuevaContrasena
) {
}
