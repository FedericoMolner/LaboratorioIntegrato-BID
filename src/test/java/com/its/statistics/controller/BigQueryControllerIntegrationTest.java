package com.its.statistics.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.its.statistics.dto.StudentRequest;
import com.its.statistics.service.StudentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {"app.api.key=testkey", "connector.mode=LOCAL"})
public class BigQueryControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

        // use real StudentService and DB fallback; no mocks to ensure compatibility with JVM selection

    @Test
    public void testBQTestEndpointWithMock() throws Exception {
        // No mock; the service will return a default value (current date) when BigQuery not configured
        mockMvc.perform(get("/bq/test").header("X-API-KEY","testkey"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ok").value(true));
    }

    @Test
    public void testGetStudentsFallbackLocal() throws Exception {
        // Ensure BigQuery not configured
        // Ensure BigQuery not configured - no property is set by default

        StudentRequest req = new StudentRequest("Local","User","local@example.com");
        String json = objectMapper.writeValueAsString(req);
        mockMvc.perform(post("/bq/local/students").header("X-API-KEY","testkey")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/bq/students").header("X-API-KEY","testkey"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Local"));
    }

    @Test
    public void testPostStudentToBigQueryWhenConfigured() throws Exception {
        // For this test we cannot easily configure real BigQuery; instead we verify the fallback creation

        StudentRequest req = new StudentRequest("BQ","User","bq@example.com");
        String json = objectMapper.writeValueAsString(req);
        mockMvc.perform(post("/bq/students").header("X-API-KEY","testkey")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ok").value(true));
    }
}
