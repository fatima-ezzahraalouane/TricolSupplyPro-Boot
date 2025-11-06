package com.tricol.supply.mapper;

import com.tricol.supply.dto.ProduitDTO;
import com.tricol.supply.model.entity.Produit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProduitMapper {
    
    ProduitDTO toDTO(Produit produit);
    
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "mouvements", ignore = true)
    Produit toEntity(ProduitDTO produitDTO);
    
    
}

