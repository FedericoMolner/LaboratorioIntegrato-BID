package com.its.statistics.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class ApiKeyFilter extends OncePerRequestFilter {

    @Value("${app.api.key:}")
    private String apiKey;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        // Only filter /bq/ endpoints for now
        String path = request.getRequestURI();
        if (path.startsWith("/bq/")) {
            return false;
        }
        return true;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // If no apiKey is configured, allow all
        if (apiKey == null || apiKey.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }
        String header = request.getHeader("X-API-KEY");
        if (header == null || !header.equals(apiKey)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Missing or invalid API key\"}");
            return;
        }
        filterChain.doFilter(request, response);
    }
}
