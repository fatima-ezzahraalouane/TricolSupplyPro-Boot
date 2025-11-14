package com.tricol.supply.service;

import com.tricol.supply.dto.ProduitDTO;
import com.tricol.supply.exception.ResourceNotFoundException;
import com.tricol.supply.mapper.ProduitMapper;
import com.tricol.supply.model.entity.MouvementStock;
import com.tricol.supply.model.entity.Produit;
import com.tricol.supply.model.enums.TypeMouvement;
import com.tricol.supply.repository.MouvementStockRepository;
import com.tricol.supply.repository.ProduitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires pour ProduitService")
class ProduitServiceTest {

    @Mock
    private ProduitRepository produitRepository;

    @Mock
    private ProduitMapper produitMapper;

    @Mock
    private MouvementStockRepository mouvementStockRepository;

    @InjectMocks
    private ProduitService produitService;

    private Produit produit;
    private ProduitDTO produitDTO;

    @BeforeEach
    void setUp() {
        produit = Produit.builder()
                .id(1L)
                .nom("Ordinateur Portable HP")
                .description("Ordinateur portable HP 15 pouces")
                .prixUnitaire(new BigDecimal("5500.00"))
                .categorie("Informatique")
                .stockActuel(50)
                .coutUnitaireMoyen(new BigDecimal("5500.00"))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        produitDTO = new ProduitDTO();
        produitDTO.setId(1L);
        produitDTO.setNom("Ordinateur Portable HP");
        produitDTO.setDescription("Ordinateur portable HP 15 pouces");
        produitDTO.setPrixUnitaire(new BigDecimal("5500.00"));
        produitDTO.setCategorie("Informatique");
        produitDTO.setStockActuel(50);
    }

    @Test
    @DisplayName("Doit retourner une page de produits")
    void testFindAll() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<Produit> produits = Arrays.asList(produit);
        Page<Produit> page = new PageImpl<>(produits, pageable, 1);

        when(produitRepository.findAll(pageable)).thenReturn(page);
        when(produitMapper.toDTO(any(Produit.class))).thenReturn(produitDTO);

        // When
        Page<ProduitDTO> result = produitService.findAll(pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(produitRepository, times(1)).findAll(pageable);
        verify(produitMapper, times(1)).toDTO(any(Produit.class));
    }

