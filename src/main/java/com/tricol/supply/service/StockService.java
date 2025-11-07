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
    
    
    
}

