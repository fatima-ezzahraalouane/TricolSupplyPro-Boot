package com.tricol.supply.controller;

import com.tricol.supply.dto.ProduitDTO;
import com.tricol.supply.service.ProduitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/produits")
@RequiredArgsConstructor
@Tag(name = "Produits", description = "Gestion des produits")
public class ProduitController {
    
    private final ProduitService produitService;
    
    @GetMapping
    @Operation(summary = "Liste des produits", description = "Retourne une liste paginée de tous les produits")
    public ResponseEntity<Page<ProduitDTO>> getAllProduits(
        @PageableDefault(size = 10, sort = "id") Pageable pageable
    ) {
        Page<ProduitDTO> produits = produitService.findAll(pageable);
        return ResponseEntity.ok(produits);
    }
    
    
}

