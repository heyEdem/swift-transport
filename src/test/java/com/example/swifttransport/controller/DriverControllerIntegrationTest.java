package com.example.swifttransport.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class DriverControllerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("swift_transport")
            .withUsername("postgres")
            .withPassword("postgres");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(roles = "ADMIN")
    void createDriver_Success() throws Exception {
        mockMvc.perform(post("/api/v1/drivers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "fullName": "Test Driver",
                                "phoneNumber": "+233244999999",
                                "licenseNumber": "DL999999999",
                                "status": "ACTIVE"
                            }
                            """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fullName").value("Test Driver"))
                .andExpect(jsonPath("$.licenseNumber").value("DL999999999"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createDriver_DuplicateLicense_ReturnsBadRequest() throws Exception {
        String requestBody = """
            {
                "fullName": "Test Driver",
                "phoneNumber": "+233244999999",
                "licenseNumber": "DL999999999",
                "status": "ACTIVE"
            }
            """;

        mockMvc.perform(post("/api/v1/drivers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        mockMvc.perform(post("/api/v1/drivers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "OPERATIONS")
    void getDrivers_ReturnsList() throws Exception {
        mockMvc.perform(get("/api/v1/drivers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @WithMockUser(roles = "OPERATIONS")
    void getDriverById_Success() throws Exception {
        mockMvc.perform(get("/api/v1/drivers/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "OPERATIONS")
    void getDriverById_NotFound_Returns404() throws Exception {
        mockMvc.perform(get("/api/v1/drivers/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getDrivers_Unauthorized_Returns401() throws Exception {
        mockMvc.perform(get("/api/v1/drivers"))
                .andExpect(status().isUnauthorized());
    }
}
