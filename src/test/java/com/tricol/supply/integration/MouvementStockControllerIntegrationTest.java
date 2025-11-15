package com.tricol.supply.integration;

import com.tricol.supply.model.entity.*;
import com.tricol.supply.model.enums.StatutCommande;
import com.tricol.supply.model.enums.TypeMouvement;
import com.tricol.supply.repository.*;
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
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Tests d'intégration pour MouvementStockController")
class MouvementStockControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MouvementStockRepository mouvementStockRepository;

    @Autowired
    private ProduitRepository produitRepository;

    @Autowired
    private FournisseurRepository fournisseurRepository;

    @Autowired
    private CommandeFournisseurRepository commandeRepository;

    private Produit testProduit;
    private CommandeFournisseur testCommande;
    private MouvementStock testMouvement;

    @BeforeEach
    void setUp() {
        mouvementStockRepository.deleteAll();
        commandeRepository.deleteAll();
        produitRepository.deleteAll();
        fournisseurRepository.deleteAll();

        // creer un fournisseur
        Fournisseur fournisseur = Fournisseur.builder()
                .societe("Fournisseur Test SARL")
                .adresse("123 Rue Test")
                .contact("Mohamed Alami")
                .email("contact@test.com")
                .telephone("0612345678")
                .ville("Casablanca")
                .ice("001234567890001")
                .createdAt(LocalDateTime.now())
                .build();
        fournisseur = fournisseurRepository.save(fournisseur);

        // creer un produit
        testProduit = Produit.builder()
                .nom("Ordinateur Portable HP")
                .description("Ordinateur portable HP 15 pouces")
                .prixUnitaire(new BigDecimal("5500.00"))
                .categorie("Informatique")
                .stockActuel(100)
                .coutUnitaireMoyen(new BigDecimal("5500.00"))
                .build();
        testProduit = produitRepository.save(testProduit);

        // creer une commande
        testCommande = CommandeFournisseur.builder()
                .dateCommande(LocalDateTime.now())
                .statut(StatutCommande.LIVREE)
                .fournisseur(fournisseur)
                .montantTotal(new BigDecimal("275000.00"))
                .build();
        testCommande = commandeRepository.save(testCommande);

        // creer un mouvement de stock
        testMouvement = MouvementStock.builder()
                .dateMouvement(LocalDateTime.now())
                .typeMouvement(TypeMouvement.SORTIE)
                .quantite(50)
                .prixUnitaire(new BigDecimal("5500.00"))
                .produit(testProduit)
                .commandeFournisseur(testCommande)
                .build();
        testMouvement = mouvementStockRepository.save(testMouvement);
    }

    @Test
    @DisplayName("GET /api/v1/mouvements/produit/{id} - Doit retourner les mouvements d'un produit")
    void testGetMouvementsByProduit() throws Exception {
        mockMvc.perform(get("/api/v1/mouvements/produit/{produitId}", testProduit.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(testMouvement.getId()))
                .andExpect(jsonPath("$[0].typeMouvement").value("SORTIE"))
                .andExpect(jsonPath("$[0].quantite").value(50))
                .andExpect(jsonPath("$[0].produitId").value(testProduit.getId()));
    }

    @Test
    @DisplayName("GET /api/v1/mouvements/produit/{id} - Doit retourner 404 si produit inexistant")
    void testGetMouvementsByProduit_NotFound() throws Exception {
        mockMvc.perform(get("/api/v1/mouvements/produit/{produitId}", 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/v1/mouvements/commande/{id} - Doit retourner les mouvements d'une commande")
    void testGetMouvementsByCommande() throws Exception {
        mockMvc.perform(get("/api/v1/mouvements/commande/{commandeId}", testCommande.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(testMouvement.getId()))
                .andExpect(jsonPath("$[0].commandeFournisseurId").value(testCommande.getId()))
                .andExpect(jsonPath("$[0].typeMouvement").value("SORTIE"));
    }

    @Test
    @DisplayName("GET /api/v1/mouvements/commande/{id} - Doit retourner 404 si commande inexistante")
    void testGetMouvementsByCommande_NotFound() throws Exception {
        mockMvc.perform(get("/api/v1/mouvements/commande/{commandeId}", 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    
}


