package com.tricol.supply.controller;

import com.tricol.supply.dto.FournisseurDTO;
import com.tricol.supply.service.FournisseurService;
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
@RequestMapping("/api/v1/fournisseurs")
@RequiredArgsConstructor
@Tag(name = "Fournisseurs", description = "Gestion des fournisseurs")
public class FournisseurController {
    
    private final FournisseurService fournisseurService;
    
    @GetMapping
    @Operation(summary = "Liste des fournisseurs", description = "Retourne une liste paginée de tous les fournisseurs")
    public ResponseEntity<Page<FournisseurDTO>> getAllFournisseurs(
        @PageableDefault(size = 10, sort = "id") Pageable pageable
    ) {
        Page<FournisseurDTO> fournisseurs = fournisseurService.findAll(pageable);
        return ResponseEntity.ok(fournisseurs);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Détails d'un fournisseur", description = "Retourne les détails d'un fournisseur par son ID")
    public ResponseEntity<FournisseurDTO> getFournisseurById(@PathVariable Long id) {
        FournisseurDTO fournisseur = fournisseurService.findById(id);
        return ResponseEntity.ok(fournisseur);
    }
    
    @PostMapping
    @Operation(summary = "Créer un fournisseur", description = "Crée un nouveau fournisseur")
    public ResponseEntity<FournisseurDTO> createFournisseur(@Valid @RequestBody FournisseurDTO fournisseurDTO) {
        FournisseurDTO created = fournisseurService.create(fournisseurDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Modifier un fournisseur", description = "Met à jour un fournisseur existant")
    public ResponseEntity<FournisseurDTO> updateFournisseur(
        @PathVariable Long id,
        @Valid @RequestBody FournisseurDTO fournisseurDTO
    ) {
        FournisseurDTO updated = fournisseurService.update(id, fournisseurDTO);
        return ResponseEntity.ok(updated);
    }
    
    
}

