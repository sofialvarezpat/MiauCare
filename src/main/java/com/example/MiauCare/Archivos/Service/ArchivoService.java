package com.example.MiauCare.Archivos.Service;

import com.example.MiauCare.DTO.ArchivoResponseDto;
import org.springframework.web.multipart.MultipartFile;

public interface ArchivoService {

    // Sube una imagen a Supabase Storage dentro de la carpeta indicada (ej: "gatos" o "cedulas") y devuelve su URL pública
    ArchivoResponseDto subirImagen(MultipartFile archivo, String carpeta);
}
