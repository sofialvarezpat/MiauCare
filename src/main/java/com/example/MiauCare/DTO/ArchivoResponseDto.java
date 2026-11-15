package com.example.MiauCare.DTO;

// DTO que devuelve la URL pública de la imagen subida a Supabase Storage
public record ArchivoResponseDto(
        String nombreArchivo,
        String urlPublica
) {
}