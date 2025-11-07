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
    
    
}

