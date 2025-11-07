package com.tricol.supply.service;

import com.tricol.supply.model.entity.Produit;
import com.tricol.supply.repository.ProduitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class StockService {
    
    private final ProduitRepository produitRepository;
    

    // met a jour le stock et recalcule le cout unitaire moyen (CUMP)
    @Transactional
    public void miseAJourStock(Produit produit, Integer quantiteEntree, BigDecimal prixUnitaire) {
        // mettre a jour la quantite en stock
        produit.setStockActuel(produit.getStockActuel() + quantiteEntree);
        
        // recalculer le cout unitaire moyen pondere
        calculerCUMP(produit, quantiteEntree, prixUnitaire);
        
        produitRepository.save(produit);
    }
    
    
    // calcule le cout unitaire moyen pondere (CUMP)
    private void calculerCUMP(Produit produit, Integer quantiteEntree, BigDecimal prixUnitaire) {
        // stock avant l'entree
        int stockAncien = produit.getStockActuel() - quantiteEntree;
        BigDecimal coutAncien = produit.getCoutUnitaireMoyen();
        
        // valeur stock ancien
        BigDecimal valeurAncienne = coutAncien.multiply(BigDecimal.valueOf(stockAncien));
        
        // valeur entree
        BigDecimal valeurEntree = prixUnitaire.multiply(BigDecimal.valueOf(quantiteEntree));
        
        // nouvelle valeur totale
        BigDecimal valeurTotale = valeurAncienne.add(valeurEntree);
        
        // nouveau stock
        int stockTotal = produit.getStockActuel();
        
        // nouveau CUMP
        if (stockTotal > 0) {
            BigDecimal cump = valeurTotale.divide(BigDecimal.valueOf(stockTotal), 2, RoundingMode.HALF_UP);
            produit.setCoutUnitaireMoyen(cump);
        }
    }
}

