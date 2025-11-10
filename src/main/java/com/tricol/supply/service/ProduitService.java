package com.tricol.supply.service;

import com.tricol.supply.dto.ProduitDTO;
import com.tricol.supply.model.entity.MouvementStock;
import com.tricol.supply.model.entity.Produit;
import com.tricol.supply.model.enums.TypeMouvement;
import com.tricol.supply.exception.ResourceNotFoundException;
import com.tricol.supply.mapper.ProduitMapper;
import com.tricol.supply.repository.MouvementStockRepository;
import com.tricol.supply.repository.ProduitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public void delete(Long id) {
        if (!produitRepository.existsById(id)) {
            throw new ResourceNotFoundException("Produit", id);
        }
        produitRepository.deleteById(id);
    }
}

