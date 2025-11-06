package com.tricol.supply.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "produits")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Produit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String nom;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "prix_unitaire", nullable = false, precision = 19, scale = 2)
    private BigDecimal prixUnitaire;

    @Column(length = 100)
    private String categorie;

    @Column(name = "stock_actuel")
    @Builder.Default
    private Integer stockActuel = 0;

    @Column(name = "cout_unitaire_moyen", precision = 19, scale = 2)
    @Builder.Default
    private BigDecimal coutUnitaireMoyen = BigDecimal.ZERO;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "produit", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MouvementStock> mouvements = new ArrayList<>();

    
}

