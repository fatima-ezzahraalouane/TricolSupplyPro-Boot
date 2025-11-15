package com.tricol.supply.service;

import com.tricol.supply.dto.*;
import com.tricol.supply.exception.ResourceNotFoundException;
import com.tricol.supply.mapper.CommandeFournisseurMapper;
import com.tricol.supply.model.entity.*;
import com.tricol.supply.model.enums.StatutCommande;
import com.tricol.supply.model.enums.TypeMouvement;
import com.tricol.supply.repository.*;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires pour CommandeFournisseurService")
class CommandeFournisseurServiceTest {

    @Mock
    private CommandeFournisseurRepository commandeRepository;

    @Mock
    private FournisseurRepository fournisseurRepository;

    @Mock
    private ProduitRepository produitRepository;

    @Mock
    private CommandeProduitRepository commandeProduitRepository;

    @Mock
    private MouvementStockRepository mouvementStockRepository;

    @Mock
    private CommandeFournisseurMapper commandeMapper;

    @InjectMocks
    private CommandeFournisseurService commandeService;

    private Fournisseur fournisseur;
    private Produit produit;
    private CommandeFournisseur commande;
    private CommandeFournisseurDTO commandeDTO;
    private CommandeFournisseurDetailDTO detailDTO;

    @BeforeEach
    void setUp() {
        fournisseur = Fournisseur.builder()
                .id(1L)
                .societe("Fournisseur Test SARL")
                .adresse("123 Rue Test")
                .contact("Mohamed Alami")
                .email("contact@test.com")
                .telephone("0612345678")
                .ville("Casablanca")
                .ice("001234567890001")
                .createdAt(LocalDateTime.now())
                .build();

        produit = Produit.builder()
                .id(1L)
                .nom("Ordinateur Portable HP")
                .prixUnitaire(new BigDecimal("5500.00"))
                .stockActuel(100)
                .coutUnitaireMoyen(new BigDecimal("5500.00"))
                .build();

        commande = CommandeFournisseur.builder()
                .id(1L)
                .dateCommande(LocalDateTime.now())
                .statut(StatutCommande.EN_ATTENTE)
                .fournisseur(fournisseur)
                .montantTotal(new BigDecimal("275000.00"))
                .build();

        commandeDTO = new CommandeFournisseurDTO();
        commandeDTO.setFournisseurId(1L);
        commandeDTO.setStatut(StatutCommande.EN_ATTENTE);
        
        ProduitCommandeDTO produitCommandeDTO = new ProduitCommandeDTO();
        produitCommandeDTO.setProduitId(1L);
        produitCommandeDTO.setQuantite(50);
        produitCommandeDTO.setPrixUnitaireCommande(new BigDecimal("5500.00"));
        commandeDTO.setProduits(Arrays.asList(produitCommandeDTO));

        detailDTO = new CommandeFournisseurDetailDTO();
        detailDTO.setId(1L);
        
        FournisseurDTO fournisseurDTO = new FournisseurDTO();
        fournisseurDTO.setId(1L);
        detailDTO.setFournisseur(fournisseurDTO);
        
        detailDTO.setMontantTotal(new BigDecimal("275000.00"));
        detailDTO.setStatut(StatutCommande.EN_ATTENTE);
    }

    @Test
    @DisplayName("Doit retourner une page de commandes")
    void testFindAll() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<CommandeFournisseur> commandes = Arrays.asList(commande);
        Page<CommandeFournisseur> page = new PageImpl<>(commandes, pageable, 1);

        when(commandeRepository.findAll(pageable)).thenReturn(page);
        when(commandeMapper.toDTO(any(CommandeFournisseur.class))).thenReturn(commandeDTO);

        // When
        Page<CommandeFournisseurDTO> result = commandeService.findAll(pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(commandeRepository, times(1)).findAll(pageable);
        verify(commandeMapper, times(1)).toDTO(any(CommandeFournisseur.class));
    }

    @Test
    @DisplayName("Doit retourner une commande par son ID")
    void testFindById_Success() {
        // Given
        Long id = 1L;
        when(commandeRepository.findById(id)).thenReturn(Optional.of(commande));
        when(commandeMapper.toDetailDTO(commande)).thenReturn(detailDTO);

        // When
        CommandeFournisseurDetailDTO result = commandeService.findById(id);

        // Then
        assertNotNull(result);
        assertEquals(id, result.getId());
        verify(commandeRepository, times(1)).findById(id);
        verify(commandeMapper, times(1)).toDetailDTO(commande);
    }

