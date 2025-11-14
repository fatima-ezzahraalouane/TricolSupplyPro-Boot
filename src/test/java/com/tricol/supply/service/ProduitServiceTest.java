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

    
}

