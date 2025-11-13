package com.tricol.supply.service;

import com.tricol.supply.dto.ProduitDTO;
import com.tricol.supply.exception.ResourceNotFoundException;
import com.tricol.supply.mapper.ProduitMapper;
import com.tricol.supply.model.entity.MouvementStock;
import com.tricol.supply.model.entity.Produit;
import com.tricol.supply.model.enums.TypeMouvement;
import com.tricol.supply.repository.MouvementStockRepository;
import com.tricol.supply.repository.ProduitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProduitService {
    
    private final ProduitRepository produitRepository;
    private final ProduitMapper produitMapper;
    private final MouvementStockRepository mouvementStockRepository;
    
    public Page<ProduitDTO> findAll(Pageable pageable) {
        return produitRepository.findAll(pageable)
            .map(produitMapper::toDTO);
    }
    
    public ProduitDTO findById(Long id) {
        Produit produit = produitRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Produit", id));
        return produitMapper.toDTO(produit);
    }
    
    @Transactional
    public ProduitDTO create(ProduitDTO dto) {
        Produit produit = produitMapper.toEntity(dto);

        int stockInitial = safeQuantity(produit.getStockActuel());
        BigDecimal prixInitial = safeAmount(produit.getPrixUnitaire());
        BigDecimal coutInitial = stockInitial > 0 ? prixInitial : BigDecimal.ZERO;

        produit.setCoutUnitaireMoyen(coutInitial);

        Produit saved = produitRepository.save(produit);
        
        // creer un mouvement ENTREE automatique lors de l'ajout d'un produit
        if (saved.getStockActuel() != null && saved.getStockActuel() > 0) {
            MouvementStock mouvement = MouvementStock.builder()
                .dateMouvement(LocalDateTime.now())
                .typeMouvement(TypeMouvement.ENTREE)
                .quantite(saved.getStockActuel())
                .prixUnitaire(saved.getPrixUnitaire())
                .produit(saved)
                .commandeFournisseur(null) // pas de commande associée
                .build();
            
            mouvementStockRepository.save(mouvement);
        }
        
        return produitMapper.toDTO(saved);
    }
    
    @Transactional
    public ProduitDTO update(Long id, ProduitDTO dto) {
        Produit existing = produitRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Produit", id));
        
        // sauvegarder l'ancien stock pour detecter les modifications manuelles
        int stockAvant = safeQuantity(existing.getStockActuel());
        BigDecimal ancienCump = safeAmount(existing.getCoutUnitaireMoyen());
        BigDecimal ancienPrixUnitaire = safeAmount(existing.getPrixUnitaire());
        
        existing.setNom(dto.getNom());
        existing.setDescription(dto.getDescription());
        existing.setPrixUnitaire(dto.getPrixUnitaire());
        existing.setCategorie(dto.getCategorie());
        
        // si le stockActuel a été modifié manuellement, creer un mouvement AJUSTEMENT
        if (dto.getStockActuel() != null && stockAvant != dto.getStockActuel()) {
            int nouveauStock = dto.getStockActuel();
            int difference = nouveauStock - stockAvant;

            BigDecimal prixOperation = safeAmount(dto.getPrixUnitaire());
            if (prixOperation.compareTo(BigDecimal.ZERO) == 0) {
                prixOperation = ancienPrixUnitaire;
            }

            BigDecimal nouveauCump = recalculerCoutUnitaireMoyen(stockAvant, ancienCump, difference, prixOperation);
            existing.setCoutUnitaireMoyen(nouveauCump);
            existing.setStockActuel(dto.getStockActuel());

            MouvementStock mouvement = MouvementStock.builder()
                .dateMouvement(LocalDateTime.now())
                .typeMouvement(TypeMouvement.AJUSTEMENT)
                .quantite(Math.abs(difference))
                .prixUnitaire(prixOperation)
                .produit(existing)
                .commandeFournisseur(null)
                .build();
            
            mouvementStockRepository.save(mouvement);
        }
        
        Produit updated = produitRepository.save(existing);
        return produitMapper.toDTO(updated);
    }
    
    @Transactional
    public void delete(Long id) {
        if (!produitRepository.existsById(id)) {
            throw new ResourceNotFoundException("Produit", id);
        }
        produitRepository.deleteById(id);
    }

    private BigDecimal recalculerCoutUnitaireMoyen(int ancienStock, BigDecimal ancienCoutMoyen, int variationStock, BigDecimal prixOperation) {
        int stockApresOperation = ancienStock + variationStock;

        if (stockApresOperation <= 0) {
            return BigDecimal.ZERO;
        }

        if (variationStock <= 0) {
            return ancienCoutMoyen;
        }

        BigDecimal totalAncien = ancienCoutMoyen.multiply(BigDecimal.valueOf(ancienStock));
        BigDecimal totalAjout = prixOperation.multiply(BigDecimal.valueOf(variationStock));

        return totalAncien.add(totalAjout)
            .divide(BigDecimal.valueOf(stockApresOperation), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal safeAmount(BigDecimal valeur) {
        return valeur != null ? valeur : BigDecimal.ZERO;
    }

    private int safeQuantity(Integer quantite) {
        return quantite != null ? quantite : 0;
    }
}

