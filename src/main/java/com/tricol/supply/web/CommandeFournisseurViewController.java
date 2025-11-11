package com.tricol.supply.web;

import com.tricol.supply.dto.CommandeFournisseurDTO;
import com.tricol.supply.dto.CommandeFournisseurDetailDTO;
import com.tricol.supply.dto.FournisseurDTO;
import com.tricol.supply.dto.ProduitCommandeDTO;
import com.tricol.supply.dto.ProduitDTO;
import com.tricol.supply.model.enums.StatutCommande;
import com.tricol.supply.service.CommandeFournisseurService;
import com.tricol.supply.service.FournisseurService;
import com.tricol.supply.service.ProduitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/ui/commandes")
@RequiredArgsConstructor
public class CommandeFournisseurViewController {
    
    private final CommandeFournisseurService commandeService;
    private final FournisseurService fournisseurService;
    private final ProduitService produitService;
    
    @GetMapping
    public String listCommandes(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        Model model
    ) {
        int pageIndex = Math.max(page, 0);
        int pageSize = Math.min(Math.max(size, 1), 50);
        Pageable pageable = PageRequest.of(pageIndex, pageSize, Sort.by("dateCommande").descending());
        Page<CommandeFournisseurDTO> commandes = commandeService.findAll(pageable);
        
        Map<Long, String> fournisseurLibelles = fournisseurService.findAll().stream()
            .filter(f -> f.getId() != null)
            .collect(Collectors.toMap(FournisseurDTO::getId, FournisseurDTO::getSociete));
        
        model.addAttribute("pageTitle", "Commandes fournisseurs");
        model.addAttribute("commandes", commandes);
        model.addAttribute("currentPage", pageIndex);
        model.addAttribute("totalPages", commandes.getTotalPages());
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("fournisseurLibelles", fournisseurLibelles);
        model.addAttribute("statuts", StatutCommande.values());
        return "commandes/list";
    }
    
    @GetMapping("/nouvelle")
    public String showCreateForm(Model model) {
        CommandeFournisseurDTO commande = new CommandeFournisseurDTO();
        commande.setStatut(StatutCommande.EN_ATTENTE);
        commande.setProduits(new ArrayList<>(List.of(new ProduitCommandeDTO())));
        
        populateFormModel(model, commande, true);
        return "commandes/form";
    }
    
    @PostMapping
    public String createCommande(
        @Valid @ModelAttribute("commande") CommandeFournisseurDTO commande,
        BindingResult bindingResult,
        RedirectAttributes redirectAttributes,
        Model model
    ) {
        sanitizeProduits(commande);
        
        if (commande.getProduits().isEmpty()) {
            bindingResult.rejectValue("produits", "commande.produits.vide", "Au moins un produit doit être renseigné.");
        }
        
        if (bindingResult.hasErrors()) {
            populateFormModel(model, commande, true);
            return "commandes/form";
        }
        
        commandeService.create(commande);
        redirectAttributes.addFlashAttribute("successMessage", "Commande créée avec succès.");
        return "redirect:/ui/commandes";
    }
    
    @GetMapping("/{id}/edition")
    public String showEditForm(@PathVariable Long id, Model model) {
        CommandeFournisseurDetailDTO detail = commandeService.findById(id);
        CommandeFournisseurDTO commande = toCommandeDto(detail);
        populateFormModel(model, commande, false);
        model.addAttribute("detail", detail);
        return "commandes/form";
    }
    
    @PostMapping("/{id}/mise-a-jour")
    public String updateCommande(
        @PathVariable Long id,
        @Valid @ModelAttribute("commande") CommandeFournisseurDTO commande,
        BindingResult bindingResult,
        RedirectAttributes redirectAttributes,
        Model model
    ) {
        sanitizeProduits(commande);
        
        if (commande.getProduits().isEmpty()) {
            bindingResult.rejectValue("produits", "commande.produits.vide", "Au moins un produit doit être renseigné.");
        }
        
        if (bindingResult.hasErrors()) {
            populateFormModel(model, commande, false);
            CommandeFournisseurDetailDTO detail = commandeService.findById(id);
            model.addAttribute("detail", detail);
            return "commandes/form";
        }
        
        commandeService.update(id, commande);
        redirectAttributes.addFlashAttribute("successMessage", "Commande mise à jour avec succès.");
        return "redirect:/ui/commandes";
    }
    
