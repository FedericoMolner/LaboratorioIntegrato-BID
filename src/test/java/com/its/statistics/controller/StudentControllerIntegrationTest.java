package com.its.statistics.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.its.statistics.dto.StudentRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.TestPropertySource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {"connector.mode=LOCAL"})
@TestPropertySource(properties = {"connector.mode=LOCAL"})
public class StudentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCreateStudentAndGetAll() throws Exception {
        StudentRequest req = new StudentRequest("TestName","TestSurname","test@example.com");
        String json = objectMapper.writeValueAsString(req);

        mockMvc.perform(post("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("TestName"));

        mockMvc.perform(get("/students")).andExpect(status().isOk()).andExpect(jsonPath("$[0].name").exists());
    }

    @Test
    public void testCreateStudentValidation() throws Exception {
        StudentRequest req = new StudentRequest("A","", "invalid-email");
        String json = objectMapper.writeValueAsString(req);

        mockMvc.perform(post("/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists());
    }
}
