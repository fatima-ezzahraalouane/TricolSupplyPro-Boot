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

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
    
    public List<ProduitDTO> findAll() {
        return produitRepository.findAll().stream()
            .map(produitMapper::toDTO)
            .collect(Collectors.toList());
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
    public ProduitDTO update(Long id, ProduitDTO dto) {
        Produit existing = produitRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Produit", id));
        
        // sauvegarder l'ancien stock pour detecter les modifications manuelles
        Integer ancienStock = existing.getStockActuel();
        
        existing.setNom(dto.getNom());
        existing.setDescription(dto.getDescription());
        existing.setPrixUnitaire(dto.getPrixUnitaire());
        existing.setCategorie(dto.getCategorie());
        
        // si le stockActuel a été modifié manuellement, creer un mouvement AJUSTEMENT
        if (dto.getStockActuel() != null && !dto.getStockActuel().equals(ancienStock)) {
            int difference = dto.getStockActuel() - ancienStock;
            
            existing.setStockActuel(dto.getStockActuel());
            
            MouvementStock mouvement = MouvementStock.builder()
                .dateMouvement(LocalDateTime.now())
                .typeMouvement(TypeMouvement.AJUSTEMENT)
                .quantite(Math.abs(difference))
                .prixUnitaire(existing.getPrixUnitaire())
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
}