    @GetMapping("/{id}")
    public String showDetails(@PathVariable Long id, Model model) {
        CommandeFournisseurDetailDTO detail = commandeService.findById(id);
        Map<Long, String> produitLibelles = produitService.findAll().stream()
            .filter(p -> p.getId() != null)
            .collect(Collectors.toMap(ProduitDTO::getId, ProduitDTO::getNom));
        
        model.addAttribute("pageTitle", "Détail commande");
        model.addAttribute("commande", detail);
        model.addAttribute("produitLibelles", produitLibelles);
        model.addAttribute("statuts", StatutCommande.values());
        return "commandes/detail";
    }
    
    @PostMapping("/{id}/suppression")
    public String deleteCommande(
        @PathVariable Long id,
        RedirectAttributes redirectAttributes
    ) {
        commandeService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Commande supprimée.");
        return "redirect:/ui/commandes";
    }
    
    private void populateFormModel(Model model, CommandeFournisseurDTO commande, boolean isNew) {
        List<FournisseurDTO> fournisseurs = fournisseurService.findAll();
        List<ProduitDTO> produits = produitService.findAll();
        
        if (commande.getProduits() == null) {
            commande.setProduits(new ArrayList<>());
        }
        
        int minimumRows = Math.max(commande.getProduits().size() + (isNew ? 2 : 1), 3);
        while (commande.getProduits().size() < minimumRows) {
            commande.getProduits().add(new ProduitCommandeDTO());
        }
        
        model.addAttribute("pageTitle", isNew ? "Nouvelle commande" : "Modifier une commande");
        model.addAttribute("commande", commande);
        model.addAttribute("isNew", isNew);
        model.addAttribute("fournisseurs", fournisseurs);
        model.addAttribute("produitsDisponibles", produits);
        model.addAttribute("statuts", StatutCommande.values());
    }
    
    private void sanitizeProduits(CommandeFournisseurDTO commande) {
        if (commande.getProduits() == null) {
            commande.setProduits(new ArrayList<>());
            return;
        }
        
        List<ProduitCommandeDTO> produitsFiltres = commande.getProduits().stream()
            .filter(p -> p.getProduitId() != null
                && p.getQuantite() != null
                && p.getQuantite() > 0
                && p.getPrixUnitaireCommande() != null
                && p.getPrixUnitaireCommande().compareTo(BigDecimal.ZERO) > 0)
            .collect(Collectors.toCollection(ArrayList::new));
        
        commande.setProduits(produitsFiltres);
    }
    
    private CommandeFournisseurDTO toCommandeDto(CommandeFournisseurDetailDTO detail) {
        CommandeFournisseurDTO dto = new CommandeFournisseurDTO();
        dto.setId(detail.getId());
        dto.setDateCommande(detail.getDateCommande());
        dto.setMontantTotal(detail.getMontantTotal());
        dto.setStatut(detail.getStatut());
        dto.setFournisseurId(detail.getFournisseur() != null ? detail.getFournisseur().getId() : null);
        
        List<ProduitCommandeDTO> produits = detail.getProduits().stream()
            .map(this::copyProduitCommande)
            .collect(Collectors.toCollection(ArrayList::new));
        dto.setProduits(produits);
        return dto;
    }
    
    private ProduitCommandeDTO copyProduitCommande(ProduitCommandeDTO source) {
        ProduitCommandeDTO target = new ProduitCommandeDTO();
        target.setProduitId(source.getProduitId());
        target.setQuantite(source.getQuantite());
        target.setPrixUnitaireCommande(source.getPrixUnitaireCommande());
        return target;
    }
}


