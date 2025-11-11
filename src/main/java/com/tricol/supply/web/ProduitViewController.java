package com.tricol.supply.web;

import com.tricol.supply.dto.ProduitDTO;
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

@Controller
@RequestMapping("/ui/produits")
@RequiredArgsConstructor
public class ProduitViewController {
    
    private final ProduitService produitService;
    
    @GetMapping
    public String listProduits(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        Model model
    ) {
        int pageIndex = Math.max(page, 0);
        int pageSize = Math.min(Math.max(size, 1), 50);
        Pageable pageable = PageRequest.of(pageIndex, pageSize, Sort.by("nom").ascending());
        Page<ProduitDTO> produits = produitService.findAll(pageable);
        
        model.addAttribute("pageTitle", "Produits");
        model.addAttribute("produits", produits);
        model.addAttribute("currentPage", pageIndex);
        model.addAttribute("totalPages", produits.getTotalPages());
        model.addAttribute("pageSize", pageSize);
        return "produits/list";
    }
    
    @GetMapping("/nouveau")
    public String showCreateForm(Model model) {
        model.addAttribute("pageTitle", "Nouveau produit");
        model.addAttribute("produit", new ProduitDTO());
        model.addAttribute("isNew", true);
        return "produits/form";
    }
    
    @PostMapping
    public String createProduit(
        @Valid @ModelAttribute("produit") ProduitDTO produit,
        BindingResult bindingResult,
        RedirectAttributes redirectAttributes,
        Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "Nouveau produit");
            model.addAttribute("isNew", true);
            return "produits/form";
        }
        
        produitService.create(produit);
        redirectAttributes.addFlashAttribute("successMessage", "Produit créé avec succès.");
        return "redirect:/ui/produits";
    }
    
    @GetMapping("/{id}/edition")
    public String showEditForm(@PathVariable Long id, Model model) {
        ProduitDTO produit = produitService.findById(id);
        model.addAttribute("pageTitle", "Modifier un produit");
        model.addAttribute("produit", produit);
        model.addAttribute("isNew", false);
        return "produits/form";
    }
    
    @PostMapping("/{id}/mise-a-jour")
    public String updateProduit(
        @PathVariable Long id,
        @Valid @ModelAttribute("produit") ProduitDTO produit,
        BindingResult bindingResult,
        RedirectAttributes redirectAttributes,
        Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "Modifier un produit");
            model.addAttribute("isNew", false);
            return "produits/form";
        }
        
        produitService.update(id, produit);
        redirectAttributes.addFlashAttribute("successMessage", "Produit mis à jour avec succès.");
        return "redirect:/ui/produits";
    }
    
    @PostMapping("/{id}/suppression")
    public String deleteProduit(
        @PathVariable Long id,
        RedirectAttributes redirectAttributes
    ) {
        produitService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Produit supprimé.");
        return "redirect:/ui/produits";
    }
}


