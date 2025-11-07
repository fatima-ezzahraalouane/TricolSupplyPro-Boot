package com.tricol.supply.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;


public enum TypeMouvement {
    ENTREE("Entrée"),
    SORTIE("Sortie"),
    AJUSTEMENT("Ajustement");

    private final String libelle;

    TypeMouvement(String libelle) {
        this.libelle = libelle;
    }

    
}

