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
    
    
}

