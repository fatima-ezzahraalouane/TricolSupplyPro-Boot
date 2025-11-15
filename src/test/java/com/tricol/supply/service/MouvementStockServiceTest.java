package com.tricol.supply.service;

import com.tricol.supply.dto.MouvementStockDTO;
import com.tricol.supply.exception.ResourceNotFoundException;
import com.tricol.supply.mapper.MouvementStockMapper;
import com.tricol.supply.model.entity.CommandeFournisseur;
import com.tricol.supply.model.entity.Fournisseur;
import com.tricol.supply.model.entity.MouvementStock;
import com.tricol.supply.model.entity.Produit;
import com.tricol.supply.model.enums.StatutCommande;
import com.tricol.supply.model.enums.TypeMouvement;
import com.tricol.supply.repository.CommandeFournisseurRepository;
import com.tricol.supply.repository.MouvementStockRepository;
import com.tricol.supply.repository.ProduitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires pour MouvementStockService")
class MouvementStockServiceTest {

    @Mock
    private MouvementStockRepository mouvementStockRepository;

    @Mock
    private ProduitRepository produitRepository;

    @Mock
    private CommandeFournisseurRepository commandeRepository;

    @Mock
    private MouvementStockMapper mouvementMapper;

    @InjectMocks
    private MouvementStockService mouvementStockService;

    private Produit produit;
    private CommandeFournisseur commande;
    private MouvementStock mouvement;
    private MouvementStockDTO mouvementDTO;

    @BeforeEach
    void setUp() {
        Fournisseur fournisseur = Fournisseur.builder()
                .id(1L)
                .societe("Fournisseur Test SARL")
                .build();

        produit = Produit.builder()
                .id(1L)
                .nom("Ordinateur Portable HP")
                .prixUnitaire(new BigDecimal("5500.00"))
                .stockActuel(100)
                .build();

        commande = CommandeFournisseur.builder()
                .id(1L)
                .dateCommande(LocalDateTime.now())
                .statut(StatutCommande.LIVREE)
                .fournisseur(fournisseur)
                .build();

        mouvement = MouvementStock.builder()
                .id(1L)
                .dateMouvement(LocalDateTime.now())
                .typeMouvement(TypeMouvement.SORTIE)
                .quantite(50)
                .prixUnitaire(new BigDecimal("5500.00"))
                .produit(produit)
                .commandeFournisseur(commande)
                .createdAt(LocalDateTime.now())
                .build();

        mouvementDTO = new MouvementStockDTO();
        mouvementDTO.setId(1L);
        mouvementDTO.setDateMouvement(LocalDateTime.now());
        mouvementDTO.setTypeMouvement(TypeMouvement.SORTIE);
        mouvementDTO.setQuantite(50);
        mouvementDTO.setPrixUnitaire(new BigDecimal("5500.00"));
        mouvementDTO.setProduitId(1L);
        mouvementDTO.setCommandeFournisseurId(1L);
    }

