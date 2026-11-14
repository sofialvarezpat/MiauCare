package com.example.MiauCare.Administradores.Controller;

import com.example.MiauCare.Administradores.Service.IndicadoresService;
import com.example.MiauCare.DTO.IndicadorZonaDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/indicadores")
public class IndicadoresController {

    private final IndicadoresService indicadoresService;

    public IndicadoresController(IndicadoresService indicadoresService) {
        this.indicadoresService = indicadoresService;
    }

    // GET /api/indicadores o GET /api/indicadores?zonaId=1 (Pantalla "Indicadores por Zona")
    @GetMapping
    public List<IndicadorZonaDto> consultar(@RequestParam(required = false) Long zonaId) {
        return indicadoresService.consultarIndicadores(zonaId);
    }

    // GET /api/indicadores/exportar-csv (Botón coral "Exportar CSV" de la pantalla del Admin)
    @GetMapping("/exportar-csv")
    public ResponseEntity<byte[]> exportarCsv(@RequestParam(required = false) Long zonaId) {
        String contenidoCsv = indicadoresService.generarCsv(zonaId);
        byte[] bytes = contenidoCsv.getBytes(StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"indicadores_zonas_miaucare.csv\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(bytes);
    }
}