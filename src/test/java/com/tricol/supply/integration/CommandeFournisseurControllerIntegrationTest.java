package com.tricol.supply.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tricol.supply.dto.CommandeFournisseurDTO;
import com.tricol.supply.dto.ProduitCommandeDTO;
import com.tricol.supply.model.entity.*;
import com.tricol.supply.model.enums.StatutCommande;
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
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Tests d'intégration pour CommandeFournisseurController")
class CommandeFournisseurControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CommandeFournisseurRepository commandeRepository;

    @Autowired
    private FournisseurRepository fournisseurRepository;

    @Autowired
    private ProduitRepository produitRepository;

    @Autowired
    private CommandeProduitRepository commandeProduitRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Fournisseur testFournisseur;
    private Produit testProduit;
    private CommandeFournisseur testCommande;

    @BeforeEach
    void setUp() {
        commandeRepository.deleteAll();
        commandeProduitRepository.deleteAll();
        produitRepository.deleteAll();
        fournisseurRepository.deleteAll();

        // creer un fournisseur
        testFournisseur = Fournisseur.builder()
                .societe("Fournisseur Test SARL")
                .adresse("123 Rue Test")
                .contact("Mohamed Alami")
                .email("contact@test.com")
                .telephone("0612345678")
                .ville("Casablanca")
                .ice("001234567890001")
                .createdAt(LocalDateTime.now())
                .build();
        testFournisseur = fournisseurRepository.save(testFournisseur);

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
                .statut(StatutCommande.EN_ATTENTE)
                .fournisseur(testFournisseur)
                .montantTotal(new BigDecimal("275000.00"))
                .build();
        testCommande = commandeRepository.save(testCommande);

        // creer une commande produit
        CommandeProduit commandeProduit = CommandeProduit.builder()
                .commande(testCommande)
                .produit(testProduit)
                .quantite(50)
                .prixUnitaireCommande(new BigDecimal("5500.00"))
                .build();
        commandeProduitRepository.save(commandeProduit);
    }

    @Test
    @DisplayName("GET /api/v1/commandes - Doit retourner une liste de commandes")
    void testGetAllCommandes() throws Exception {
        mockMvc.perform(get("/api/v1/commandes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(testCommande.getId()))
                .andExpect(jsonPath("$.content[0].statut").value("EN_ATTENTE"));
    }

    @Test
    @DisplayName("GET /api/v1/commandes/{id} - Doit retourner une commande par son ID")
    void testGetCommandeById() throws Exception {
        mockMvc.perform(get("/api/v1/commandes/{id}", testCommande.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testCommande.getId()))
                .andExpect(jsonPath("$.fournisseur.id").value(testFournisseur.getId()))
                .andExpect(jsonPath("$.montantTotal").value(275000.00));
    }

    @Test
    @DisplayName("GET /api/v1/commandes/{id} - Doit retourner 404 si commande inexistante")
    void testGetCommandeById_NotFound() throws Exception {
        mockMvc.perform(get("/api/v1/commandes/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/v1/commandes/fournisseur/{id} - Doit retourner les commandes d'un fournisseur")
    void testGetCommandesByFournisseur() throws Exception {
        mockMvc.perform(get("/api/v1/commandes/fournisseur/{id}", testFournisseur.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(testCommande.getId()))
                .andExpect(jsonPath("$[0].statut").value("EN_ATTENTE"));
    }

    @Test
    @DisplayName("POST /api/v1/commandes - Doit créer une nouvelle commande et réserver le stock")
    void testCreateCommande() throws Exception {
        CommandeFournisseurDTO newCommande = new CommandeFournisseurDTO();
        newCommande.setFournisseurId(testFournisseur.getId());
        newCommande.setStatut(StatutCommande.EN_ATTENTE);
        
        ProduitCommandeDTO produitCommande = new ProduitCommandeDTO();
        produitCommande.setProduitId(testProduit.getId());
        produitCommande.setQuantite(20);
        produitCommande.setPrixUnitaireCommande(new BigDecimal("5500.00"));
        
        newCommande.setProduits(Arrays.asList(produitCommande));

        int stockAvant = testProduit.getStockActuel();

        mockMvc.perform(post("/api/v1/commandes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCommande)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.fournisseur.id").value(testFournisseur.getId()))
                .andExpect(jsonPath("$.montantTotal").value(110000.00));

        // verifier que le stock a été reserve (diminué)
        Produit updatedProduit = produitRepository.findById(testProduit.getId()).orElseThrow();
        assertEquals(stockAvant - 20, updatedProduit.getStockActuel(),
                "Le stock doit être diminué de 20 unités");
    }

    @Test
    @DisplayName("POST /api/v1/commandes - Doit retourner une erreur si stock insuffisant")
    void testCreateCommande_InsufficientStock() throws Exception {
        CommandeFournisseurDTO newCommande = new CommandeFournisseurDTO();
        newCommande.setFournisseurId(testFournisseur.getId());
        newCommande.setStatut(StatutCommande.EN_ATTENTE);
        
        ProduitCommandeDTO produitCommande = new ProduitCommandeDTO();
        produitCommande.setProduitId(testProduit.getId());
        produitCommande.setQuantite(200); // stock insuffisant (disponible: 100)
        produitCommande.setPrixUnitaireCommande(new BigDecimal("5500.00"));
        
        newCommande.setProduits(Arrays.asList(produitCommande));

        mockMvc.perform(post("/api/v1/commandes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCommande)))
                .andExpect(status().isBadRequest());
    }

    
}


