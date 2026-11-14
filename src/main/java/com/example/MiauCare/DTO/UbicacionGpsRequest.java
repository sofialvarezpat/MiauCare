package com.example.MiauCare.DTO;

// DTO simple para recibir latitud y longitud sin usar Map
public record UbicacionGpsRequest(Double latitud, Double longitud) {
}