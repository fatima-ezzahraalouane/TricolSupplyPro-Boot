package com.tricol.supply.web;

import com.tricol.supply.dto.FournisseurDTO;
import com.tricol.supply.service.FournisseurService;
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
@RequestMapping("/ui/fournisseurs")
@RequiredArgsConstructor
public class FournisseurViewController {
    
    private final FournisseurService fournisseurService;
    
    @GetMapping
    public String listFournisseurs(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        Model model
    ) {
        int pageIndex = Math.max(page, 0);
        int pageSize = Math.min(Math.max(size, 1), 50);
        Pageable pageable = PageRequest.of(pageIndex, pageSize, Sort.by("societe").ascending());
        Page<FournisseurDTO> fournisseurs = fournisseurService.findAll(pageable);
        
        model.addAttribute("pageTitle", "Fournisseurs");
        model.addAttribute("fournisseurs", fournisseurs);
        model.addAttribute("currentPage", pageIndex);
        model.addAttribute("totalPages", fournisseurs.getTotalPages());
        model.addAttribute("pageSize", pageSize);
        return "fournisseurs/list";
    }
    
    @GetMapping("/nouveau")
    public String showCreateForm(Model model) {
        model.addAttribute("pageTitle", "Nouveau fournisseur");
        model.addAttribute("fournisseur", new FournisseurDTO());
        model.addAttribute("isNew", true);
        return "fournisseurs/form";
    }
    
    @PostMapping
    public String createFournisseur(
        @Valid @ModelAttribute("fournisseur") FournisseurDTO fournisseur,
        BindingResult bindingResult,
        RedirectAttributes redirectAttributes,
        Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "Nouveau fournisseur");
            model.addAttribute("isNew", true);
            return "fournisseurs/form";
        }
        
        fournisseurService.create(fournisseur);
        redirectAttributes.addFlashAttribute("successMessage", "Fournisseur créé avec succès.");
        return "redirect:/ui/fournisseurs";
    }
    
    @GetMapping("/{id}/edition")
    public String showEditForm(@PathVariable Long id, Model model) {
        FournisseurDTO fournisseur = fournisseurService.findById(id);
        model.addAttribute("pageTitle", "Modifier un fournisseur");
        model.addAttribute("fournisseur", fournisseur);
        model.addAttribute("isNew", false);
        return "fournisseurs/form";
    }
    
    @PostMapping("/{id}/mise-a-jour")
    public String updateFournisseur(
        @PathVariable Long id,
        @Valid @ModelAttribute("fournisseur") FournisseurDTO fournisseur,
        BindingResult bindingResult,
        RedirectAttributes redirectAttributes,
        Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "Modifier un fournisseur");
            model.addAttribute("isNew", false);
            return "fournisseurs/form";
        }
        
        fournisseurService.update(id, fournisseur);
        redirectAttributes.addFlashAttribute("successMessage", "Fournisseur mis à jour avec succès.");
        return "redirect:/ui/fournisseurs";
    }
    
    @PostMapping("/{id}/suppression")
    public String deleteFournisseur(
        @PathVariable Long id,
        RedirectAttributes redirectAttributes
    ) {
        fournisseurService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Fournisseur supprimé.");
        return "redirect:/ui/fournisseurs";
    }
}


