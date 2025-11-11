package com.tricol.supply.service;

import com.tricol.supply.dto.MouvementStockDTO;
import com.tricol.supply.model.entity.CommandeFournisseur;
import com.tricol.supply.model.entity.MouvementStock;
import com.tricol.supply.model.entity.Produit;
import com.tricol.supply.exception.ResourceNotFoundException;
import com.tricol.supply.mapper.MouvementStockMapper;
import com.tricol.supply.repository.CommandeFournisseurRepository;
import com.tricol.supply.repository.MouvementStockRepository;
import com.tricol.supply.repository.ProduitRepository;
import lombok.RequiredArgsConstructor;
import com.tricol.supply.model.enums.TypeMouvement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MouvementStockService {
    
    private final MouvementStockRepository mouvementStockRepository;
    private final ProduitRepository produitRepository;
    private final CommandeFournisseurRepository commandeRepository;
    private final MouvementStockMapper mouvementMapper;
    
    public Page<MouvementStockDTO> findAll(Pageable pageable) {
        return mouvementStockRepository.findAll(pageable)
            .map(mouvementMapper::toDTO);
    }
    
    public Page<MouvementStockDTO> findByFilters(Long produitId, TypeMouvement type, Pageable pageable) {
        if (produitId != null) {
            Produit produit = produitRepository.findById(produitId)
                .orElseThrow(() -> new ResourceNotFoundException("Produit", produitId));
            
            if (type != null) {
                return mouvementStockRepository.findByProduitAndTypeMouvement(produit, type, pageable)
                    .map(mouvementMapper::toDTO);
            }
            return mouvementStockRepository.findByProduit(produit, pageable)
                .map(mouvementMapper::toDTO);
        }
        
        if (type != null) {
            return mouvementStockRepository.findByTypeMouvement(type, pageable)
                .map(mouvementMapper::toDTO);
        }
        
        return findAll(pageable);
    }
    
    public Page<MouvementStockDTO> findByCommande(Long commandeId, Pageable pageable) {
        CommandeFournisseur commande = commandeRepository.findById(commandeId)
            .orElseThrow(() -> new ResourceNotFoundException("Commande", commandeId));
        
        return mouvementStockRepository.findByCommandeFournisseur(commande, pageable)
            .map(mouvementMapper::toDTO);
    }
    
    public List<MouvementStockDTO> findByProduit(Long produitId) {
        Produit produit = produitRepository.findById(produitId)
            .orElseThrow(() -> new ResourceNotFoundException("Produit", produitId));
        
        List<MouvementStock> mouvements = mouvementStockRepository.findByProduit(produit);
        return mouvementMapper.toDTOList(mouvements);
    }
    
    public List<MouvementStockDTO> findByCommande(Long commandeId) {
        CommandeFournisseur commande = commandeRepository.findById(commandeId)
            .orElseThrow(() -> new ResourceNotFoundException("Commande", commandeId));
        
        List<MouvementStock> mouvements = mouvementStockRepository.findByCommandeFournisseur(commande);
        return mouvementMapper.toDTOList(mouvements);
    }
}

