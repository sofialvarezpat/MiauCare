package com.example.MiauCare.Auth;

public class LoginResponse {
    private String token;
    private Long id;
    private String nombreCompleto;
    private String rol;
    private boolean debeCambiarContrasena;

    public LoginResponse(String token, Long id, String nombreCompleto, String rol, boolean debeCambiarContrasena) {
        this.token = token;
        this.id = id;
        this.nombreCompleto = nombreCompleto;
        this.rol = rol;
        this.debeCambiarContrasena = debeCambiarContrasena;
    }

    public String getToken() { return token; }
    public Long getId() { return id; }
    public String getNombreCompleto() { return nombreCompleto; }
    public String getRol() { return rol; }
    public boolean isDebeCambiarContrasena() { return debeCambiarContrasena; }
}