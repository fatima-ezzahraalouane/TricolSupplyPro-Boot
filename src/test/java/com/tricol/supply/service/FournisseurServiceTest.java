package com.tricol.supply.service;

import com.tricol.supply.dto.FournisseurDTO;
import com.tricol.supply.exception.ResourceNotFoundException;
import com.tricol.supply.mapper.FournisseurMapper;
import com.tricol.supply.model.entity.Fournisseur;
import com.tricol.supply.repository.FournisseurRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires pour FournisseurService")
class FournisseurServiceTest {

    @Mock
    private FournisseurRepository fournisseurRepository;

    @Mock
    private FournisseurMapper fournisseurMapper;

    @InjectMocks
    private FournisseurService fournisseurService;

    private Fournisseur fournisseur;
    private FournisseurDTO fournisseurDTO;

    @BeforeEach
    void setUp() {
        fournisseur = Fournisseur.builder()
                .id(1L)
                .societe("Fournisseur Test SARL")
                .adresse("123 Rue Test")
                .contact("Mohamed Alami")
                .email("contact@test.com")
                .telephone("0612345678")
                .ville("Casablanca")
                .ice("001234567890001")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        fournisseurDTO = new FournisseurDTO();
        fournisseurDTO.setId(1L);
        fournisseurDTO.setSociete("Fournisseur Test SARL");
        fournisseurDTO.setAdresse("123 Rue Test");
        fournisseurDTO.setContact("Mohamed Alami");
        fournisseurDTO.setEmail("contact@test.com");
        fournisseurDTO.setTelephone("0612345678");
        fournisseurDTO.setVille("Casablanca");
        fournisseurDTO.setIce("001234567890001");
    }

    @Test
    @DisplayName("Doit retourner une page de fournisseurs")
    void testFindAll() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<Fournisseur> fournisseurs = Arrays.asList(fournisseur);
        Page<Fournisseur> page = new PageImpl<>(fournisseurs, pageable, 1);

        when(fournisseurRepository.findAll(pageable)).thenReturn(page);
        when(fournisseurMapper.toDTO(any(Fournisseur.class))).thenReturn(fournisseurDTO);

        // When
        Page<FournisseurDTO> result = fournisseurService.findAll(pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(fournisseurDTO, result.getContent().get(0));
        verify(fournisseurRepository, times(1)).findAll(pageable);
        verify(fournisseurMapper, times(1)).toDTO(any(Fournisseur.class));
    }

    @Test
    @DisplayName("Doit retourner un fournisseur par son ID")
    void testFindById_Success() {
        // Given
        Long id = 1L;
        when(fournisseurRepository.findById(id)).thenReturn(Optional.of(fournisseur));
        when(fournisseurMapper.toDTO(fournisseur)).thenReturn(fournisseurDTO);

        // When
        FournisseurDTO result = fournisseurService.findById(id);

        // Then
        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("Fournisseur Test SARL", result.getSociete());
        verify(fournisseurRepository, times(1)).findById(id);
        verify(fournisseurMapper, times(1)).toDTO(fournisseur);
    }

    
}

