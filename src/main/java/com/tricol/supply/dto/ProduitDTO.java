package com.tricol.supply.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProduitDTO {
    
    private Long id;
    
    @NotBlank(message = "Le nom du produit est obligatoire")
    private String nom;
    
    private String description;
    
    @DecimalMin(value = "0.0", message = "Le prix unitaire doit être positif ou nul")
    private BigDecimal prixUnitaire;
    
    private String categorie;
    
    private Integer stockActuel;
    
    private BigDecimal coutUnitaireMoyen;
}

