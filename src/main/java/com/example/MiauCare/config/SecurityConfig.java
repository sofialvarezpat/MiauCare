package com.example.MiauCare.config;

import com.example.MiauCare.Auth.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

                        // Catálogos: lectura libre (todos los roles los necesitan para formularios)
                        .requestMatchers("GET", "/api/zonas/**", "/api/diagnosticos/**", "/api/especialidades/**").permitAll()

                        // Gatos: lectura para los 3 roles autenticados; escritura solo Vet/Admin
                        .requestMatchers("GET", "/api/gatos/**").authenticated()
                        .requestMatchers("POST", "/api/gatos/**").hasAnyRole("VETERINARIO", "ADMINISTRADOR")
                        .requestMatchers("PUT", "/api/gatos/**").hasAnyRole("VETERINARIO", "ADMINISTRADOR")
                        .requestMatchers("PATCH", "/api/gatos/**").hasAnyRole("VETERINARIO", "ADMINISTRADOR")
                        .requestMatchers("DELETE", "/api/gatos/**").hasRole("ADMINISTRADOR")

                        // Historial clínico: lectura para todos, escritura solo Veterinario
                        .requestMatchers("GET", "/api/historial-clinico/**").authenticated()
                        .requestMatchers("POST", "/api/historial-clinico/**").hasRole("VETERINARIO")

                        // Administración de usuarios: solo Administrador
                        .requestMatchers("/api/administradores/**").hasRole("ADMINISTRADOR")
                        .requestMatchers("/api/veterinarios/pendientes").hasRole("ADMINISTRADOR")
                        .requestMatchers("/api/veterinarios/*/aprobar").hasRole("ADMINISTRADOR")
                        .requestMatchers("/api/veterinarios/*/rechazar").hasRole("ADMINISTRADOR")
                        .requestMatchers("/api/veterinarios/*/estado").hasRole("ADMINISTRADOR")
                        .requestMatchers("/api/voluntarios/*/estado").hasRole("ADMINISTRADOR")

                        // Catálogos: escritura (crear/editar/eliminar) solo Administrador
                        .requestMatchers("POST", "/api/zonas/**", "/api/diagnosticos/**", "/api/especialidades/**").hasRole("ADMINISTRADOR")
                        .requestMatchers("PUT", "/api/zonas/**", "/api/diagnosticos/**", "/api/especialidades/**").hasRole("ADMINISTRADOR")
                        .requestMatchers("DELETE", "/api/zonas/**", "/api/diagnosticos/**", "/api/especialidades/**").hasRole("ADMINISTRADOR")

                        // Cualquier otra ruta: requiere estar autenticado, sea quien sea
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