    @Test
    @DisplayName("Doit retourner les mouvements d'un produit existant")
    void testFindByProduit_Success() {
        // Given
        Long produitId = 1L;
        List<MouvementStock> mouvements = Arrays.asList(mouvement);
        
        when(produitRepository.findById(produitId)).thenReturn(Optional.of(produit));
        when(mouvementStockRepository.findByProduit(produit)).thenReturn(mouvements);
        when(mouvementMapper.toDTOList(mouvements)).thenReturn(Arrays.asList(mouvementDTO));

        // When
        List<MouvementStockDTO> result = mouvementStockService.findByProduit(produitId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(mouvementDTO.getId(), result.get(0).getId());
        assertEquals(mouvementDTO.getQuantite(), result.get(0).getQuantite());
        verify(produitRepository, times(1)).findById(produitId);
        verify(mouvementStockRepository, times(1)).findByProduit(produit);
        verify(mouvementMapper, times(1)).toDTOList(mouvements);
    }

    @Test
    @DisplayName("Doit lever une exception si le produit n'existe pas")
    void testFindByProduit_NotFound() {
        // Given
        Long produitId = 999L;
        when(produitRepository.findById(produitId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> mouvementStockService.findByProduit(produitId));
        verify(produitRepository, times(1)).findById(produitId);
        verify(mouvementStockRepository, never()).findByProduit(any());
        verify(mouvementMapper, never()).toDTOList(any());
    }

    @Test
    @DisplayName("Doit retourner une liste vide si aucun mouvement pour le produit")
    void testFindByProduit_EmptyList() {
        // Given
        Long produitId = 1L;
        List<MouvementStock> mouvementsVides = Arrays.asList();
        
        when(produitRepository.findById(produitId)).thenReturn(Optional.of(produit));
        when(mouvementStockRepository.findByProduit(produit)).thenReturn(mouvementsVides);
        when(mouvementMapper.toDTOList(mouvementsVides)).thenReturn(Arrays.asList());

        // When
        List<MouvementStockDTO> result = mouvementStockService.findByProduit(produitId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(produitRepository, times(1)).findById(produitId);
        verify(mouvementStockRepository, times(1)).findByProduit(produit);
    }

    @Test
    @DisplayName("Doit retourner les mouvements d'une commande existante")
    void testFindByCommande_Success() {
        // Given
        Long commandeId = 1L;
        List<MouvementStock> mouvements = Arrays.asList(mouvement);
        
        when(commandeRepository.findById(commandeId)).thenReturn(Optional.of(commande));
        when(mouvementStockRepository.findByCommandeFournisseur(commande)).thenReturn(mouvements);
        when(mouvementMapper.toDTOList(mouvements)).thenReturn(Arrays.asList(mouvementDTO));

        // When
        List<MouvementStockDTO> result = mouvementStockService.findByCommande(commandeId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(mouvementDTO.getId(), result.get(0).getId());
        assertEquals(mouvementDTO.getCommandeFournisseurId(), result.get(0).getCommandeFournisseurId());
        verify(commandeRepository, times(1)).findById(commandeId);
        verify(mouvementStockRepository, times(1)).findByCommandeFournisseur(commande);
        verify(mouvementMapper, times(1)).toDTOList(mouvements);
    }

    @Test
    @DisplayName("Doit lever une exception si la commande n'existe pas")
    void testFindByCommande_NotFound() {
        // Given
        Long commandeId = 999L;
        when(commandeRepository.findById(commandeId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> mouvementStockService.findByCommande(commandeId));
        verify(commandeRepository, times(1)).findById(commandeId);
        verify(mouvementStockRepository, never()).findByCommandeFournisseur(any());
        verify(mouvementMapper, never()).toDTOList(any());
    }

    @Test
    @DisplayName("Doit retourner plusieurs mouvements pour un produit")
    void testFindByProduit_MultipleMouvements() {
        // Given
        Long produitId = 1L;
        
        MouvementStock mouvement1 = MouvementStock.builder()
                .id(1L)
                .typeMouvement(TypeMouvement.ENTREE)
                .quantite(100)
                .prixUnitaire(new BigDecimal("5500.00"))
                .produit(produit)
                .build();
        
        MouvementStock mouvement2 = MouvementStock.builder()
                .id(2L)
                .typeMouvement(TypeMouvement.SORTIE)
                .quantite(50)
                .prixUnitaire(new BigDecimal("5500.00"))
                .produit(produit)
                .build();
        
        List<MouvementStock> mouvements = Arrays.asList(mouvement1, mouvement2);
        
        MouvementStockDTO dto1 = new MouvementStockDTO();
        dto1.setId(1L);
        dto1.setTypeMouvement(TypeMouvement.ENTREE);
        dto1.setQuantite(100);
        
        MouvementStockDTO dto2 = new MouvementStockDTO();
        dto2.setId(2L);
        dto2.setTypeMouvement(TypeMouvement.SORTIE);
        dto2.setQuantite(50);
        
        List<MouvementStockDTO> dtos = Arrays.asList(dto1, dto2);
        
        when(produitRepository.findById(produitId)).thenReturn(Optional.of(produit));
        when(mouvementStockRepository.findByProduit(produit)).thenReturn(mouvements);
        when(mouvementMapper.toDTOList(mouvements)).thenReturn(dtos);

        // When
        List<MouvementStockDTO> result = mouvementStockService.findByProduit(produitId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(TypeMouvement.ENTREE, result.get(0).getTypeMouvement());
        assertEquals(TypeMouvement.SORTIE, result.get(1).getTypeMouvement());
    }
}


