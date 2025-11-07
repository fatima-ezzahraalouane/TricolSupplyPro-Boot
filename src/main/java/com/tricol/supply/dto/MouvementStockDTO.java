package com.tricol.supply.dto;

import com.tricol.supply.model.enums.TypeMouvement;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class MouvementStockDTO {
    
    private Long id;
    private LocalDateTime dateMouvement;
    private TypeMouvement typeMouvement;
    private Integer quantite;
    private BigDecimal prixUnitaire;
    private Long produitId;
    private String nomProduit;
    private Long commandeFournisseurId;
    private LocalDateTime createdAt;
}

