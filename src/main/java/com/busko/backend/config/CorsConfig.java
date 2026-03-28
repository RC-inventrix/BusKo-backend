package com.busko.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * Global CORS configuration for all REST endpoints.
 *
 * <p>Allows all origins, headers, and methods so that the Flutter mobile app
 * running on a physical device (via USB debugging or a local network address)
 * is never blocked by browser-level CORS policies.
 *
 * <p>Tighten {@code allowedOriginPatterns} to the specific frontend URL before
 * going fully public.
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOriginPattern("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        // Credentials cannot be sent with a wildcard origin.
        // Set to false until explicit allowed origins are configured.
        config.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
