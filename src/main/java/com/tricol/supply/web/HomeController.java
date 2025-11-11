package com.tricol.supply.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    
    @GetMapping("/")
    public String redirectToUi() {
        return "redirect:/ui";
    }
    
    @GetMapping("/ui")
    public String home(Model model) {
        model.addAttribute("pageTitle", "Tableau de bord");
        return "index";
    }
}


