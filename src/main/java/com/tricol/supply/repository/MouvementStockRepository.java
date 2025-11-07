package com.tricol.supply.repository;

import com.tricol.supply.model.entity.MouvementStock;
import com.tricol.supply.model.entity.Produit;
import com.tricol.supply.model.entity.CommandeFournisseur;
import com.tricol.supply.model.enums.TypeMouvement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MouvementStockRepository extends JpaRepository<MouvementStock, Long> {
    
}

