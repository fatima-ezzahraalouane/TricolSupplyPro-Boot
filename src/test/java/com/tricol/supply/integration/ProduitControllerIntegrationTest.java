package com.tricol.supply.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tricol.supply.dto.ProduitDTO;
import com.tricol.supply.model.entity.Produit;
import com.tricol.supply.repository.ProduitRepository;
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

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Tests d'intégration pour ProduitController - incluant calcul CUMP")
class ProduitControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProduitRepository produitRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Produit testProduit;

    @BeforeEach
    void setUp() {
        produitRepository.deleteAll();
        
        testProduit = Produit.builder()
                .nom("Ordinateur Portable HP")
                .description("Ordinateur portable HP 15 pouces")
                .prixUnitaire(new BigDecimal("5500.00"))
                .categorie("Informatique")
                .stockActuel(50)
                .coutUnitaireMoyen(new BigDecimal("5500.00"))
                .build();
        
        testProduit = produitRepository.save(testProduit);
    }

    @Test
    @DisplayName("GET /api/v1/produits - Doit retourner une liste de produits")
    void testGetAllProduits() throws Exception {
        mockMvc.perform(get("/api/v1/produits")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].nom").value("Ordinateur Portable HP"));
    }

    
}

