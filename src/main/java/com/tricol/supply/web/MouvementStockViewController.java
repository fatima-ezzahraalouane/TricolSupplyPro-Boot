package com.tricol.supply.web;

import com.tricol.supply.dto.MouvementStockDTO;
import com.tricol.supply.dto.ProduitDTO;
import com.tricol.supply.model.enums.TypeMouvement;
import com.tricol.supply.service.MouvementStockService;
import com.tricol.supply.service.ProduitService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/ui/mouvements")
@RequiredArgsConstructor
public class MouvementStockViewController {
    
    private final MouvementStockService mouvementStockService;
    private final ProduitService produitService;
    
    @GetMapping
    public String listMouvements(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) Long produitId,
        @RequestParam(required = false) TypeMouvement type,
        Model model
    ) {
        int pageIndex = Math.max(page, 0);
        int pageSize = Math.min(Math.max(size, 1), 50);
        Pageable pageable = PageRequest.of(pageIndex, pageSize, Sort.by("dateMouvement").descending());
        
        Page<MouvementStockDTO> mouvements = mouvementStockService.findByFilters(produitId, type, pageable);
        List<ProduitDTO> produits = produitService.findAll();
        
        model.addAttribute("pageTitle", "Mouvements de stock");
        model.addAttribute("mouvements", mouvements);
        model.addAttribute("currentPage", pageIndex);
        model.addAttribute("totalPages", mouvements.getTotalPages());
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("produits", produits);
        model.addAttribute("types", TypeMouvement.values());
        model.addAttribute("selectedProduitId", produitId);
        model.addAttribute("selectedType", type);
        return "mouvements/list";
    }
}



