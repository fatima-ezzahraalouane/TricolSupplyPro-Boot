package com.tricol.supply.controller;

import com.tricol.supply.dto.MouvementStockDTO;
import com.tricol.supply.service.MouvementStockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/mouvements")
@RequiredArgsConstructor
@Tag(name = "Mouvements de Stock", description = "Gestion des mouvements de stock")
public class MouvementStockController {
    
    private final MouvementStockService mouvementService;
    
    @GetMapping("/produit/{produitId}")
    @Operation(summary = "Mouvements d'un produit", description = "Retourne tous les mouvements de stock pour un produit")
    public ResponseEntity<List<MouvementStockDTO>> getMouvementsByProduit(@PathVariable Long produitId) {
        List<MouvementStockDTO> mouvements = mouvementService.findByProduit(produitId);
        return ResponseEntity.ok(mouvements);
    }
    
    
}

