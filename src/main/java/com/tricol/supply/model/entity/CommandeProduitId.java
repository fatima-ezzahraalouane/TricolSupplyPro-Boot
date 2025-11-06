package com.tricol.supply.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommandeProduitId implements Serializable {

    private Long commande;
    private Long produit;
}

