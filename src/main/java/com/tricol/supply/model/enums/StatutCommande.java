package com.tricol.supply.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Énumération des statuts d'une commande fournisseur
 */
public enum StatutCommande {
    EN_ATTENTE("En attente"),
    VALIDEE("Validée"),
    LIVREE("Livrée"),
    ANNULEE("Annulée");

    private final String libelle;

    StatutCommande(String libelle) {
        this.libelle = libelle;
    }

    @JsonValue
    public String getLibelle() {
        return libelle;
    }

    @Override
    public String toString() {
        return libelle;
    }
}

