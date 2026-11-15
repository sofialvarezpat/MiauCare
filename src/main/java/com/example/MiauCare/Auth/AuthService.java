package com.example.MiauCare.Auth;

public interface AuthService {

    // Valida credenciales en las 3 tablas de usuarios y devuelve el token JWT
    LoginResponse login(LoginRequest request);

    // Restablece la contraseña del usuario (Voluntario, Veterinario o Administrador) buscando su correo
    void restablecerContrasena(RestablecerContrasenaRequest request);
}