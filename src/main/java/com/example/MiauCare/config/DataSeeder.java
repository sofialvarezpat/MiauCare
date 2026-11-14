package com.example.MiauCare.config;

import com.example.MiauCare.Administradores.Entity.Administrador;
import com.example.MiauCare.Administradores.Repository.AdministradorRepository;
import com.example.MiauCare.Administradores.enums.EstadoAdministrador;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final AdministradorRepository administradorRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(AdministradorRepository administradorRepository, PasswordEncoder passwordEncoder) {
        this.administradorRepository = administradorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        String correoFijo = "adminmiaucare@administrador.com";

        boolean existe = administradorRepository.findByCorreo(correoFijo).isPresent();

        if (!existe) {
            Administrador admin = new Administrador();
            admin.setNombreCompleto("Admin MiauCare");
            admin.setCorreo(correoFijo);
            admin.setContrasena(passwordEncoder.encode("Admin1234"));
            admin.setEstado(EstadoAdministrador.ACTIVO);
            admin.setDebeCambiarContrasena(false);

            administradorRepository.save(admin);
            System.out.println("Administrador fijo creado: " + correoFijo);
        } else {
            System.out.println("Administrador fijo ya existe, no se crea de nuevo.");
        }
    }
}