    @Test
    @DisplayName("Doit lever une exception quand la commande n'existe pas")
    void testFindById_NotFound() {
        // Given
        Long id = 999L;
        when(commandeRepository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> commandeService.findById(id));
        verify(commandeRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Doit créer une commande et réserver le stock")
    void testCreate_Success() {
        // Given
        when(fournisseurRepository.findById(1L)).thenReturn(Optional.of(fournisseur));
        when(produitRepository.findById(1L)).thenReturn(Optional.of(produit));
        when(commandeRepository.save(any(CommandeFournisseur.class))).thenReturn(commande);
        when(commandeProduitRepository.saveAll(any())).thenReturn(new ArrayList<>());
        when(produitRepository.save(any(Produit.class))).thenReturn(produit);
        when(commandeMapper.toDetailDTO(any(CommandeFournisseur.class))).thenReturn(detailDTO);

        // When
        CommandeFournisseurDetailDTO result = commandeService.create(commandeDTO);

        // Then
        assertNotNull(result);
        assertEquals(new BigDecimal("275000.00"), result.getMontantTotal());
        
        // verifier que le stock a été reserve (diminué)
        ArgumentCaptor<Produit> produitCaptor = ArgumentCaptor.forClass(Produit.class);
        verify(produitRepository, times(1)).save(produitCaptor.capture());
        Produit updatedProduit = produitCaptor.getValue();
        assertEquals(50, updatedProduit.getStockActuel(), "Le stock doit être diminué de 50 unités");
        
        verify(commandeRepository, atLeastOnce()).save(any(CommandeFournisseur.class));
        verify(commandeProduitRepository, times(1)).saveAll(any());
    }

    @Test
    @DisplayName("Doit lever une exception si stock insuffisant")
    void testCreate_InsufficientStock() {
        // Given
        produit.setStockActuel(30); // stock insuffisant (demande de 50)
        
        when(fournisseurRepository.findById(1L)).thenReturn(Optional.of(fournisseur));
        when(produitRepository.findById(1L)).thenReturn(Optional.of(produit));
        when(commandeRepository.save(any(CommandeFournisseur.class))).thenReturn(commande);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, 
                () -> commandeService.create(commandeDTO)
        );
        
        assertTrue(exception.getMessage().contains("Stock insuffisant"));
        verify(produitRepository, never()).save(any(Produit.class));
    }

    @Test
    @DisplayName("Doit changer le statut de la commande")
    void testChangerStatut_Success() {
        // Given
        Long id = 1L;
        StatutCommande nouveauStatut = StatutCommande.VALIDEE;
        
        commande.setStatut(StatutCommande.EN_ATTENTE);
        commande.setCommandeProduits(new ArrayList<>());
        
        when(commandeRepository.findById(id)).thenReturn(Optional.of(commande));
        when(commandeRepository.save(any(CommandeFournisseur.class))).thenReturn(commande);
        when(commandeMapper.toDetailDTO(any(CommandeFournisseur.class))).thenReturn(detailDTO);

        // When
        CommandeFournisseurDetailDTO result = commandeService.changerStatut(id, nouveauStatut);

        // Then
        assertNotNull(result);
        ArgumentCaptor<CommandeFournisseur> commandeCaptor = ArgumentCaptor.forClass(CommandeFournisseur.class);
        verify(commandeRepository, times(1)).save(commandeCaptor.capture());
        assertEquals(nouveauStatut, commandeCaptor.getValue().getStatut());
    }

    @Test
    @DisplayName("Doit créer des mouvements de stock lors de la livraison")
    void testChangerStatut_ToLivree_CreateMouvements() {
        // Given
        Long id = 1L;
        
        CommandeProduit commandeProduit = CommandeProduit.builder()
                .commande(commande)
                .produit(produit)
                .quantite(50)
                .prixUnitaireCommande(new BigDecimal("5500.00"))
                .build();
        
        commande.setCommandeProduits(Arrays.asList(commandeProduit));
        
        when(commandeRepository.findById(id)).thenReturn(Optional.of(commande));
        when(commandeRepository.save(any(CommandeFournisseur.class))).thenReturn(commande);
        when(mouvementStockRepository.save(any(MouvementStock.class))).thenReturn(new MouvementStock());
        when(commandeMapper.toDetailDTO(any(CommandeFournisseur.class))).thenReturn(detailDTO);

        // When
        commandeService.changerStatut(id, StatutCommande.LIVREE);

        // Then
        verify(mouvementStockRepository, times(1)).save(any(MouvementStock.class));
        ArgumentCaptor<MouvementStock> mouvementCaptor = ArgumentCaptor.forClass(MouvementStock.class);
        verify(mouvementStockRepository).save(mouvementCaptor.capture());
        MouvementStock mouvement = mouvementCaptor.getValue();
        assertEquals(TypeMouvement.SORTIE, mouvement.getTypeMouvement());
        assertEquals(50, mouvement.getQuantite());
    }

    @Test
    @DisplayName("Doit retourner les commandes d'un fournisseur")
    void testFindByFournisseur() {
        // Given
        Long fournisseurId = 1L;
        List<CommandeFournisseur> commandes = Arrays.asList(commande);
        
        when(fournisseurRepository.findById(fournisseurId)).thenReturn(Optional.of(fournisseur));
        when(commandeRepository.findByFournisseur(fournisseur)).thenReturn(commandes);
        when(commandeMapper.toDTO(any(CommandeFournisseur.class))).thenReturn(commandeDTO);

        // When
        List<CommandeFournisseurDTO> result = commandeService.findByFournisseur(fournisseurId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(fournisseurRepository, times(1)).findById(fournisseurId);
        verify(commandeRepository, times(1)).findByFournisseur(fournisseur);
    }

}

