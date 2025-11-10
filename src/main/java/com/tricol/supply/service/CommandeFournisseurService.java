package com.tricol.supply.service;

import com.tricol.supply.dto.*;
import com.tricol.supply.model.entity.*;
import com.tricol.supply.model.enums.StatutCommande;
import com.tricol.supply.model.enums.TypeMouvement;
import com.tricol.supply.exception.ResourceNotFoundException;
import com.tricol.supply.mapper.CommandeFournisseurMapper;
import com.tricol.supply.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommandeFournisseurService {
    
    private final CommandeFournisseurRepository commandeRepository;
    private final FournisseurRepository fournisseurRepository;
    private final ProduitRepository produitRepository;
    private final CommandeProduitRepository commandeProduitRepository;
    private final MouvementStockRepository mouvementStockRepository;
    private final CommandeFournisseurMapper commandeMapper;
    private final StockService stockService;
    
    public Page<CommandeFournisseurDTO> findAll(Pageable pageable) {
        return commandeRepository.findAll(pageable)
            .map(commandeMapper::toDTO);
    }
    
    public CommandeFournisseurDetailDTO findById(Long id) {
        CommandeFournisseur commande = commandeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Commande", id));
        return commandeMapper.toDetailDTO(commande);
    }
    
    @Transactional
    public CommandeFournisseurDetailDTO create(CommandeFournisseurDTO dto) {
        // verifier que le fournisseur existe
        Fournisseur fournisseur = fournisseurRepository.findById(dto.getFournisseurId())
            .orElseThrow(() -> new ResourceNotFoundException("Fournisseur", dto.getFournisseurId()));
        
        // creer la commande
        CommandeFournisseur commande = CommandeFournisseur.builder()
            .dateCommande(LocalDateTime.now())
            .statut(StatutCommande.EN_ATTENTE)
            .fournisseur(fournisseur)
            .montantTotal(BigDecimal.ZERO)
            .build();
        
        commande = commandeRepository.save(commande);
        
        // ajouter les produits a la commande
        List<CommandeProduit> commandeProduits = new ArrayList<>();
        BigDecimal montantTotal = BigDecimal.ZERO;
        
        for (ProduitCommandeDTO produitDTO : dto.getProduits()) {
            Produit produit = produitRepository.findById(produitDTO.getProduitId())
                .orElseThrow(() -> new ResourceNotFoundException("Produit", produitDTO.getProduitId()));
            
            // verifier qu'il y a assez de stock disponible
            if (produit.getStockActuel() < produitDTO.getQuantite()) {
                throw new IllegalArgumentException(
                    "Stock insuffisant pour le produit " + produit.getNom() + 
                    ". Disponible: " + produit.getStockActuel() + 
                    ", Demandé: " + produitDTO.getQuantite()
                );
            }
            
            // diminuer le stock lors de la création de la commande (reservation)
            produit.setStockActuel(produit.getStockActuel() - produitDTO.getQuantite());
            produitRepository.save(produit);
            
            CommandeProduit cp = CommandeProduit.builder()
                .commande(commande)
                .produit(produit)
                .quantite(produitDTO.getQuantite())
                .prixUnitaireCommande(produitDTO.getPrixUnitaireCommande())
                .build();
            
            commandeProduits.add(cp);
            
            // calculer le montant total
            BigDecimal montantLigne = produitDTO.getPrixUnitaireCommande()
                .multiply(BigDecimal.valueOf(produitDTO.getQuantite()));
            montantTotal = montantTotal.add(montantLigne);
        }
        
        // sauvegarder les produits de la commande
        commandeProduitRepository.saveAll(commandeProduits);
        
        // mettre a jour le montant total
        commande.setMontantTotal(montantTotal);
        commande = commandeRepository.save(commande);
        
        return commandeMapper.toDetailDTO(commande);
    }
    
    @Transactional
    public CommandeFournisseurDetailDTO update(Long id, CommandeFournisseurDTO dto) {
        CommandeFournisseur existing = commandeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Commande", id));
        
        // verifier qu'on ne peut modifier que les commandes EN_ATTENTE
        if (existing.getStatut() != StatutCommande.EN_ATTENTE) {
            throw new IllegalArgumentException("Seules les commandes en attente peuvent être modifiées");
        }
        
        // verifier le fournisseur
        Fournisseur fournisseur = fournisseurRepository.findById(dto.getFournisseurId())
            .orElseThrow(() -> new ResourceNotFoundException("Fournisseur", dto.getFournisseurId()));
        
        existing.setFournisseur(fournisseur);
        existing.setStatut(dto.getStatut());
        
        // supprimer les anciens produits
        commandeProduitRepository.deleteAll(existing.getCommandeProduits());
        
        // ajouter les nouveaux produits
        List<CommandeProduit> commandeProduits = new ArrayList<>();
        BigDecimal montantTotal = BigDecimal.ZERO;
        
        for (ProduitCommandeDTO produitDTO : dto.getProduits()) {
            Produit produit = produitRepository.findById(produitDTO.getProduitId())
                .orElseThrow(() -> new ResourceNotFoundException("Produit", produitDTO.getProduitId()));
            
            CommandeProduit cp = CommandeProduit.builder()
                .commande(existing)
                .produit(produit)
                .quantite(produitDTO.getQuantite())
                .prixUnitaireCommande(produitDTO.getPrixUnitaireCommande())
                .build();
            
            commandeProduits.add(cp);
            
            BigDecimal montantLigne = produitDTO.getPrixUnitaireCommande()
                .multiply(BigDecimal.valueOf(produitDTO.getQuantite()));
            montantTotal = montantTotal.add(montantLigne);
        }
        
        commandeProduitRepository.saveAll(commandeProduits);
        existing.setMontantTotal(montantTotal);
        
        CommandeFournisseur updated = commandeRepository.save(existing);
        return commandeMapper.toDetailDTO(updated);
    }

    @Transactional
    public void delete(Long id) {
        CommandeFournisseur commande = commandeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Commande", id));
        
        // verifier qu'on ne peut supprimer que les commandes EN_ATTENTE ou ANNULEES
        if (commande.getStatut() != StatutCommande.EN_ATTENTE && commande.getStatut() != StatutCommande.ANNULEE) {
            throw new IllegalArgumentException("Seules les commandes en attente ou annulées peuvent être supprimées");
        }
        
        commandeRepository.deleteById(id);
    }

    @Transactional
    public CommandeFournisseurDetailDTO changerStatut(Long id, StatutCommande nouveauStatut) {
        CommandeFournisseur commande = commandeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Commande", id));
        
        StatutCommande ancienStatut = commande.getStatut();
        
        // gerer automatiquement les mouvements de stock lors de la livraison
        if (nouveauStatut == StatutCommande.LIVREE && ancienStatut != StatutCommande.LIVREE) {
            creerMouvementsStockPourLivraison(commande);
        }
        
        // si la commande est annulee, remettre le stock (annuler la reservation)
        if (nouveauStatut == StatutCommande.ANNULEE && ancienStatut != StatutCommande.ANNULEE && ancienStatut != StatutCommande.LIVREE) {
            restaurerStockPourAnnulation(commande);
        }
        
        commande.setStatut(nouveauStatut);
        CommandeFournisseur updated = commandeRepository.save(commande);
        
        return commandeMapper.toDetailDTO(updated);
    }

    private void creerMouvementsStockPourLivraison(CommandeFournisseur commande) {
        for (CommandeProduit cp : commande.getCommandeProduits()) {
            MouvementStock mouvement = MouvementStock.builder()
                .dateMouvement(LocalDateTime.now())
                .typeMouvement(TypeMouvement.SORTIE) // SORTIE car c'est une livraison au fournisseur (vente)
                .quantite(cp.getQuantite())
                .prixUnitaire(cp.getPrixUnitaireCommande())
                .produit(cp.getProduit())
                .commandeFournisseur(commande)
                .build();
            
            mouvementStockRepository.save(mouvement);
        }
    }

    private void restaurerStockPourAnnulation(CommandeFournisseur commande) {
        // remettre le stock qui avait été reserve lors de la creation de la commande
        for (CommandeProduit cp : commande.getCommandeProduits()) {
            Produit produit = cp.getProduit();
            produit.setStockActuel(produit.getStockActuel() + cp.getQuantite());
            produitRepository.save(produit);
            
            // creer un mouvement AJUSTEMENT pour tracer cette restauration
            MouvementStock mouvement = MouvementStock.builder()
                .dateMouvement(LocalDateTime.now())
                .typeMouvement(TypeMouvement.AJUSTEMENT)
                .quantite(cp.getQuantite())
                .prixUnitaire(cp.getPrixUnitaireCommande())
                .produit(produit)
                .commandeFournisseur(commande)
                .build();
            
            mouvementStockRepository.save(mouvement);
        }
    }

    public List<CommandeFournisseurDTO> findByFournisseur(Long fournisseurId) {
        Fournisseur fournisseur = fournisseurRepository.findById(fournisseurId)
            .orElseThrow(() -> new ResourceNotFoundException("Fournisseur", fournisseurId));
        
        List<CommandeFournisseur> commandes = commandeRepository.findByFournisseur(fournisseur);
        return commandes.stream()
            .map(commandeMapper::toDTO)
            .toList();
    }
   }

