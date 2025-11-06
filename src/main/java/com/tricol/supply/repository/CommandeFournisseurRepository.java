package com.tricol.supply.repository;

import com.tricol.supply.model.entity.CommandeFournisseur;
import com.tricol.supply.model.entity.Fournisseur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommandeFournisseurRepository extends JpaRepository<CommandeFournisseur, Long> {
    List<CommandeFournisseur> findByFournisseur(Fournisseur fournisseur);
}

