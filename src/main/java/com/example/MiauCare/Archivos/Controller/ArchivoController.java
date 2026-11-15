package com.example.MiauCare.Archivos.Controller;

import com.example.MiauCare.Archivos.Service.ArchivoService;
import com.example.MiauCare.DTO.ArchivoResponseDto;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/archivos")
public class ArchivoController {

    private final ArchivoService archivoService;

    public ArchivoController(ArchivoService archivoService) {
        this.archivoService = archivoService;
    }

    // Endpoint multipart/form-data para subir fotos desde el teléfono (cámara o galería)
    // Parámetros form-data: "archivo" (File) y opcionalmente "carpeta" ("gatos" o "cedulas")
    @PostMapping(value = "/subir", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ArchivoResponseDto> subir(
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam(value = "carpeta", defaultValue = "gatos") String carpeta) {
        return ResponseEntity.ok(archivoService.subirImagen(archivo, carpeta));
    }
}