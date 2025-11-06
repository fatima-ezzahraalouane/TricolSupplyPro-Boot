package com.tricol.supply.dto;

import com.tricol.supply.model.enums.StatutCommande;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class CommandeFournisseurDTO {
    
    private Long id;
    
    private LocalDateTime dateCommande;
    
    @NotNull(message = "Le statut est obligatoire")
    private StatutCommande statut;
    
    private BigDecimal montantTotal;
    
    @NotNull(message = "Le fournisseur est obligatoire")
    private Long fournisseurId;
    
    @NotEmpty(message = "Au moins un produit doit être associé")
    private List<ProduitCommandeDTO> produits = new ArrayList<>();
}

