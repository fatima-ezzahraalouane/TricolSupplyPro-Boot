package com.tricol.supply.repository;

import com.tricol.supply.model.entity.MouvementStock;
import com.tricol.supply.model.entity.Produit;
import com.tricol.supply.model.entity.CommandeFournisseur;
import com.tricol.supply.model.enums.TypeMouvement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MouvementStockRepository extends JpaRepository<MouvementStock, Long> {
    List<MouvementStock> findByProduit(Produit produit);
    List<MouvementStock> findByProduitAndTypeMouvement(Produit produit, TypeMouvement typeMouvement);
    List<MouvementStock> findByCommandeFournisseur(CommandeFournisseur commandeFournisseur);
    
    Page<MouvementStock> findByProduit(Produit produit, Pageable pageable);
    Page<MouvementStock> findByProduitAndTypeMouvement(Produit produit, TypeMouvement typeMouvement, Pageable pageable);
    Page<MouvementStock> findByTypeMouvement(TypeMouvement typeMouvement, Pageable pageable);
    Page<MouvementStock> findByCommandeFournisseur(CommandeFournisseur commandeFournisseur, Pageable pageable);
}

