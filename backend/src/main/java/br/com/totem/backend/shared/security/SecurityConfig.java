package br.com.totem.backend.shared.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**"
                        ).permitAll()

                        .requestMatchers("/api/auth/**").permitAll()

                        // Temporariamente público até criarmos autenticação de dispositivos.
                        .requestMatchers("/api/totem/**").permitAll()

                        .requestMatchers(
                                "/api/admin/restaurantes",
                                "/api/admin/restaurantes/**"
                        ).hasAuthority("SUPER_ADMIN")

                        .requestMatchers(
                                "/api/admin/categorias",
                                "/api/admin/categorias/**"
                        ).hasAnyAuthority("SUPER_ADMIN", "ADMIN_RESTAURANTE")

                        .requestMatchers(
                                "/api/admin/produtos",
                                "/api/admin/produtos/**"
                        ).hasAnyAuthority("SUPER_ADMIN", "ADMIN_RESTAURANTE")

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/admin/usuarios",
                                "/api/admin/usuarios/**"
                        ).hasAuthority("SUPER_ADMIN")

                        .requestMatchers(
                                "/api/caixa",
                                "/api/caixa/**"
                        ).hasAnyAuthority("SUPER_ADMIN", "OPERADOR_CAIXA")

                        .requestMatchers(
                                "/api/cozinha",
                                "/api/cozinha/**"
                        ).hasAnyAuthority("SUPER_ADMIN", "OPERADOR_COZINHA")

                        .requestMatchers(
                                "/api/pedidos",
                                "/api/pedidos/**"
                        ).hasAnyAuthority(
                                "SUPER_ADMIN",
                                "ADMIN_RESTAURANTE",
                                "OPERADOR_CAIXA",
                                "OPERADOR_COZINHA"
                        )

                        .anyRequest().authenticated()
                )
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                )
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}