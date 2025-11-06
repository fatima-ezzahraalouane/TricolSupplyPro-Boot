package com.tricol.supply.service;

import com.tricol.supply.dto.FournisseurDTO;
import com.tricol.supply.model.entity.Fournisseur;
import com.tricol.supply.exception.ResourceNotFoundException;
import com.tricol.supply.mapper.FournisseurMapper;
import com.tricol.supply.repository.FournisseurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FournisseurService {
    
    private final FournisseurRepository fournisseurRepository;
    private final FournisseurMapper fournisseurMapper;
    
    public Page<FournisseurDTO> findAll(Pageable pageable) {
        return fournisseurRepository.findAll(pageable)
            .map(fournisseurMapper::toDTO);
    }
    
    public FournisseurDTO findById(Long id) {
        Fournisseur fournisseur = fournisseurRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Fournisseur", id));
        return fournisseurMapper.toDTO(fournisseur);
    }
    
    @Transactional
    public FournisseurDTO create(FournisseurDTO dto) {
        Fournisseur fournisseur = fournisseurMapper.toEntity(dto);
        Fournisseur saved = fournisseurRepository.save(fournisseur);
        return fournisseurMapper.toDTO(saved);
    }
    
    
}

