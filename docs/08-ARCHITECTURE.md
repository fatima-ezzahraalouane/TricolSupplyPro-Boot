# 🏗️ Architecture du Projet - Guide Complet

[← Retour à l'index](README.md) | [← Swagger/OpenAPI](07-SWAGGER.md)

---

## 📖 Table des matières

1. [Architecture en couches](#architecture-en-couches)
2. [Structure des packages](#structure-des-packages)
3. [Flow d'une requête HTTP](#flow-dune-requête-http)
4. [Bonnes pratiques](#bonnes-pratiques)
5. [Patterns utilisés](#patterns-utilisés)

---

## Architecture en couches

### Vue d'ensemble

Le projet suit une **architecture en couches** (Layered Architecture) qui sépare les responsabilités :

```
┌─────────────────────────────────────┐
│         COUCHE PRÉSENTATION         │
│         (Controllers)               │
│  - Endpoints REST                   │
│  - Validation des entrées           │
│  - Gestion des réponses HTTP        │
└─────────────────────────────────────┘
              ↓ ↑
┌─────────────────────────────────────┐
│         COUCHE MÉTIER               │
│         (Services)                  │
│  - Logique métier                   │
│  - Transactions                     │
│  - Règles de gestion                │
└─────────────────────────────────────┘
              ↓ ↑
┌─────────────────────────────────────┐
│         COUCHE MAPPING              │
│         (Mappers)                   │
│  - Conversion Entity ↔ DTO          │
│  - MapStruct                        │
└─────────────────────────────────────┘
              ↓ ↑
┌─────────────────────────────────────┐
│         COUCHE PERSISTANCE          │
│         (Repositories)              │
│  - Accès aux données                │
│  - Requêtes JPA                     │
└─────────────────────────────────────┘
              ↓ ↑
┌─────────────────────────────────────┐
│         BASE DE DONNÉES             │
│         (PostgreSQL)                │
└─────────────────────────────────────┘
```

### Responsabilités de chaque couche

| Couche | Responsabilité | Annotations | Exemple |
|--------|----------------|-------------|---------|
| **Controller** | Exposer les endpoints REST | `@RestController`, `@RequestMapping` | `ProduitController` |
| **Service** | Logique métier et transactions | `@Service`, `@Transactional` | `ProduitService` |
| **Mapper** | Conversion Entity ↔ DTO | `@Mapper` | `ProduitMapper` |
| **Repository** | Accès aux données | `@Repository` | `ProduitRepository` |
| **Entity** | Modèle de données | `@Entity`, `@Table` | `Produit` |
| **DTO** | Transfert de données | `@Data`, `@Valid` | `ProduitDTO` |

---

## Structure des packages

### Arborescence complète

```
com.tricol.supply
├── TricolSupplyProApplication.java    ← Point d'entrée
│
├── config/                            ← Configuration
│   ├── OpenApiConfig.java            ← Config Swagger
│   └── WebConfig.java                ← Config Web (CORS, etc.)
│
├── controller/                        ← Couche Présentation
│   ├── ProduitController.java
│   ├── FournisseurController.java
│   ├── CommandeFournisseurController.java
│   └── MouvementStockController.java
│
├── service/                           ← Couche Métier
│   ├── ProduitService.java
│   ├── FournisseurService.java
│   ├── CommandeFournisseurService.java
│   └── MouvementStockService.java
│
├── repository/                        ← Couche Persistance
│   ├── ProduitRepository.java
│   ├── FournisseurRepository.java
│   ├── CommandeFournisseurRepository.java
│   ├── CommandeProduitRepository.java
│   └── MouvementStockRepository.java
│
├── mapper/                            ← Mapping Entity ↔ DTO
│   ├── ProduitMapper.java
│   ├── FournisseurMapper.java
│   ├── CommandeFournisseurMapper.java
│   └── MouvementStockMapper.java
│
├── dto/                               ← Data Transfer Objects
│   ├── ProduitDTO.java
│   ├── FournisseurDTO.java
│   ├── CommandeFournisseurDTO.java
│   ├── CommandeFournisseurDetailDTO.java
│   ├── ProduitCommandeDTO.java
│   └── MouvementStockDTO.java
│
├── entity/                            ← Entités JPA
│   ├── Produit.java
│   ├── Fournisseur.java
│   ├── CommandeFournisseur.java
│   ├── CommandeProduit.java
│   └── MouvementStock.java
│
├── enums/                             ← Énumérations
│   ├── StatutCommande.java
│   └── TypeMouvement.java
│
└── exception/                         ← Gestion des erreurs
    ├── ResourceNotFoundException.java
    ├── BusinessException.java
    ├── ErrorResponse.java
    └── GlobalExceptionHandler.java
```

### Principe de séparation

**Chaque package a une responsabilité unique** :

- ✅ **Controller** : Gère les requêtes HTTP uniquement
- ✅ **Service** : Contient la logique métier uniquement
- ✅ **Repository** : Accède aux données uniquement
- ✅ **Mapper** : Convertit les objets uniquement
- ✅ **DTO** : Représente les données de l'API uniquement
- ✅ **Entity** : Représente les tables de la base uniquement

---

## Flow d'une requête HTTP

### Exemple : Créer un produit

**Requête** :
```http
POST /api/v1/produits
Content-Type: application/json

{
  "nom": "Veste professionnelle",
  "prixUnitaire": 150.00,
  "stockActuel": 100
}
```

**Flow complet** :

```
1. CLIENT
   └─> Envoie une requête HTTP POST
       
2. SPRING BOOT (Tomcat)
   └─> Reçoit la requête sur le port 8080
       
3. DISPATCHER SERVLET
   └─> Route vers le bon Controller
       
4. CONTROLLER (ProduitController)
   ├─> @PostMapping("/api/v1/produits")
   ├─> Validation avec @Valid
   └─> Appelle le Service
       
5. SERVICE (ProduitService)
   ├─> Logique métier
   ├─> Appelle le Mapper (DTO → Entity)
   ├─> Appelle le Repository
   └─> Gère la transaction (@Transactional)
       
6. MAPPER (ProduitMapper)
   └─> Convertit ProduitDTO → Produit
       
7. REPOSITORY (ProduitRepository)
   └─> Appelle JPA/Hibernate
       
8. HIBERNATE
   ├─> Génère le SQL INSERT
   └─> Exécute la requête
       
9. BASE DE DONNÉES (PostgreSQL)
   ├─> Exécute INSERT INTO produits
   └─> Retourne l'ID généré
       
10. HIBERNATE
    └─> Retourne l'entité Produit avec l'ID
        
11. REPOSITORY
    └─> Retourne Produit au Service
        
12. MAPPER
    └─> Convertit Produit → ProduitDTO
        
13. SERVICE
    └─> Retourne ProduitDTO au Controller
        
14. CONTROLLER
    ├─> Crée ResponseEntity avec status 201
    └─> Retourne la réponse
        
15. SPRING BOOT
    ├─> Convertit ProduitDTO en JSON (Jackson)
    └─> Envoie la réponse HTTP
        
16. CLIENT
    └─> Reçoit la réponse 201 Created
```

### Code correspondant

**1. Controller** :
```java
@RestController
@RequestMapping("/api/v1/produits")
@RequiredArgsConstructor
public class ProduitController {
    
    private final ProduitService produitService;
    
    @PostMapping
    public ResponseEntity<ProduitDTO> create(@Valid @RequestBody ProduitDTO dto) {
        ProduitDTO created = produitService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
```

**2. Service** :
```java
@Service
@RequiredArgsConstructor
public class ProduitService {
    
    private final ProduitRepository produitRepository;
    private final ProduitMapper produitMapper;
    
    @Transactional
    public ProduitDTO create(ProduitDTO dto) {
        // Mapper DTO → Entity
        Produit produit = produitMapper.toEntity(dto);
        
        // Sauvegarder
        Produit saved = produitRepository.save(produit);
        
        // Mapper Entity → DTO
        return produitMapper.toDTO(saved);
    }
}
```

**3. Mapper** :
```java
@Mapper(componentModel = "spring")
public interface ProduitMapper {
    ProduitDTO toDTO(Produit produit);
    Produit toEntity(ProduitDTO dto);
}
```

**4. Repository** :
```java
@Repository
public interface ProduitRepository extends JpaRepository<Produit, Long> {
    // Spring Data génère l'implémentation
}
```

---

## Bonnes pratiques

### 1. Injection de dépendances par constructeur

**✅ Bon** :
```java
@Service
@RequiredArgsConstructor  // Lombok génère le constructeur
public class ProduitService {
    private final ProduitRepository produitRepository;
    private final ProduitMapper produitMapper;
}
```

**❌ Mauvais** :
```java
@Service
public class ProduitService {
    @Autowired
    private ProduitRepository produitRepository;
}
```

### 2. Ne jamais exposer les entités

**✅ Bon** :
```java
@GetMapping("/{id}")
public ResponseEntity<ProduitDTO> findById(@PathVariable Long id) {
    return ResponseEntity.ok(produitService.findById(id));
}
```

**❌ Mauvais** :
```java
@GetMapping("/{id}")
public ResponseEntity<Produit> findById(@PathVariable Long id) {
    return ResponseEntity.ok(produitRepository.findById(id).get());
}
```

### 3. Utiliser @Transactional dans les Services

**✅ Bon** :
```java
@Service
public class CommandeFournisseurService {
    
    @Transactional
    public CommandeFournisseurDetailDTO create(CommandeFournisseurDTO dto) {
        // Plusieurs opérations dans une transaction
    }
}
```

### 4. Gestion centralisée des erreurs

**✅ Bon** :
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        // Gestion centralisée
    }
}
```

### 5. Validation dans les DTOs

**✅ Bon** :
```java
@Data
public class ProduitDTO {
    @NotBlank
    @Size(min = 2, max = 100)
    private String nom;
    
    @NotNull
    @Positive
    private BigDecimal prixUnitaire;
}
```

### 6. Utiliser les Query Methods de Spring Data

**✅ Bon** :
```java
@Repository
public interface ProduitRepository extends JpaRepository<Produit, Long> {
    List<Produit> findByCategorie(String categorie);
    Page<Produit> findByNomContainingIgnoreCase(String nom, Pageable pageable);
}
```

---

## Patterns utilisés

### 1. Repository Pattern

**Définition** : Abstraction de l'accès aux données.

```java
@Repository
public interface ProduitRepository extends JpaRepository<Produit, Long> {
    // Spring Data génère l'implémentation
}
```

**Avantages** :
- ✅ Découplage de la logique métier et de la persistance
- ✅ Facilite les tests (mock du repository)
- ✅ Changement de base de données facilité

### 2. DTO Pattern

**Définition** : Objet pour transférer des données entre couches.

```java
// Entity (ne jamais exposer)
@Entity
public class Produit { ... }

// DTO (exposé dans l'API)
public class ProduitDTO { ... }
```

**Avantages** :
- ✅ Sécurité (ne pas exposer toutes les données)
- ✅ Flexibilité (différentes vues)
- ✅ Performance (éviter lazy loading)

### 3. Service Layer Pattern

**Définition** : Couche contenant la logique métier.

```java
@Service
public class ProduitService {
    // Logique métier ici
}
```

**Avantages** :
- ✅ Séparation des responsabilités
- ✅ Réutilisabilité du code
- ✅ Gestion des transactions

### 4. Dependency Injection

**Définition** : Spring gère les dépendances.

```java
@Service
@RequiredArgsConstructor
public class ProduitService {
    private final ProduitRepository repository;  // Injecté par Spring
}
```

**Avantages** :
- ✅ Couplage faible
- ✅ Testabilité
- ✅ Maintenabilité

### 5. Builder Pattern

**Définition** : Construction d'objets complexes.

```java
@Builder
public class ProduitDTO {
    // ...
}

// Utilisation
ProduitDTO dto = ProduitDTO.builder()
    .nom("Veste")
    .prixUnitaire(new BigDecimal("150.00"))
    .build();
```

**Avantages** :
- ✅ Code lisible
- ✅ Immutabilité possible
- ✅ Validation à la construction

---

## Diagramme de séquence

### Création d'une commande

```
Client          Controller          Service          Mapper          Repository          DB
  │                 │                  │                │                 │               │
  │─POST /commandes─>│                  │                │                 │               │
  │                 │                  │                │                 │               │
  │                 │─create(dto)─────>│                │                 │               │
  │                 │                  │                │                 │               │
  │                 │                  │─toEntity(dto)─>│                 │               │
  │                 │                  │<─entity────────│                 │               │
  │                 │                  │                │                 │               │
  │                 │                  │─save(entity)──────────────────>│               │
  │                 │                  │                │                 │─INSERT───────>│
  │                 │                  │                │                 │<─id──────────│
  │                 │                  │<─savedEntity───────────────────│               │
  │                 │                  │                │                 │               │
  │                 │                  │─toDTO(entity)─>│                 │               │
  │                 │                  │<─dto───────────│                 │               │
  │                 │                  │                │                 │               │
  │                 │                  │─updateStock()─>│                 │               │
  │                 │                  │                │─save()─────────>│               │
  │                 │                  │                │                 │─UPDATE───────>│
  │                 │                  │                │<────────────────│               │
  │                 │                  │                │                 │               │
  │                 │<─dto─────────────│                │                 │               │
  │                 │                  │                │                 │               │
  │<─201 Created────│                  │                │                 │               │
  │   + JSON        │                  │                │                 │               │
```

---

## Questions fréquentes

### Q1 : Qu'est-ce qu'une architecture en couches ?

**Réponse** : Une architecture en couches sépare l'application en différentes couches ayant chacune une responsabilité spécifique. Dans notre projet, nous avons : Controller (présentation), Service (logique métier), Repository (persistance). Cela facilite la maintenance et les tests.

### Q2 : Pourquoi ne pas exposer les entités directement ?

**Réponse** : Exposer les entités directement pose plusieurs problèmes : risque de lazy loading, exposition de données sensibles, couplage fort entre l'API et la base de données. Les DTOs permettent de contrôler exactement quelles données sont exposées.

### Q3 : Quel est le rôle de la couche Service ?

**Réponse** : La couche Service contient la logique métier de l'application. Elle orchestre les appels aux repositories, gère les transactions avec `@Transactional`, applique les règles de gestion, et coordonne les différentes opérations.

### Q4 : Comment les couches communiquent-elles ?

**Réponse** : Les couches communiquent via l'injection de dépendances. Le Controller appelle le Service, le Service appelle le Repository. Chaque couche ne connaît que la couche directement en dessous, ce qui crée un couplage faible.

### Q5 : Qu'est-ce que le Repository Pattern ?

**Réponse** : Le Repository Pattern est un pattern qui abstrait l'accès aux données. Il fournit une interface pour les opérations CRUD sans exposer les détails de la persistance. Spring Data JPA génère automatiquement l'implémentation.

---

[← Swagger/OpenAPI](07-SWAGGER.md) | [Retour à l'index](README.md)
