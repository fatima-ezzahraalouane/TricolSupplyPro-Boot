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

    @Test
    @DisplayName("GET /api/v1/fournisseurs/{id} - Doit retourner 404 si fournisseur inexistant")
    void testGetFournisseurById_NotFound() throws Exception {
        mockMvc.perform(get("/api/v1/fournisseurs/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/v1/fournisseurs - Doit créer un nouveau fournisseur")
    void testCreateFournisseur() throws Exception {
        FournisseurDTO newFournisseur = new FournisseurDTO();
        newFournisseur.setSociete("Nouveau Fournisseur SARL");
        newFournisseur.setAdresse("456 Nouvelle Rue");
        newFournisseur.setContact("Fatima-Ezzahra");
        newFournisseur.setEmail("nouveau@test.com");
        newFournisseur.setTelephone("0698765432");
        newFournisseur.setVille("Rabat");
        newFournisseur.setIce("009876543210001");

        mockMvc.perform(post("/api/v1/fournisseurs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newFournisseur)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.societe").value("Nouveau Fournisseur SARL"))
                .andExpect(jsonPath("$.email").value("nouveau@test.com"));
    }

    @Test
    @DisplayName("PUT /api/v1/fournisseurs/{id} - Doit mettre à jour un fournisseur existant")
    void testUpdateFournisseur() throws Exception {
        FournisseurDTO updateDTO = new FournisseurDTO();
        updateDTO.setSociete("Fournisseur Modifié SARL");
        updateDTO.setAdresse("789 Rue Modifiée");
        updateDTO.setContact("Ahmed Benali");
        updateDTO.setEmail("modifie@test.com");
        updateDTO.setTelephone("0611111111");
        updateDTO.setVille("Marrakech");
        updateDTO.setIce("001234567890001");

        mockMvc.perform(put("/api/v1/fournisseurs/{id}", testFournisseur.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.societe").value("Fournisseur Modifié SARL"))
                .andExpect(jsonPath("$.ville").value("Marrakech"));
    }

    @Test
    @DisplayName("DELETE /api/v1/fournisseurs/{id} - Doit supprimer un fournisseur")
    void testDeleteFournisseur() throws Exception {
        mockMvc.perform(delete("/api/v1/fournisseurs/{id}", testFournisseur.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Fournisseur supprimé avec succès"));

        // verifier que le fournisseur n'existe plus
        mockMvc.perform(get("/api/v1/fournisseurs/{id}", testFournisseur.getId()))
                .andExpect(status().isNotFound());
    }
}

