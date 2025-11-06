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
    
    
}

