package com.tricol.supply.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "commandes_produits")
@IdClass(CommandeProduitId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommandeProduit {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commande_id", nullable = false)
    private CommandeFournisseur commande;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produit_id", nullable = false)
    private Produit produit;

    
}

