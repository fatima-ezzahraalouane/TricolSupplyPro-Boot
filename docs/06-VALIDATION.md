# ✅ Validation Jakarta - Guide Complet

[← Retour à l'index](README.md) | [← Pagination et Tri](05-PAGINATION-TRI.md)

---

## 📖 Table des matières

1. [Qu'est-ce que Jakarta Validation ?](#quest-ce-que-jakarta-validation)
2. [Annotations de validation](#annotations-de-validation)
3. [Validation dans les Controllers](#validation-dans-les-controllers)
4. [Messages personnalisés](#messages-personnalisés)
5. [Gestion des erreurs](#gestion-des-erreurs)

---

## Qu'est-ce que Jakarta Validation ?

### Définition

**Jakarta Validation** (anciennement Bean Validation) est une spécification Java pour valider les données d'objets à l'aide d'annotations.

**Implémentation** : Hibernate Validator (la plus utilisée)

### Pourquoi valider ?

| Raison | Explication |
|--------|-------------|
| 🛡️ **Sécurité** | Éviter les données malveillantes |
| 📊 **Intégrité** | Garantir la cohérence des données |
| 🐛 **Prévention d'erreurs** | Détecter les problèmes tôt |
| 👤 **Expérience utilisateur** | Messages d'erreur clairs |
| 📝 **Documentation** | Les contraintes documentent les règles |

### Avant vs Après

**❌ Sans validation** :
```java
@PostMapping
public ResponseEntity<ProduitDTO> create(@RequestBody ProduitDTO dto) {
    // Pas de validation !
    // Risque : nom null, prix négatif, etc.
    return ResponseEntity.ok(produitService.create(dto));
}
```

**✅ Avec validation** :
```java
@PostMapping
public ResponseEntity<ProduitDTO> create(@Valid @RequestBody ProduitDTO dto) {
    // Validation automatique avant d'entrer dans la méthode
    return ResponseEntity.ok(produitService.create(dto));
}
```

---

## Annotations de validation

### Annotations de base

| Annotation | Description | Exemple |
|------------|-------------|---------|
| `@NotNull` | Ne doit pas être null | `@NotNull private String nom;` |
| `@NotEmpty` | Ne doit pas être null ou vide | `@NotEmpty private String email;` |
| `@NotBlank` | Ne doit pas être null, vide ou espaces | `@NotBlank private String societe;` |
| `@Size` | Taille min/max | `@Size(min = 2, max = 100)` |
| `@Min` | Valeur minimale | `@Min(0) private Integer stock;` |
| `@Max` | Valeur maximale | `@Max(1000)` |
| `@Positive` | Doit être positif (> 0) | `@Positive private BigDecimal prix;` |
| `@PositiveOrZero` | Doit être ≥ 0 | `@PositiveOrZero private Integer quantite;` |
| `@Negative` | Doit être négatif (< 0) | `@Negative` |
| `@NegativeOrZero` | Doit être ≤ 0 | `@NegativeOrZero` |
| `@DecimalMin` | Valeur décimale minimale | `@DecimalMin("0.01")` |
| `@DecimalMax` | Valeur décimale maximale | `@DecimalMax("999999.99")` |
| `@Digits` | Nombre de chiffres | `@Digits(integer = 10, fraction = 2)` |
| `@Email` | Format email valide | `@Email private String email;` |
| `@Pattern` | Expression régulière | `@Pattern(regexp = "^[0-9]{10}$")` |
| `@Past` | Date dans le passé | `@Past private LocalDate dateNaissance;` |
| `@PastOrPresent` | Date passée ou présente | `@PastOrPresent` |
| `@Future` | Date dans le futur | `@Future private LocalDate dateLivraison;` |
| `@FutureOrPresent` | Date future ou présente | `@FutureOrPresent` |

### Exemples dans le projet

**ProduitDTO** :
```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProduitDTO {
    
    private Long id;
    
    @NotBlank(message = "Le nom du produit est obligatoire")
    @Size(min = 2, max = 100, message = "Le nom doit contenir entre 2 et 100 caractères")
    private String nom;
    
    @Size(max = 500, message = "La description ne peut pas dépasser 500 caractères")
    private String description;
    
    @NotNull(message = "Le prix unitaire est obligatoire")
    @Positive(message = "Le prix doit être positif")
    @Digits(integer = 10, fraction = 2, message = "Le prix doit avoir au maximum 10 chiffres entiers et 2 décimales")
    private BigDecimal prixUnitaire;
    
    @Size(max = 50, message = "La catégorie ne peut pas dépasser 50 caractères")
    private String categorie;
    
    @PositiveOrZero(message = "Le stock ne peut pas être négatif")
    private Integer stockActuel;
    
    @PositiveOrZero(message = "Le coût unitaire moyen ne peut pas être négatif")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal coutUnitaireMoyen;
}
```

**FournisseurDTO** :
```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FournisseurDTO {
    
    private Long id;
    
    @NotBlank(message = "Le nom de la société est obligatoire")
    @Size(min = 2, max = 100, message = "Le nom doit contenir entre 2 et 100 caractères")
    private String societe;
    
    @NotBlank(message = "L'adresse est obligatoire")
    @Size(max = 255, message = "L'adresse ne peut pas dépasser 255 caractères")
    private String adresse;
    
    @NotBlank(message = "Le téléphone est obligatoire")
    @Pattern(regexp = "^[0-9]{10}$", message = "Le téléphone doit contenir 10 chiffres")
    private String telephone;
    
    @Email(message = "L'email doit être valide")
    @Size(max = 100, message = "L'email ne peut pas dépasser 100 caractères")
    private String email;
}
```

**CommandeFournisseurDTO** :
```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommandeFournisseurDTO {
    
    private Long id;
    
    @NotNull(message = "Le fournisseur est obligatoire")
    private Long fournisseurId;
    
    @NotNull(message = "La date de commande est obligatoire")
    @PastOrPresent(message = "La date de commande ne peut pas être dans le futur")
    private LocalDate dateCommande;
    
    @FutureOrPresent(message = "La date de livraison prévue doit être dans le futur ou aujourd'hui")
    private LocalDate dateLivraisonPrevue;
    
    @NotNull(message = "Le statut est obligatoire")
    private StatutCommande statut;
    
    @NotEmpty(message = "La commande doit contenir au moins un produit")
    @Valid  // Valide aussi les objets de la liste
    private List<ProduitCommandeDTO> produits;
}
```

**ProduitCommandeDTO** :
```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProduitCommandeDTO {
    
    @NotNull(message = "Le produit est obligatoire")
    private Long produitId;
    
    @NotNull(message = "La quantité est obligatoire")
    @Positive(message = "La quantité doit être positive")
    private Integer quantite;
    
    @NotNull(message = "Le prix unitaire est obligatoire")
    @Positive(message = "Le prix unitaire doit être positif")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal prixUnitaireCommande;
}
```

---

## Validation dans les Controllers

### Annotation @Valid

```java
@RestController
@RequestMapping("/api/v1/produits")
@RequiredArgsConstructor
public class ProduitController {
    
    private final ProduitService produitService;
    
    @PostMapping
    @Operation(summary = "Créer un produit")
    public ResponseEntity<ProduitDTO> create(
        @Valid @RequestBody ProduitDTO dto  // @Valid déclenche la validation
    ) {
        ProduitDTO created = produitService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Modifier un produit")
    public ResponseEntity<ProduitDTO> update(
        @PathVariable Long id,
        @Valid @RequestBody ProduitDTO dto  // Validation aussi pour update
    ) {
        ProduitDTO updated = produitService.update(id, dto);
        return ResponseEntity.ok(updated);
    }
}
```

### Validation des paramètres

```java
@RestController
@RequestMapping("/api/v1/produits")
@Validated  // Nécessaire pour valider les paramètres de méthode
public class ProduitController {
    
    @GetMapping("/{id}")
    public ResponseEntity<ProduitDTO> findById(
        @PathVariable @Positive(message = "L'ID doit être positif") Long id
    ) {
        ProduitDTO produit = produitService.findById(id);
        return ResponseEntity.ok(produit);
    }
    
    @GetMapping
    public ResponseEntity<Page<ProduitDTO>> findAll(
        @RequestParam(required = false) 
        @Size(min = 2, max = 50, message = "Le nom doit contenir entre 2 et 50 caractères") 
        String nom,
        
        @PageableDefault(size = 10, sort = "id") Pageable pageable
    ) {
        Page<ProduitDTO> produits = produitService.findAll(nom, pageable);
        return ResponseEntity.ok(produits);
    }
}
```

---

## Messages personnalisés

### Messages dans les annotations

```java
@NotBlank(message = "Le nom du produit est obligatoire")
@Size(min = 2, max = 100, message = "Le nom doit contenir entre 2 et 100 caractères")
private String nom;
```

### Fichier de messages (messages.properties)

**src/main/resources/messages.properties** :
```properties
# Validation messages
produit.nom.notblank=Le nom du produit est obligatoire
produit.nom.size=Le nom doit contenir entre {min} et {max} caractères
produit.prix.positive=Le prix doit être positif
produit.prix.notnull=Le prix unitaire est obligatoire

fournisseur.societe.notblank=Le nom de la société est obligatoire
fournisseur.telephone.pattern=Le téléphone doit contenir 10 chiffres
fournisseur.email.email=L'email doit être valide
```

**Utilisation** :
```java
@NotBlank(message = "{produit.nom.notblank}")
@Size(min = 2, max = 100, message = "{produit.nom.size}")
private String nom;
```

---

## Gestion des erreurs

### Exception Handler Global

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * Gestion des erreurs de validation
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
        MethodArgumentNotValidException ex
    ) {
        Map<String, String> errors = new HashMap<>();
        
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        
        ErrorResponse errorResponse = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error("Erreur de validation")
            .message("Les données fournies sont invalides")
            .errors(errors)
            .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    
    /**
     * Gestion des erreurs de validation des paramètres
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(
        ConstraintViolationException ex
    ) {
        Map<String, String> errors = new HashMap<>();
        
        ex.getConstraintViolations().forEach(violation -> {
            String propertyPath = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            errors.put(propertyPath, message);
        });
        
        ErrorResponse errorResponse = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error("Erreur de validation")
            .message("Les paramètres fournis sont invalides")
            .errors(errors)
            .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}
```

### Classe ErrorResponse

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private Map<String, String> errors;  // Détails des erreurs de validation
}
```

### Exemple de réponse d'erreur

**Requête** :
```json
POST /api/v1/produits
{
  "nom": "",
  "prixUnitaire": -10,
  "stockActuel": -5
}
```

**Réponse (400 Bad Request)** :
```json
{
  "timestamp": "2025-11-10T22:00:00",
  "status": 400,
  "error": "Erreur de validation",
  "message": "Les données fournies sont invalides",
  "errors": {
    "nom": "Le nom du produit est obligatoire",
    "prixUnitaire": "Le prix doit être positif",
    "stockActuel": "Le stock ne peut pas être négatif"
  }
}
```

---

## Validation personnalisée

### Créer une annotation personnalisée

**@ValidPhone** (exemple) :

```java
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PhoneValidator.class)
@Documented
public @interface ValidPhone {
    String message() default "Le numéro de téléphone est invalide";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
```

**PhoneValidator** :

```java
public class PhoneValidator implements ConstraintValidator<ValidPhone, String> {
    
    @Override
    public boolean isValid(String phone, ConstraintValidatorContext context) {
        if (phone == null) {
            return true;  // @NotNull gère les null
        }
        
        // Validation personnalisée
        return phone.matches("^(\\+33|0)[1-9][0-9]{8}$");
    }
}
```

**Utilisation** :

```java
@ValidPhone(message = "Le téléphone doit être un numéro français valide")
private String telephone;
```

---

## Questions fréquentes

### Q1 : Qu'est-ce que Jakarta Validation ?

**Réponse** : Jakarta Validation (anciennement Bean Validation) est une spécification Java pour valider les données d'objets à l'aide d'annotations. Hibernate Validator est l'implémentation la plus utilisée. Elle permet de définir des contraintes de validation directement sur les champs des classes.

### Q2 : Quelle est la différence entre @NotNull, @NotEmpty et @NotBlank ?

**Réponse** :
- `@NotNull` : Le champ ne doit pas être null
- `@NotEmpty` : Le champ ne doit pas être null ET ne doit pas être vide (pour String, Collection, Map, Array)
- `@NotBlank` : Le champ ne doit pas être null, vide ou contenir uniquement des espaces (String uniquement)

### Q3 : Comment valider une liste d'objets ?

**Réponse** : On utilise `@Valid` sur la liste pour valider chaque élément :
```java
@NotEmpty
@Valid  // Valide chaque ProduitCommandeDTO
private List<ProduitCommandeDTO> produits;
```

### Q4 : Comment personnaliser les messages d'erreur ?

**Réponse** : On peut utiliser le paramètre `message` dans l'annotation ou créer un fichier `messages.properties` et référencer les clés avec `{cle}`.

### Q5 : Où se fait la validation ?

**Réponse** : La validation se fait automatiquement avant l'entrée dans la méthode du controller quand on utilise `@Valid` ou `@Validated`. Si la validation échoue, une exception `MethodArgumentNotValidException` est levée.

---

[← Pagination et Tri](05-PAGINATION-TRI.md) | [Suivant : Swagger/OpenAPI →](07-SWAGGER.md)
