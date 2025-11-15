package com.tricol.supply.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tricol.supply.dto.FournisseurDTO;
import com.tricol.supply.model.entity.Fournisseur;
import com.tricol.supply.repository.FournisseurRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Tests d'intégration pour FournisseurController")
class FournisseurControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FournisseurRepository fournisseurRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Fournisseur testFournisseur;

    @BeforeEach
    void setUp() {
        fournisseurRepository.deleteAll();
        
        testFournisseur = Fournisseur.builder()
                .societe("Fournisseur Test SARL")
                .adresse("123 Rue Test")
                .contact("Mohamed Alami")
                .email("contact@test.com")
                .telephone("0612345678")
                .ville("Casablanca")
                .ice("001234567890001")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        testFournisseur = fournisseurRepository.save(testFournisseur);
    }

    @Test
    @DisplayName("GET /api/v1/fournisseurs - Doit retourner une liste de fournisseurs")
    void testGetAllFournisseurs() throws Exception {
        mockMvc.perform(get("/api/v1/fournisseurs")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].societe").value("Fournisseur Test SARL"))
                .andExpect(jsonPath("$.content[0].email").value("contact@test.com"));
    }

    @Test
    @DisplayName("GET /api/v1/fournisseurs/{id} - Doit retourner un fournisseur par son ID")
    void testGetFournisseurById() throws Exception {
        mockMvc.perform(get("/api/v1/fournisseurs/{id}", testFournisseur.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testFournisseur.getId()))
                .andExpect(jsonPath("$.societe").value("Fournisseur Test SARL"))
                .andExpect(jsonPath("$.email").value("contact@test.com"));
    }

    
}

