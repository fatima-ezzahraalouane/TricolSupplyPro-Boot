package com.tricol.supply.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FournisseurDTO {
    
    private Long id;
    
    @NotBlank(message = "Le nom de la société est obligatoire")
    private String societe;
    
    private String adresse;
    
    private String contact;
    
    @Email(message = "Email invalide")
    private String email;
    
    private String telephone;
    
    private String ville;
    
    private String ice;
}

