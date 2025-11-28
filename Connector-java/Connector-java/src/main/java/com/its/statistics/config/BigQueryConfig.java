package com.its.statistics.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

@Configuration
public class BigQueryConfig {

    @Value("${google.project.id:}")
    private String projectId;

    @Value("${google.credentials.path:}")
    private String credentialsPath;

    @Value("${connector.mode:BIGQUERY}")
    private String connectorMode;

    /**
     * Crea il bean BigQuery per connettersi a Google Cloud
     */
    @Bean
    @ConditionalOnProperty(prefix = "connector", name = "mode", havingValue = "BIGQUERY")
    public BigQuery bigQuery() throws IOException {
        // Require connector.mode == BIGQUERY
        if (credentialsPath == null || credentialsPath.isEmpty()) {
            throw new IllegalStateException("BigQuery mode selected but credentials path not configured");
        }

        // Carica le credenziali dal file JSON
        GoogleCredentials credentials = GoogleCredentials
                .fromStream(new FileInputStream(credentialsPath))
                .createScoped(Arrays.asList(
                    "https://www.googleapis.com/auth/bigquery",
                    "https://www.googleapis.com/auth/cloud-platform"
                ));

        // Costruisci il client BigQuery
        return BigQueryOptions.newBuilder()
                .setCredentials(credentials)
                .setProjectId(projectId)
                .build()
                .getService();
    }
}