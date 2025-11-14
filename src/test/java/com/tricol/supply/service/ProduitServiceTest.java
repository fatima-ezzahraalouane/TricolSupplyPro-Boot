package com.tricol.supply.service;

import com.tricol.supply.dto.ProduitDTO;
import com.tricol.supply.exception.ResourceNotFoundException;
import com.tricol.supply.mapper.ProduitMapper;
import com.tricol.supply.model.entity.MouvementStock;
import com.tricol.supply.model.entity.Produit;
import com.tricol.supply.model.enums.TypeMouvement;
import com.tricol.supply.repository.MouvementStockRepository;
import com.tricol.supply.repository.ProduitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires pour ProduitService")
class ProduitServiceTest {

    @Mock
    private ProduitRepository produitRepository;

    @Mock
    private ProduitMapper produitMapper;

    @Mock
    private MouvementStockRepository mouvementStockRepository;

    @InjectMocks
    private ProduitService produitService;

    private Produit produit;
    private ProduitDTO produitDTO;

    
}

