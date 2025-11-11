# 📖 Swagger/OpenAPI - Guide Complet

[← Retour à l'index](README.md) | [← Validation Jakarta](06-VALIDATION.md)

---

## 📖 Table des matières

1. [Qu'est-ce que Swagger/OpenAPI ?](#quest-ce-que-swaggeropenapi)
2. [Configuration](#configuration)
3. [Annotations Swagger](#annotations-swagger)
4. [Interface Swagger UI](#interface-swagger-ui)
5. [Documentation avancée](#documentation-avancée)

---

## Qu'est-ce que Swagger/OpenAPI ?

### Définition

**OpenAPI** : Spécification pour décrire les APIs REST  
**Swagger** : Ensemble d'outils pour implémenter OpenAPI  
**Swagger UI** : Interface web pour visualiser et tester l'API

### Pourquoi utiliser Swagger ?

| Avantage | Explication |
|----------|-------------|
| 📖 **Documentation automatique** | Génère la doc depuis le code |
| 🧪 **Tests interactifs** | Tester l'API directement dans le navigateur |
| 🔄 **Toujours à jour** | La doc suit le code automatiquement |
| 👥 **Collaboration** | Facilite la communication avec le frontend |
| 📝 **Standardisation** | Format OpenAPI reconnu mondialement |

### Avant vs Après

**❌ Sans Swagger** :
- Documentation manuelle (Word, PDF)
- Risque d'obsolescence
- Tests avec Postman/cURL
- Difficile à maintenir

**✅ Avec Swagger** :
- Documentation générée automatiquement
- Toujours synchronisée avec le code
- Tests directement dans le navigateur
- Interface intuitive

---

## Configuration

### Dépendance Maven

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.2.0</version>
</dependency>
```

### Configuration Spring Boot

**application.properties** :

```properties
# Swagger/OpenAPI Configuration
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.operationsSorter=method
springdoc.swagger-ui.tagsSorter=alpha
springdoc.swagger-ui.tryItOutEnabled=true

# Afficher les schémas
springdoc.swagger-ui.defaultModelsExpandDepth=1
springdoc.swagger-ui.defaultModelExpandDepth=1
```

### Classe de configuration

```java
@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("TricolSupplyPro API")
                .version("1.0.0")
                .description("API de gestion des approvisionnements pour TricolSupplyPro")
                .contact(new Contact()
                    .name("Fatima Ezzahra Alouane")
                    .email("fatima@tricolsupply.com")
                    .url("https://github.com/fatima-ezzahraalouane"))
                .license(new License()
                    .name("Apache 2.0")
                    .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
            .externalDocs(new ExternalDocumentation()
                .description("Documentation complète")
                .url("https://github.com/fatima-ezzahraalouane/TricolSupplyPro-Boot"));
    }
}
```

### URLs d'accès

| URL | Description |
|-----|-------------|
| `http://localhost:8080/swagger-ui.html` | Interface Swagger UI |
| `http://localhost:8080/api-docs` | Spécification OpenAPI (JSON) |
| `http://localhost:8080/api-docs.yaml` | Spécification OpenAPI (YAML) |

---

## Annotations Swagger

### Annotations de Controller

#### @Tag

Groupe les endpoints par catégorie :

```java
@RestController
@RequestMapping("/api/v1/produits")
@RequiredArgsConstructor
@Tag(name = "Produits", description = "Gestion des produits")
public class ProduitController {
    // ...
}
```

#### @Operation

Documente une méthode :

```java
@GetMapping("/{id}")
@Operation(
    summary = "Récupérer un produit par son ID",
    description = "Retourne les détails d'un produit spécifique"
)
public ResponseEntity<ProduitDTO> findById(@PathVariable Long id) {
    // ...
}
```

#### @ApiResponse / @ApiResponses

Documente les réponses possibles :

```java
@PostMapping
@Operation(summary = "Créer un nouveau produit")
@ApiResponses(value = {
    @ApiResponse(
        responseCode = "201",
        description = "Produit créé avec succès",
        content = @Content(schema = @Schema(implementation = ProduitDTO.class))
    ),
    @ApiResponse(
        responseCode = "400",
        description = "Données invalides",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    ),
    @ApiResponse(
        responseCode = "500",
        description = "Erreur serveur"
    )
})
public ResponseEntity<ProduitDTO> create(@Valid @RequestBody ProduitDTO dto) {
    // ...
}
```

### Annotations de paramètres

#### @Parameter

Documente un paramètre :

```java
@GetMapping("/{id}")
@Operation(summary = "Récupérer un produit")
public ResponseEntity<ProduitDTO> findById(
    @Parameter(description = "ID du produit", required = true, example = "1")
    @PathVariable Long id
) {
    // ...
}
```

```java
@GetMapping
@Operation(summary = "Liste des produits")
public ResponseEntity<Page<ProduitDTO>> findAll(
    @Parameter(description = "Numéro de page (commence à 0)", example = "0")
    @RequestParam(defaultValue = "0") int page,
    
    @Parameter(description = "Nombre d'éléments par page", example = "10")
    @RequestParam(defaultValue = "10") int size,
    
    @Parameter(description = "Champ de tri", example = "nom")
    @RequestParam(required = false) String sort
) {
    // ...
}
```

### Annotations de modèle

#### @Schema

Documente un champ de DTO :

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Données d'un produit")
public class ProduitDTO {
    
    @Schema(description = "Identifiant unique du produit", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;
    
    @Schema(description = "Nom du produit", example = "Veste professionnelle", required = true)
    @NotBlank
    private String nom;
    
    @Schema(description = "Description détaillée du produit", example = "Veste de travail résistante")
    private String description;
    
    @Schema(description = "Prix unitaire en euros", example = "150.00", required = true)
    @NotNull
    @Positive
    private BigDecimal prixUnitaire;
    
    @Schema(description = "Catégorie du produit", example = "Vêtements")
    private String categorie;
    
    @Schema(description = "Stock actuel disponible", example = "100")
    @PositiveOrZero
    private Integer stockActuel;
    
    @Schema(description = "Coût unitaire moyen", example = "120.00")
    private BigDecimal coutUnitaireMoyen;
}
```

---

## Interface Swagger UI

### Accès à l'interface

1. Démarrer l'application : `mvn spring-boot:run`
2. Ouvrir le navigateur : `http://localhost:8080/swagger-ui.html`

### Sections de l'interface

#### 1. En-tête

```
TricolSupplyPro API - 1.0.0
API de gestion des approvisionnements pour TricolSupplyPro
```

#### 2. Tags (Catégories)

```
📦 Produits - Gestion des produits
👥 Fournisseurs - Gestion des fournisseurs
📋 Commandes - Gestion des commandes fournisseurs
📊 Mouvements de Stock - Suivi des mouvements de stock
```

#### 3. Endpoints par tag

**Exemple : Produits**

```
GET    /api/v1/produits          Liste des produits
POST   /api/v1/produits          Créer un produit
GET    /api/v1/produits/{id}     Récupérer un produit
PUT    /api/v1/produits/{id}     Modifier un produit
DELETE /api/v1/produits/{id}     Supprimer un produit
```

#### 4. Détails d'un endpoint

Cliquer sur un endpoint pour voir :
- Description
- Paramètres
- Corps de la requête (Request Body)
- Réponses possibles
- Bouton "Try it out"

### Tester un endpoint

**Exemple : Créer un produit**

1. Cliquer sur `POST /api/v1/produits`
2. Cliquer sur "Try it out"
3. Modifier le JSON d'exemple :
```json
{
  "nom": "Pantalon de travail",
  "description": "Pantalon résistant avec poches renforcées",
  "prixUnitaire": 80.00,
  "categorie": "Vêtements",
  "stockActuel": 50,
  "coutUnitaireMoyen": 65.00
}
```
4. Cliquer sur "Execute"
5. Voir la réponse :
   - Code HTTP (201 Created)
   - Corps de la réponse
   - Headers

---

## Documentation avancée

### Exemple complet de Controller

```java
@RestController
@RequestMapping("/api/v1/produits")
@RequiredArgsConstructor
@Tag(name = "Produits", description = "API de gestion des produits")
public class ProduitController {
    
    private final ProduitService produitService;
    
    @GetMapping
    @Operation(
        summary = "Liste des produits",
        description = "Retourne une liste paginée de tous les produits avec possibilité de tri"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès"),
        @ApiResponse(responseCode = "500", description = "Erreur serveur")
    })
    public ResponseEntity<Page<ProduitDTO>> getAllProduits(
        @Parameter(description = "Configuration de pagination et tri")
        @PageableDefault(size = 10, sort = "id") Pageable pageable
    ) {
        Page<ProduitDTO> produits = produitService.findAll(pageable);
        return ResponseEntity.ok(produits);
    }
    
    @GetMapping("/{id}")
    @Operation(
        summary = "Récupérer un produit",
        description = "Retourne les détails d'un produit spécifique par son ID"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Produit trouvé",
            content = @Content(schema = @Schema(implementation = ProduitDTO.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Produit non trouvé",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    public ResponseEntity<ProduitDTO> findById(
        @Parameter(description = "ID du produit à récupérer", required = true, example = "1")
        @PathVariable Long id
    ) {
        ProduitDTO produit = produitService.findById(id);
        return ResponseEntity.ok(produit);
    }
    
    @PostMapping
    @Operation(
        summary = "Créer un produit",
        description = "Crée un nouveau produit dans le système"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Produit créé avec succès",
            content = @Content(schema = @Schema(implementation = ProduitDTO.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Données invalides",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    public ResponseEntity<ProduitDTO> create(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Données du produit à créer",
            required = true,
            content = @Content(schema = @Schema(implementation = ProduitDTO.class))
        )
        @Valid @RequestBody ProduitDTO dto
    ) {
        ProduitDTO created = produitService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    @PutMapping("/{id}")
    @Operation(
        summary = "Modifier un produit",
        description = "Met à jour les informations d'un produit existant"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Produit modifié avec succès"),
        @ApiResponse(responseCode = "404", description = "Produit non trouvé"),
        @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    public ResponseEntity<ProduitDTO> update(
        @Parameter(description = "ID du produit à modifier", required = true)
        @PathVariable Long id,
        
        @Valid @RequestBody ProduitDTO dto
    ) {
        ProduitDTO updated = produitService.update(id, dto);
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/{id}")
    @Operation(
        summary = "Supprimer un produit",
        description = "Supprime un produit du système"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Produit supprimé avec succès"),
        @ApiResponse(responseCode = "404", description = "Produit non trouvé")
    })
    public ResponseEntity<Void> delete(
        @Parameter(description = "ID du produit à supprimer", required = true)
        @PathVariable Long id
    ) {
        produitService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
```

### Documenter les enums

```java
@Schema(description = "Statut d'une commande fournisseur")
public enum StatutCommande {
    @Schema(description = "Commande en attente de validation")
    EN_ATTENTE,
    
    @Schema(description = "Commande validée")
    VALIDEE,
    
    @Schema(description = "Commande livrée")
    LIVREE,
    
    @Schema(description = "Commande annulée")
    ANNULEEE
}
```

---

## Questions fréquentes

### Q1 : Qu'est-ce que Swagger/OpenAPI ?

**Réponse** : OpenAPI est une spécification pour décrire les APIs REST de manière standardisée. Swagger est un ensemble d'outils qui implémente cette spécification, notamment Swagger UI qui génère une interface web interactive pour visualiser et tester l'API directement depuis le navigateur.

### Q2 : Comment accéder à Swagger UI ?

**Réponse** : Une fois l'application démarrée, on accède à Swagger UI via `http://localhost:8080/swagger-ui.html`. Cette interface permet de voir tous les endpoints, leurs paramètres, et de les tester directement.

### Q3 : La documentation est-elle générée automatiquement ?

**Réponse** : Oui, Swagger génère automatiquement la documentation à partir du code source. Les annotations comme `@Operation`, `@Parameter`, `@Schema` permettent d'enrichir cette documentation avec des descriptions détaillées.

### Q4 : Peut-on tester l'API depuis Swagger UI ?

**Réponse** : Oui, Swagger UI permet de tester tous les endpoints directement depuis le navigateur. On clique sur "Try it out", on remplit les paramètres, et on exécute la requête pour voir la réponse en temps réel.

### Q5 : Comment documenter les codes de réponse HTTP ?

**Réponse** : On utilise l'annotation `@ApiResponses` avec plusieurs `@ApiResponse` pour documenter chaque code de réponse possible (200, 201, 400, 404, 500, etc.) avec une description et le schéma de la réponse.

---

[← Validation Jakarta](06-VALIDATION.md) | [Suivant : Architecture →](08-ARCHITECTURE.md)
