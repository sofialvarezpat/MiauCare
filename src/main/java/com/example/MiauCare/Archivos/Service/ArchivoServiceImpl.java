package com.example.MiauCare.Archivos.Service;

import com.example.MiauCare.DTO.ArchivoResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

@Service
public class ArchivoServiceImpl implements ArchivoService {

    // Lee la URL del proyecto desde application.properties
    @Value("${supabase.url}")
    private String supabaseUrl;

    // Lee la clave secreta de Supabase desde la variable de entorno SUPABASE_KEY
    @Value("${supabase.key:}")
    private String supabaseKey;

    // Lee el nombre del bucket configurado en Supabase Storage
    @Value("${supabase.bucket:miaucare-archivos}")
    private String bucket;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Override
    public ArchivoResponseDto subirImagen(MultipartFile archivo, String carpeta) {
        // 1. Validaciones básicas del archivo recibido
        if (archivo == null || archivo.isEmpty()) {
            throw new IllegalArgumentException("Debes seleccionar una imagen para subir.");
        }

        String contentType = archivo.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Solo se permiten archivos de imagen (JPG, PNG, WEBP).");
        }

        if (supabaseKey == null || supabaseKey.isBlank()) {
            throw new IllegalStateException("Falta configurar la variable de entorno SUPABASE_KEY en el servidor.");
        }

        // 2. Genera un nombre único con UUID para que dos fotos con el mismo nombre no se sobrescriban
        String extension = obtenerExtension(archivo.getOriginalFilename());
        String carpetaLimpia = (carpeta == null || carpeta.isBlank()) ? "general" : carpeta.trim().toLowerCase();
        String rutaArchivo = carpetaLimpia + "/" + UUID.randomUUID() + extension;

        try {
            // 3. Construye la petición HTTP POST hacia la API de Supabase Storage
            String urlSubida = supabaseUrl + "/storage/v1/object/" + bucket + "/" + rutaArchivo;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlSubida))
                    .header("Authorization", "Bearer " + supabaseKey)
                    .header("apikey", supabaseKey)
                    .header("Content-Type", contentType)
                    .POST(HttpRequest.BodyPublishers.ofByteArray(archivo.getBytes()))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200 && response.statusCode() != 201) {
                throw new IllegalStateException("Error al subir imagen a Supabase Storage: " + response.body());
            }

            // 4. Construye y devuelve la URL pública permanente para guardarla en la base de datos
            String urlPublica = supabaseUrl + "/storage/v1/object/public/" + bucket + "/" + rutaArchivo;
            return new ArchivoResponseDto(rutaArchivo, urlPublica);

        } catch (IllegalArgumentException | IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("No se pudo procesar la subida de la imagen: " + e.getMessage());
        }
    }

    // Extrae la extensión original del archivo (.jpg, .png, etc.)
    private String obtenerExtension(String nombreOriginal) {
        if (nombreOriginal != null && nombreOriginal.contains(".")) {
            return nombreOriginal.substring(nombreOriginal.lastIndexOf(".")).toLowerCase();
        }
        return ".jpg";
    }
}
