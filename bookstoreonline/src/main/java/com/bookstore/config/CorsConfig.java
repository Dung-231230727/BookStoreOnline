package com.bookstore.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * Cấu hình CORS để Frontend (HTML/JS) gọi được Backend API.
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(java.util.List.of(
            "http://localhost:8080",
            "http://localhost:3000",
            "http://127.0.0.1:5500",
            "http://localhost:5500"
        ));
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.setExposedHeaders(java.util.List.of("Content-Disposition", "Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return new CorsFilter(source);
    }
}
