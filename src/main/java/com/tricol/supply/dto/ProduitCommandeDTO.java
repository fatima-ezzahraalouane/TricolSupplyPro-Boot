package com.tricol.supply.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProduitCommandeDTO {
    
    @NotNull(message = "L'ID du produit est obligatoire")
    private Long produitId;
    
    @Min(value = 1, message = "La quantité doit être supérieure à 0")
    @NotNull(message = "La quantité est obligatoire")
    private Integer quantite;
    
    @NotNull(message = "Le prix unitaire de la commande est obligatoire")
    private java.math.BigDecimal prixUnitaireCommande;
}

