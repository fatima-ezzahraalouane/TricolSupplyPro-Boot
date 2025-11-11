# 📄 Pagination et Tri - Guide Complet

[← Retour à l'index](README.md) | [← Liquibase](04-LIQUIBASE.md)

---

## 📖 Table des matières

1. [Qu'est-ce que la pagination ?](#quest-ce-que-la-pagination)
2. [Interface Pageable](#interface-pageable)
3. [Implémentation](#implémentation)
4. [Tri (Sort)](#tri-sort)
5. [Objet Page](#objet-page)

---

## Qu'est-ce que la pagination ?

### Définition

**Pagination** : Technique pour diviser un grand ensemble de données en pages plus petites.

### Pourquoi paginer ?

| Avantage | Explication |
|----------|-------------|
| ⚡ **Performance** | Moins de données transférées |
| 👤 **Expérience utilisateur** | Chargement plus rapide |
| 💾 **Mémoire** | Évite de charger toutes les données |
| 🌐 **Bande passante** | Réduit le trafic réseau |
| 📱 **Mobile-friendly** | Adapté aux petits écrans |

### Exemple concret

**Sans pagination** :
```
GET /api/v1/produits
→ Retourne 10 000 produits (très lent !)
```

**Avec pagination** :
```
GET /api/v1/produits?page=0&size=10
→ Retourne 10 produits (rapide !)
```

---

## Interface Pageable

### Qu'est-ce que Pageable ?

**Pageable** est une interface Spring Data qui encapsule les informations de pagination et de tri.

```java
public interface Pageable {
    int getPageNumber();    // Numéro de page (commence à 0)
    int getPageSize();      // Taille de la page
    Sort getSort();         // Tri
    long getOffset();       // Offset (pageNumber * pageSize)
}
```

### Création d'un Pageable

**Méthode 1 : Automatique dans les Controllers**

```java
@GetMapping
public ResponseEntity<Page<ProduitDTO>> getAllProduits(Pageable pageable) {
    // Spring crée automatiquement le Pageable depuis les paramètres de requête
    Page<ProduitDTO> produits = produitService.findAll(pageable);
    return ResponseEntity.ok(produits);
}
```

**Méthode 2 : Avec valeurs par défaut**

```java
@GetMapping
public ResponseEntity<Page<ProduitDTO>> getAllProduits(
    @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable
) {
    Page<ProduitDTO> produits = produitService.findAll(pageable);
    return ResponseEntity.ok(produits);
}
```

**Méthode 3 : Manuelle**

```java
// Page 0, 10 éléments, tri par nom
Pageable pageable = PageRequest.of(0, 10, Sort.by("nom").ascending());

// Page 1, 20 éléments, tri par prix décroissant
Pageable pageable = PageRequest.of(1, 20, Sort.by("prixUnitaire").descending());
```

---

## Implémentation

### Dans les Controllers

```java
@RestController
@RequestMapping("/api/v1/produits")
@RequiredArgsConstructor
public class ProduitController {
    
    private final ProduitService produitService;
    
    @GetMapping
    @Operation(summary = "Liste des produits", description = "Retourne une liste paginée")
    public ResponseEntity<Page<ProduitDTO>> getAllProduits(
        @PageableDefault(size = 10, sort = "id") Pageable pageable
    ) {
        Page<ProduitDTO> produits = produitService.findAll(pageable);
        return ResponseEntity.ok(produits);
    }
}
```

### Dans les Services

```java
@Service
@RequiredArgsConstructor
public class ProduitService {
    
    private final ProduitRepository produitRepository;
    private final ProduitMapper produitMapper;
    
    public Page<ProduitDTO> findAll(Pageable pageable) {
        return produitRepository.findAll(pageable)
            .map(produitMapper::toDTO);  // Conversion Entity → DTO
    }
}
```

### Dans les Repositories

```java
@Repository
public interface ProduitRepository extends JpaRepository<Produit, Long> {
    // Méthode héritée automatiquement :
    Page<Produit> findAll(Pageable pageable);
    
    // Méthodes personnalisées avec pagination :
    Page<Produit> findByCategorie(String categorie, Pageable pageable);
    
    Page<Produit> findByNomContainingIgnoreCase(String nom, Pageable pageable);
}
```

---

## Tri (Sort)

### Paramètres de tri

**Format** : `sort={field},{direction}`

**Exemples** :

```bash
# Tri par nom (ordre croissant)
GET /api/v1/produits?sort=nom,asc

# Tri par prix (ordre décroissant)
GET /api/v1/produits?sort=prixUnitaire,desc

# Tri multiple : d'abord par catégorie, puis par nom
GET /api/v1/produits?sort=categorie,asc&sort=nom,asc
```

### Création d'un Sort

```java
// Tri simple ascendant
Sort sort = Sort.by("nom").ascending();

// Tri simple descendant
Sort sort = Sort.by("prixUnitaire").descending();

// Tri multiple
Sort sort = Sort.by("categorie").ascending()
                .and(Sort.by("prixUnitaire").descending());

// Tri avec direction
Sort sort = Sort.by(Sort.Direction.ASC, "nom");
```

### Tri dans les requêtes

```java
// Avec Pageable
Pageable pageable = PageRequest.of(0, 10, Sort.by("nom").ascending());
Page<Produit> produits = produitRepository.findAll(pageable);

// Sans pagination (juste tri)
List<Produit> produits = produitRepository.findAll(Sort.by("nom").ascending());
```

---

## Objet Page

### Qu'est-ce que Page ?

**Page<T>** est une interface qui contient les résultats paginés et les métadonnées.

```java
public interface Page<T> extends Slice<T> {
    int getTotalPages();        // Nombre total de pages
    long getTotalElements();    // Nombre total d'éléments
    List<T> getContent();       // Contenu de la page
    int getNumber();            // Numéro de page actuelle
    int getSize();              // Taille de la page
    boolean hasNext();          // Y a-t-il une page suivante ?
    boolean hasPrevious();      // Y a-t-il une page précédente ?
    boolean isFirst();          // Est-ce la première page ?
    boolean isLast();           // Est-ce la dernière page ?
}
```

### Réponse JSON

**Requête** :
```bash
GET /api/v1/produits?page=0&size=10&sort=nom,asc
```

**Réponse** :
```json
{
  "content": [
    {
      "id": 1,
      "nom": "Casque de sécurité",
      "prixUnitaire": 25.00,
      "stockActuel": 150
    },
    {
      "id": 2,
      "nom": "Gants de protection",
      "prixUnitaire": 8.50,
      "stockActuel": 300
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10,
    "sort": {
      "sorted": true,
      "unsorted": false,
      "empty": false
    },
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "totalElements": 45,
  "totalPages": 5,
  "last": false,
  "first": true,
  "size": 10,
  "number": 0,
  "numberOfElements": 10,
  "empty": false
}
```

### Informations clés dans la réponse

| Champ | Description | Exemple |
|-------|-------------|---------|
| `content` | Liste des éléments de la page | `[{...}, {...}]` |
| `totalElements` | Nombre total d'éléments | `45` |
| `totalPages` | Nombre total de pages | `5` |
| `number` | Numéro de la page actuelle | `0` (première page) |
| `size` | Nombre d'éléments par page | `10` |
| `first` | Est-ce la première page ? | `true` |
| `last` | Est-ce la dernière page ? | `false` |
| `numberOfElements` | Nombre d'éléments dans cette page | `10` |
| `empty` | La page est-elle vide ? | `false` |

---

## Exemples de requêtes

### Pagination simple

```bash
# Première page, 10 éléments
GET /api/v1/produits?page=0&size=10

# Deuxième page, 10 éléments
GET /api/v1/produits?page=1&size=10

# Première page, 20 éléments
GET /api/v1/produits?page=0&size=20
```

### Tri simple

```bash
# Tri par nom (ordre croissant)
GET /api/v1/produits?sort=nom,asc

# Tri par prix (ordre décroissant)
GET /api/v1/produits?sort=prixUnitaire,desc

# Tri par stock (ordre croissant)
GET /api/v1/produits?sort=stockActuel,asc
```

### Pagination + Tri

```bash
# Page 0, 10 éléments, tri par nom
GET /api/v1/produits?page=0&size=10&sort=nom,asc

# Page 2, 15 éléments, tri par prix décroissant
GET /api/v1/produits?page=2&size=15&sort=prixUnitaire,desc
```

### Tri multiple

```bash
# Tri par catégorie puis par nom
GET /api/v1/produits?sort=categorie,asc&sort=nom,asc

# Tri par catégorie puis par prix décroissant
GET /api/v1/produits?sort=categorie,asc&sort=prixUnitaire,desc
```

### Combinaison complète

```bash
# Page 2, 15 éléments, tri par catégorie puis prix
GET /api/v1/produits?page=2&size=15&sort=categorie,asc&sort=prixUnitaire,desc
```

---

## Bonnes pratiques

### 1. Valeurs par défaut

```java
@GetMapping
public ResponseEntity<Page<ProduitDTO>> getAllProduits(
    @PageableDefault(
        size = 10,              // Taille par défaut
        sort = "id",            // Tri par défaut
        direction = Sort.Direction.ASC
    ) Pageable pageable
) {
    // ...
}
```

### 2. Limiter la taille maximale

```properties
# application.properties
spring.data.web.pageable.max-page-size=100
spring.data.web.pageable.default-page-size=10
```

### 3. Valider les paramètres

```java
@GetMapping
public ResponseEntity<Page<ProduitDTO>> getAllProduits(
    @RequestParam(defaultValue = "0") @Min(0) int page,
    @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
    Pageable pageable
) {
    // ...
}
```

### 4. Documentation Swagger

```java
@GetMapping
@Operation(summary = "Liste des produits")
@Parameter(name = "page", description = "Numéro de page (commence à 0)")
@Parameter(name = "size", description = "Nombre d'éléments par page")
@Parameter(name = "sort", description = "Champ de tri (ex: nom,asc)")
public ResponseEntity<Page<ProduitDTO>> getAllProduits(Pageable pageable) {
    // ...
}
```

---

## Questions fréquentes

### Q1 : Qu'est-ce que la pagination ?

**Réponse** : La pagination est une technique qui divise un grand ensemble de données en pages plus petites. Cela améliore les performances en limitant le nombre de données transférées et affichées à la fois.

### Q2 : Comment fonctionne Pageable ?

**Réponse** : Pageable est une interface Spring Data qui encapsule les informations de pagination (numéro de page, taille) et de tri. Spring le crée automatiquement à partir des paramètres de requête HTTP (`page`, `size`, `sort`).

### Q3 : Pourquoi les pages commencent à 0 ?

**Réponse** : C'est une convention en programmation (zero-based indexing). La première page est la page 0, la deuxième est la page 1, etc. Cela simplifie les calculs d'offset.

### Q4 : Comment faire un tri sur plusieurs champs ?

**Réponse** : On utilise plusieurs paramètres `sort` dans l'URL : `?sort=categorie,asc&sort=nom,asc`. Spring Data applique les tris dans l'ordre spécifié.

### Q5 : Quelle est la différence entre Page et Slice ?

**Réponse** : `Page` contient le nombre total d'éléments et de pages (nécessite une requête COUNT), tandis que `Slice` ne sait que s'il y a une page suivante (plus performant). `Page` est plus utilisé car il fournit plus d'informations.

---

[← Liquibase](04-LIQUIBASE.md) | [Retour à l'index](README.md)
