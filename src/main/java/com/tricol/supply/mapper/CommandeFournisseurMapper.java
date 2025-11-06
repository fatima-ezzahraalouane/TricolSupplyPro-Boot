package com.tricol.supply.mapper;

import com.tricol.supply.dto.*;
import com.tricol.supply.model.entity.CommandeFournisseur;
import com.tricol.supply.model.entity.CommandeProduit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {FournisseurMapper.class})
public interface CommandeFournisseurMapper {
    
    @Mapping(target = "produits", expression = "java(mapProduits(commande))")
    @Mapping(target = "fournisseurId", source = "fournisseur.id")
    CommandeFournisseurDTO toDTO(CommandeFournisseur commande);
    
    @Mapping(target = "fournisseur", ignore = true)
    @Mapping(target = "mouvements", ignore = true)
    @Mapping(target = "commandeProduits", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    CommandeFournisseur toEntity(CommandeFournisseurDTO commandeDTO);
    
    @Mapping(target = "fournisseur", source = "fournisseur")
    @Mapping(target = "produits", expression = "java(mapProduits(commande))")
    CommandeFournisseurDetailDTO toDetailDTO(CommandeFournisseur commande);
    
    
}