    @Test
    @DisplayName("Doit retourner un produit par son ID")
    void testFindById_Success() {
        // Given
        Long id = 1L;
        when(produitRepository.findById(id)).thenReturn(Optional.of(produit));
        when(produitMapper.toDTO(produit)).thenReturn(produitDTO);

        // When
        ProduitDTO result = produitService.findById(id);

        // Then
        assertNotNull(result);
        assertEquals(id, result.getId());
        verify(produitRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Doit lever une exception quand le produit n'existe pas")
    void testFindById_NotFound() {
        // Given
        Long id = 999L;
        when(produitRepository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> produitService.findById(id));
        verify(produitRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Doit créer un produit avec calcul automatique du CUMP initial")
    void testCreate_WithStock() {
        // Given
        when(produitMapper.toEntity(produitDTO)).thenReturn(produit);
        when(produitRepository.save(any(Produit.class))).thenReturn(produit);
        when(produitMapper.toDTO(produit)).thenReturn(produitDTO);
        when(mouvementStockRepository.save(any(MouvementStock.class))).thenReturn(new MouvementStock());

        ArgumentCaptor<Produit> produitCaptor = ArgumentCaptor.forClass(Produit.class);
        ArgumentCaptor<MouvementStock> mouvementCaptor = ArgumentCaptor.forClass(MouvementStock.class);

        // When
        ProduitDTO result = produitService.create(produitDTO);

        // Then
        assertNotNull(result);
        verify(produitRepository, times(1)).save(produitCaptor.capture());
        
        Produit savedProduit = produitCaptor.getValue();
        assertEquals(new BigDecimal("5500.00"), savedProduit.getCoutUnitaireMoyen(), 
                "Le CUMP initial doit être égal au prix unitaire quand stock > 0");
        
        verify(mouvementStockRepository, times(1)).save(mouvementCaptor.capture());
        MouvementStock mouvementCree = mouvementCaptor.getValue();
        assertEquals(TypeMouvement.ENTREE, mouvementCree.getTypeMouvement(),
                "Le type de mouvement doit être ENTREE lors de la création d'un produit avec stock");
        assertEquals(50, mouvementCree.getQuantite());
    }

    @Test
    @DisplayName("Doit créer un produit sans stock avec CUMP à zéro")
    void testCreate_WithoutStock() {
        // Given
        produitDTO.setStockActuel(0);
        produit.setStockActuel(0);
        produit.setCoutUnitaireMoyen(BigDecimal.ZERO);

        when(produitMapper.toEntity(produitDTO)).thenReturn(produit);
        when(produitRepository.save(any(Produit.class))).thenReturn(produit);
        when(produitMapper.toDTO(produit)).thenReturn(produitDTO);

        ArgumentCaptor<Produit> produitCaptor = ArgumentCaptor.forClass(Produit.class);

        // When
        produitService.create(produitDTO);

        // Then
        verify(produitRepository, times(1)).save(produitCaptor.capture());
        Produit savedProduit = produitCaptor.getValue();
        assertEquals(BigDecimal.ZERO, savedProduit.getCoutUnitaireMoyen(), 
                "Le CUMP doit être zéro quand stock = 0");
        
        verify(mouvementStockRepository, never()).save(any(MouvementStock.class));
    }

    @Test
    @DisplayName("Doit recalculer le CUMP lors d'une augmentation de stock")
    void testUpdate_RecalculateCUMP_StockIncrease() {
        // Given
        Long id = 1L;
        
        // Produit existant : stock=50, CUMP=5500.00
        Produit existingProduit = Produit.builder()
                .id(id)
                .nom("Ordinateur Portable HP")
                .prixUnitaire(new BigDecimal("5500.00"))
                .stockActuel(50)
                .coutUnitaireMoyen(new BigDecimal("5500.00"))
                .build();

        // Nouveau DTO : stock=75 (ajout de 25 unités), prix=6000.00
        ProduitDTO dtoUpdate = new ProduitDTO();
        dtoUpdate.setNom("Ordinateur Portable HP Modifié");
        dtoUpdate.setPrixUnitaire(new BigDecimal("6000.00"));
        dtoUpdate.setStockActuel(75);

        when(produitRepository.findById(id)).thenReturn(Optional.of(existingProduit));
        when(produitRepository.save(any(Produit.class))).thenReturn(existingProduit);
        when(produitMapper.toDTO(any(Produit.class))).thenReturn(produitDTO);

        ArgumentCaptor<Produit> produitCaptor = ArgumentCaptor.forClass(Produit.class);
        ArgumentCaptor<MouvementStock> mouvementCaptor = ArgumentCaptor.forClass(MouvementStock.class);

        // When
        produitService.update(id, dtoUpdate);

        // Then
        verify(produitRepository, times(1)).save(produitCaptor.capture());
        Produit updatedProduit = produitCaptor.getValue();

        // Calcul CUMP attendu : (50 * 5500 + 25 * 6000) / 75 = 5666.67
        BigDecimal expectedCump = new BigDecimal("50")
                .multiply(new BigDecimal("5500"))
                .add(new BigDecimal("25").multiply(new BigDecimal("6000")))
                .divide(new BigDecimal("75"), 2, RoundingMode.HALF_UP);

        assertEquals(expectedCump, updatedProduit.getCoutUnitaireMoyen(), 
                "Le CUMP doit être recalculé selon la formule CUMP");
        assertEquals(75, updatedProduit.getStockActuel());
        
        verify(mouvementStockRepository, times(1)).save(mouvementCaptor.capture());
        MouvementStock mouvementCree = mouvementCaptor.getValue();
        assertEquals(TypeMouvement.AJUSTEMENT, mouvementCree.getTypeMouvement(),
                "Le type de mouvement doit être AJUSTEMENT lors d'une modification manuelle du stock");
        assertEquals(25, mouvementCree.getQuantite());
    }

    @Test
    @DisplayName("Doit conserver le CUMP lors d'une diminution de stock")
    void testUpdate_KeepCUMP_StockDecrease() {
        // Given
        Long id = 1L;
        
        Produit existingProduit = Produit.builder()
                .id(id)
                .prixUnitaire(new BigDecimal("5500.00"))
                .stockActuel(50)
                .coutUnitaireMoyen(new BigDecimal("5500.00"))
                .build();

        ProduitDTO dtoUpdate = new ProduitDTO();
        dtoUpdate.setPrixUnitaire(new BigDecimal("6000.00"));
        dtoUpdate.setStockActuel(30); // Diminution de 20 unités

        when(produitRepository.findById(id)).thenReturn(Optional.of(existingProduit));
        when(produitRepository.save(any(Produit.class))).thenReturn(existingProduit);
        when(produitMapper.toDTO(any(Produit.class))).thenReturn(produitDTO);

        ArgumentCaptor<Produit> produitCaptor = ArgumentCaptor.forClass(Produit.class);
        ArgumentCaptor<MouvementStock> mouvementCaptor = ArgumentCaptor.forClass(MouvementStock.class);

        // When
        produitService.update(id, dtoUpdate);

        // Then
        verify(produitRepository, times(1)).save(produitCaptor.capture());
        Produit updatedProduit = produitCaptor.getValue();

        // Le CUMP doit rester inchangé lors d'une sortie
        assertEquals(new BigDecimal("5500.00"), updatedProduit.getCoutUnitaireMoyen(), 
                "Le CUMP ne doit pas changer lors d'une sortie de stock");
        
        verify(mouvementStockRepository, times(1)).save(mouvementCaptor.capture());
        MouvementStock mouvementCree = mouvementCaptor.getValue();
        assertEquals(TypeMouvement.AJUSTEMENT, mouvementCree.getTypeMouvement(),
                "Le type de mouvement doit être AJUSTEMENT lors d'une modification manuelle du stock");
        assertEquals(20, mouvementCree.getQuantite());
    }

    @Test
    @DisplayName("Doit mettre à jour un produit sans modifier le stock (aucun mouvement créé)")
    void testUpdate_WithoutStockChange() {
        // Given
        Long id = 1L;
        
        Produit existingProduit = Produit.builder()
                .id(id)
                .nom("Ordinateur Portable HP")
                .description("Description originale")
                .prixUnitaire(new BigDecimal("5500.00"))
                .categorie("Informatique")
                .stockActuel(50)
                .coutUnitaireMoyen(new BigDecimal("5500.00"))
                .build();

        ProduitDTO dtoUpdate = new ProduitDTO();
        dtoUpdate.setNom("Ordinateur Portable HP Modifié");
        dtoUpdate.setDescription("Nouvelle description");
        dtoUpdate.setPrixUnitaire(new BigDecimal("6000.00"));
        dtoUpdate.setCategorie("Électronique");
        dtoUpdate.setStockActuel(50); // Même stock, pas de modification

        when(produitRepository.findById(id)).thenReturn(Optional.of(existingProduit));
        when(produitRepository.save(any(Produit.class))).thenReturn(existingProduit);
        when(produitMapper.toDTO(any(Produit.class))).thenReturn(produitDTO);

        ArgumentCaptor<Produit> produitCaptor = ArgumentCaptor.forClass(Produit.class);

        // When
        produitService.update(id, dtoUpdate);

        // Then
        verify(produitRepository, times(1)).save(produitCaptor.capture());
        Produit updatedProduit = produitCaptor.getValue();
        
        // Vérifier que les autres champs sont mis à jour
        assertEquals("Ordinateur Portable HP Modifié", updatedProduit.getNom());
        assertEquals("Nouvelle description", updatedProduit.getDescription());
        assertEquals(new BigDecimal("6000.00"), updatedProduit.getPrixUnitaire());
        assertEquals("Électronique", updatedProduit.getCategorie());
        
        // Le stock et CUMP ne doivent pas changer
        assertEquals(50, updatedProduit.getStockActuel());
        assertEquals(new BigDecimal("5500.00"), updatedProduit.getCoutUnitaireMoyen(),
                "Le CUMP ne doit pas changer si le stock n'est pas modifié");
        
        // Aucun mouvement ne doit être créé
        verify(mouvementStockRepository, never()).save(any(MouvementStock.class));
    }

    @Test
    @DisplayName("Doit mettre le CUMP à zéro si le stock devient nul")
    void testUpdate_StockToZero() {
        // Given
        Long id = 1L;
        
        Produit existingProduit = Produit.builder()
                .id(id)
                .prixUnitaire(new BigDecimal("5500.00"))
                .stockActuel(10)
                .coutUnitaireMoyen(new BigDecimal("5500.00"))
                .build();

        ProduitDTO dtoUpdate = new ProduitDTO();
        dtoUpdate.setPrixUnitaire(new BigDecimal("6000.00"));
        dtoUpdate.setStockActuel(0);

        when(produitRepository.findById(id)).thenReturn(Optional.of(existingProduit));
        when(produitRepository.save(any(Produit.class))).thenReturn(existingProduit);
        when(produitMapper.toDTO(any(Produit.class))).thenReturn(produitDTO);

        ArgumentCaptor<Produit> produitCaptor = ArgumentCaptor.forClass(Produit.class);
        ArgumentCaptor<MouvementStock> mouvementCaptor = ArgumentCaptor.forClass(MouvementStock.class);

        // When
        produitService.update(id, dtoUpdate);

        // Then
        verify(produitRepository, times(1)).save(produitCaptor.capture());
        Produit updatedProduit = produitCaptor.getValue();

        assertEquals(BigDecimal.ZERO, updatedProduit.getCoutUnitaireMoyen(), 
                "Le CUMP doit être zéro si le stock devient nul");
        
        verify(mouvementStockRepository, times(1)).save(mouvementCaptor.capture());
        MouvementStock mouvementCree = mouvementCaptor.getValue();
        assertEquals(TypeMouvement.AJUSTEMENT, mouvementCree.getTypeMouvement(),
                "Le type de mouvement doit être AJUSTEMENT lors d'une modification manuelle du stock");
        assertEquals(10, mouvementCree.getQuantite());
    }

    @Test
    @DisplayName("Doit supprimer un produit existant")
    void testDelete_Success() {
        // Given
        Long id = 1L;
        when(produitRepository.existsById(id)).thenReturn(true);
        doNothing().when(produitRepository).deleteById(id);

        // When
        produitService.delete(id);

        // Then
        verify(produitRepository, times(1)).existsById(id);
        verify(produitRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("Doit lever une exception lors de la suppression d'un produit inexistant")
    void testDelete_NotFound() {
        // Given
        Long id = 999L;
        when(produitRepository.existsById(id)).thenReturn(false);

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> produitService.delete(id));
        verify(produitRepository, times(1)).existsById(id);
        verify(produitRepository, never()).deleteById(any());
    }
}

