package com.prepnovis.backend.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.prepnovis.backend.security.CustomAccessDeniedHandler;
import com.prepnovis.backend.security.CustomAuthenticationEntryPoint;
import com.prepnovis.backend.security.CustomUserDetailsService;
import com.prepnovis.backend.security.JwtAuthenticationFilter;
import com.prepnovis.backend.security.RateLimitFilter;

@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final RateLimitFilter rateLimitFilter;

    @Value("${app.cors.allowed-origins}")
    private String allowedOrigins;

    public SecurityConfig(
        CustomUserDetailsService customUserDetailsService,
        JwtAuthenticationFilter jwtAuthenticationFilter,
        RateLimitFilter rateLimitFilter,
        CustomAuthenticationEntryPoint customAuthenticationEntryPoint,
        CustomAccessDeniedHandler customAccessDeniedHandler) {

    this.customUserDetailsService = customUserDetailsService;
    this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    this.rateLimitFilter = rateLimitFilter;
    this.customAuthenticationEntryPoint =
            customAuthenticationEntryPoint;
    this.customAccessDeniedHandler =
            customAccessDeniedHandler;
}

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider authenticationProvider =
                new DaoAuthenticationProvider(customUserDetailsService);

        authenticationProvider.setPasswordEncoder(passwordEncoder());

        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration)
            throws Exception {

        return configuration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(
                List.of(allowedOrigins.split(","))
        );

        configuration.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "OPTIONS"
                )
        );

        configuration.setAllowedHeaders(
                List.of(
                        "Authorization",
                        "Content-Type"
                )
        );

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                configuration
        );

        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http)
            throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)

                .cors(Customizer.withDefaults())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(
                                customAuthenticationEntryPoint
                        )
                        .accessDeniedHandler(
                                customAccessDeniedHandler
                        )
                )

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/v1/auth/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()
                        .anyRequest().authenticated()
                )

                .authenticationProvider(
                        authenticationProvider()
                )

                .addFilterBefore(
                  rateLimitFilter,
                  UsernamePasswordAuthenticationFilter.class
                )

                .addFilterBefore(
                  jwtAuthenticationFilter,
                  UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}