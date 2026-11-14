package com.example.MiauCare.config;

import com.example.MiauCare.Auth.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> {})
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        // Público: login y registro/solicitud iniciales
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/voluntarios/registro").permitAll()
                        .requestMatchers("/api/veterinarios/solicitud").permitAll()

                        // Catálogos: lectura libre
                        .requestMatchers(HttpMethod.GET, "/api/zonas/**", "/api/diagnosticos/**", "/api/especialidades/**").permitAll()

                        // Catálogos: escritura solo Administrador
                        .requestMatchers(HttpMethod.POST, "/api/zonas/**", "/api/diagnosticos/**", "/api/especialidades/**").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.PUT, "/api/zonas/**", "/api/diagnosticos/**", "/api/especialidades/**").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/zonas/**", "/api/diagnosticos/**", "/api/especialidades/**").hasRole("ADMINISTRADOR")

                        // Gatos: lectura autenticada; escritura solo Vet/Admin
                        .requestMatchers(HttpMethod.GET, "/api/gatos/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/gatos/**").hasAnyRole("VETERINARIO", "ADMINISTRADOR")
                        .requestMatchers(HttpMethod.PUT, "/api/gatos/**").hasAnyRole("VETERINARIO", "ADMINISTRADOR")
                        .requestMatchers(HttpMethod.PATCH, "/api/gatos/**").hasAnyRole("VETERINARIO", "ADMINISTRADOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/gatos/**").hasRole("ADMINISTRADOR")

                        // Historial clínico: lectura autenticada; escritura solo Veterinario
                        .requestMatchers(HttpMethod.GET, "/api/historial-clinico/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/historial-clinico/**").hasRole("VETERINARIO")

                        // Administración de usuarios: solo Administrador
                        .requestMatchers("/api/administradores/**").hasRole("ADMINISTRADOR")
                        .requestMatchers("/api/veterinarios/pendientes").hasRole("ADMINISTRADOR")
                        .requestMatchers("/api/veterinarios/*/aprobar").hasRole("ADMINISTRADOR")
                        .requestMatchers("/api/veterinarios/*/rechazar").hasRole("ADMINISTRADOR")
                        .requestMatchers("/api/veterinarios/*/estado").hasRole("ADMINISTRADOR")
                        .requestMatchers("/api/voluntarios/*/estado").hasRole("ADMINISTRADOR")

                        // Avistamientos: solo Voluntario registra
                        .requestMatchers(HttpMethod.POST, "/api/avistamientos/**").hasRole("VOLUNTARIO")
                        .requestMatchers(HttpMethod.GET, "/api/avistamientos/**").authenticated()

                        // Solicitudes de gato: Voluntario crea, Admin aprueba/rechaza
                        .requestMatchers(HttpMethod.POST, "/api/solicitudes-gato").hasRole("VOLUNTARIO")
                        .requestMatchers("/api/solicitudes-gato/pendientes").hasRole("ADMINISTRADOR")
                        .requestMatchers("/api/solicitudes-gato/*/aceptar").hasRole("ADMINISTRADOR")
                        .requestMatchers("/api/solicitudes-gato/*/rechazar").hasRole("ADMINISTRADOR")

                        // Alertas médicas: solo Veterinario gestiona
                        .requestMatchers("/api/alertas-medicas/**").hasRole("VETERINARIO")

                        // Rondas (el orden importa: Spring aplica la primera regla que coincide)
                        .requestMatchers(HttpMethod.GET, "/api/rondas/**").hasAnyRole("ADMINISTRADOR", "VETERINARIO", "VOLUNTARIO")
                        .requestMatchers(HttpMethod.PATCH, "/api/rondas/*/asistir").hasRole("VOLUNTARIO")
                        .requestMatchers(HttpMethod.PATCH, "/api/rondas/*/iniciar", "/api/rondas/*/finalizar").hasAnyRole("VETERINARIO", "ADMINISTRADOR")
                        .requestMatchers("/api/rondas/**").hasRole("ADMINISTRADOR")

                        // Cirugías CES: lectura Veterinario y Admin; programar, iniciar, completar y cancelar solo Veterinario
                        .requestMatchers(HttpMethod.GET, "/api/cirugias-ces/**").hasAnyRole("VETERINARIO", "ADMINISTRADOR")
                        .requestMatchers("/api/cirugias-ces/**").hasRole("VETERINARIO")

                        // Cualquier otra ruta: solo necesita estar autenticado
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}