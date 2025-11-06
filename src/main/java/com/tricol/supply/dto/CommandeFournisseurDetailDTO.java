package com.tricol.supply.dto;

import com.tricol.supply.model.enums.StatutCommande;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class CommandeFournisseurDetailDTO {
    
    private Long id;
    private LocalDateTime dateCommande;
    private StatutCommande statut;
    private BigDecimal montantTotal;
    
    
}

