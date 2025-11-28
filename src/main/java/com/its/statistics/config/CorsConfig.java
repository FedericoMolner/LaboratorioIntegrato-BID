package com.its.statistics.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Configurazione per tutte le API
        registry.addMapping("/api/**")
                .allowedOriginPatterns("*") // Permette a PowerApps di chiamare da qualsiasi origine
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);

        // Configurazione per Metabase embed (se necessario)
        registry.addMapping("/embed/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    /**
     * Bean alternativo per CORS più permissivo (utile per PowerApps)
     * Questo gestisce meglio le richieste preflight OPTIONS
     */
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        // Permetti credenziali
        config.setAllowCredentials(true);
        
        // Permetti tutte le origini (PowerApps può chiamare da domini diversi)
        config.addAllowedOriginPattern("*");
        
        // Permetti tutti gli header
        config.addAllowedHeader("*");
        
        // Permetti tutti i metodi HTTP
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        
        // Esponi gli header nella risposta
        config.setExposedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "X-Requested-With",
            "Accept",
            "Origin",
            "Access-Control-Request-Method",
            "Access-Control-Request-Headers"
        ));
        
        // Applica la configurazione a tutti i path
        source.registerCorsConfiguration("/**", config);
        
        return new CorsFilter(source);
    }
}