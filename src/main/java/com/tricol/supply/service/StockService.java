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
    
    
    
    
}

