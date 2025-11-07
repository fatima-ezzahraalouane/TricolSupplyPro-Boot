package com.tricol.supply.mapper;

import com.tricol.supply.dto.MouvementStockDTO;
import com.tricol.supply.model.entity.MouvementStock;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MouvementStockMapper {
    
    @Mapping(target = "produitId", source = "produit.id")
    @Mapping(target = "nomProduit", source = "produit.nom")
    @Mapping(target = "commandeFournisseurId", expression = "java(mouvement.getCommandeFournisseur() != null ? mouvement.getCommandeFournisseur().getId() : null)")
    MouvementStockDTO toDTO(MouvementStock mouvement);
    
    
}

