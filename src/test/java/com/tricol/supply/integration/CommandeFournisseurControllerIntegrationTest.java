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

    
}


