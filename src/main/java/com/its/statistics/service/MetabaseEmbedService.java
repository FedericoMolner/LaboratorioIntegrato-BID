package com.its.statistics.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
public class MetabaseEmbedService {

    @Value("${app.metabase.embed-secret:}")
    private String embedSecret;

    @Value("${app.metabase.url:}")
    private String metabaseUrl;

    public String generateDashboardEmbedUrl(long dashboardId, Map<String, Object> params, long minutesValid) {
        if (embedSecret == null || embedSecret.isBlank() || metabaseUrl == null || metabaseUrl.isBlank()) {
            throw new IllegalStateException("Metabase embed secret or url is not configured (app.metabase.embed-secret, app.metabase.url)");
        }

        Algorithm algorithm = Algorithm.HMAC256(embedSecret);
        long now = System.currentTimeMillis();
        Date exp = new Date(now + minutesValid * 60L * 1000L);

        com.auth0.jwt.JWTCreator.Builder tokenBuilder = JWT.create()
                .withClaim("resource", Map.of("dashboard", dashboardId))
                .withClaim("params", params != null ? params : Map.of())
                .withExpiresAt(exp);

        String token = tokenBuilder.sign(algorithm);
        String url = metabaseUrl + "/embed/dashboard/" + token + "#bordered=true&titled=true";
        return url;
    }
}
