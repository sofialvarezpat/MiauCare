package com.example.MiauCare.Auth;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    // Clave secreta para firmar los tokens. En producción debe venir de variable de entorno.
    private final SecretKey secretKey = Keys.hmacShaKeyFor(
            "MiauCareClaveSecretaSuperLargaParaFirmarTokensJWT2026".getBytes()
    );

    private final long expiracionMs = 1000 * 60 * 60 * 24; // 24 horas

    public String generarToken(Long id, String correo, String rol) {
        return Jwts.builder()
                .subject(correo)
                .claim("id", id)
                .claim("rol", rol)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiracionMs))
                .signWith(secretKey)
                .compact();
    }

    public io.jsonwebtoken.Claims validarYObtenerClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
