package com.tricol.supply.controller;

import com.tricol.supply.dto.CommandeFournisseurDTO;
import com.tricol.supply.dto.CommandeFournisseurDetailDTO;
import com.tricol.supply.model.enums.StatutCommande;
import com.tricol.supply.service.CommandeFournisseurService;
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

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/commandes")
@RequiredArgsConstructor
@Tag(name = "Commandes Fournisseurs", description = "Gestion des commandes fournisseurs")
public class CommandeFournisseurController {
    
    private final CommandeFournisseurService commandeService;
    
    @GetMapping
    @Operation(summary = "Liste des commandes", description = "Retourne une liste paginée de toutes les commandes")
    public ResponseEntity<Page<CommandeFournisseurDTO>> getAllCommandes(
        @PageableDefault(size = 10, sort = "id") Pageable pageable
    ) {
        Page<CommandeFournisseurDTO> commandes = commandeService.findAll(pageable);
        return ResponseEntity.ok(commandes);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Détails d'une commande", description = "Retourne les détails d'une commande par son ID")
    public ResponseEntity<CommandeFournisseurDetailDTO> getCommandeById(@PathVariable Long id) {
        CommandeFournisseurDetailDTO commande = commandeService.findById(id);
        return ResponseEntity.ok(commande);
    }
    
    @GetMapping("/fournisseur/{id}")
    @Operation(summary = "Commandes d'un fournisseur", description = "Retourne toutes les commandes d'un fournisseur spécifique")
    public ResponseEntity<List<CommandeFournisseurDTO>> getCommandesByFournisseur(@PathVariable Long id) {
        List<CommandeFournisseurDTO> commandes = commandeService.findByFournisseur(id);
        return ResponseEntity.ok(commandes);
    }
    
    @PostMapping
    @Operation(summary = "Créer une commande", description = "Crée une nouvelle commande fournisseur")
    public ResponseEntity<CommandeFournisseurDetailDTO> createCommande(@Valid @RequestBody CommandeFournisseurDTO commandeDTO) {
        CommandeFournisseurDetailDTO created = commandeService.create(commandeDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    
}

