package com.tricol.supply.mapper;

import com.tricol.supply.dto.FournisseurDTO;
import com.tricol.supply.model.entity.Fournisseur;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FournisseurMapper {
    
    FournisseurDTO toDTO(Fournisseur fournisseur);
    
    
}

