package com.tricol.supply.repository;

import com.tricol.supply.model.entity.CommandeFournisseur;
import com.tricol.supply.model.entity.CommandeProduit;
import com.tricol.supply.model.entity.CommandeProduitId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommandeProduitRepository extends JpaRepository<CommandeProduit, CommandeProduitId> {
    void deleteByCommande(CommandeFournisseur commande);
}

