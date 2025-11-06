package com.tricol.supply.service;

import com.tricol.supply.dto.ProduitDTO;
import com.tricol.supply.model.entity.Produit;
import com.tricol.supply.exception.ResourceNotFoundException;
import com.tricol.supply.mapper.ProduitMapper;
import com.tricol.supply.repository.ProduitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProduitService {
    
    private final ProduitRepository produitRepository;
    private final ProduitMapper produitMapper;
    
    public Page<ProduitDTO> findAll(Pageable pageable) {
        return produitRepository.findAll(pageable)
            .map(produitMapper::toDTO);
    }
    
    public ProduitDTO findById(Long id) {
        Produit produit = produitRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Produit", id));
        return produitMapper.toDTO(produit);
    }
    
    
}

