# 🗄️ Spring Data JPA - Guide Complet

[← Retour à l'index](README.md) | [← Spring Boot](01-SPRING-BOOT.md)

---

## 📖 Table des matières

1. [Qu'est-ce que JPA ?](#quest-ce-que-jpa)
2. [Spring Data JPA](#spring-data-jpa)
3. [Entités JPA](#entités-jpa)
4. [Relations](#relations-jpa)
5. [Repositories](#repositories)
6. [Transactions](#transactions)

---

## Qu'est-ce que JPA ?

### Définition

**JPA** (Java Persistence API) est une spécification Java pour gérer la persistance des données relationnelles.

**Termes importants** :
- **Spécification** : Ensemble de règles et d'interfaces (comme un contrat)
- **Implémentation** : Code qui respecte ces règles (Hibernate, EclipseLink)
- **ORM** : Object-Relational Mapping (mapper objets Java ↔ tables SQL)

### Pourquoi JPA ?

**❌ Sans JPA (JDBC traditionnel)** :

```java
public Produit findById(Long id) {
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    
    try {
        conn = dataSource.getConnection();
        stmt = conn.prepareStatement("SELECT * FROM produits WHERE id = ?");
        stmt.setLong(1, id);
        rs = stmt.executeQuery();
        
        if (rs.next()) {
            Produit produit = new Produit();
            produit.setId(rs.getLong("id"));
            produit.setNom(rs.getString("nom"));
            produit.setPrixUnitaire(rs.getBigDecimal("prix_unitaire"));
            // ... mapper tous les champs manuellement
            return produit;
        }
    } catch (SQLException e) {
        // Gestion erreurs
    } finally {
        // Fermer rs, stmt, conn
    }
    return null;
}
```

**✅ Avec JPA** :

```java
@Repository
public interface ProduitRepository extends JpaRepository<Produit, Long> {
    // C'est tout ! Spring Data JPA génère l'implémentation
}

// Utilisation
Produit produit = produitRepository.findById(1L).orElse(null);
```

---

## Spring Data JPA

### Architecture

```
┌─────────────────────────────────┐
│   Votre Code (Service)          │
│   produitRepository.findById()  │
└─────────────────────────────────┘
            ↓
┌─────────────────────────────────┐
│   Spring Data JPA               │
│   (Génère l'implémentation)     │
└─────────────────────────────────┘
            ↓
┌─────────────────────────────────┐
│   JPA (Spécification)           │
│   EntityManager, Query API      │
└─────────────────────────────────┘
            ↓
┌─────────────────────────────────┐
│   Hibernate (Implémentation)    │
│   Génère les requêtes SQL       │
└─────────────────────────────────┘
            ↓
┌─────────────────────────────────┐
│   JDBC Driver (PostgreSQL)      │
└─────────────────────────────────┘
            ↓
┌─────────────────────────────────┐
│   Base de données PostgreSQL    │
└─────────────────────────────────┘
```

### Configuration

**application.properties** :

```properties
# Connexion PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/tricolsupplyboot
spring.datasource.username=postgres
spring.datasource.password=admin

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=none  # Liquibase gère le schéma
spring.jpa.show-sql=true            # Afficher les requêtes SQL
spring.jpa.properties.hibernate.format_sql=true  # Formater SQL
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

---

## Entités JPA

### Qu'est-ce qu'une entité ?

Une **entité** est une classe Java qui représente une table de base de données.

### Exemple complet

```java
@Entity                              // Marque comme entité JPA
@Table(name = "produits")           // Nom de la table
@Data                               // Lombok : getters/setters
@Builder                            // Pattern Builder
@NoArgsConstructor
@AllArgsConstructor
public class Produit {
    
    @Id                             // Clé primaire
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100, unique = true)
    private String nom;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "prix_unitaire", nullable = false, precision = 10, scale = 2)
    private BigDecimal prixUnitaire;
    
    @Column(length = 50)
    private String categorie;
    
    @Column(name = "stock_actuel")
    private Integer stockActuel;
    
    @Column(name = "cout_unitaire_moyen", precision = 10, scale = 2)
    private BigDecimal coutUnitaireMoyen;
    
    @CreationTimestamp              // Timestamp automatique création
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp                // Timestamp automatique modification
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
```

### Mapping Table ↔ Entité

**Table PostgreSQL** :
```sql
CREATE TABLE produits (
    id BIGSERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    prix_unitaire DECIMAL(10,2) NOT NULL,
    categorie VARCHAR(50),
    stock_actuel INTEGER,
    cout_unitaire_moyen DECIMAL(10,2),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
```

### Annotations JPA importantes

| Annotation | Rôle | Exemple |
|------------|------|---------|
| `@Entity` | Marque une classe comme entité | Toutes les entités |
| `@Table` | Nom de la table | `@Table(name = "produits")` |
| `@Id` | Clé primaire | Champ `id` |
| `@GeneratedValue` | Génération auto ID | `IDENTITY`, `SEQUENCE` |
| `@Column` | Configuration colonne | `nullable`, `length`, `unique` |
| `@Enumerated` | Mapping enums | `EnumType.STRING` |
| `@Temporal` | Mapping dates | `TemporalType.TIMESTAMP` |
| `@Lob` | Large Object | Fichiers, textes longs |
| `@Transient` | Champ non persisté | Champs calculés |
| `@CreationTimestamp` | Timestamp création | Audit |
| `@UpdateTimestamp` | Timestamp modification | Audit |

### Stratégies de génération d'ID

| Stratégie | Description | Utilisation |
|-----------|-------------|-------------|
| `IDENTITY` | Auto-incrémenté par la BD | PostgreSQL SERIAL |
| `SEQUENCE` | Utilise une séquence | PostgreSQL SEQUENCE |
| `AUTO` | JPA choisit | Par défaut |
| `TABLE` | Table dédiée | Rarement utilisé |

**Notre projet utilise IDENTITY** :
```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
```

---

## Relations JPA

### @ManyToOne (Plusieurs vers Un)

**Exemple** : Plusieurs mouvements → Un produit

```java
@Entity
@Table(name = "mouvements_stock")
public class MouvementStock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Plusieurs mouvements → Un produit
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produit_id")
    private Produit produit;
    
    // Plusieurs mouvements → Une commande (optionnel)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commande_fournisseur_id")
    private CommandeFournisseur commandeFournisseur;
}
```

**SQL généré** :
```sql
CREATE TABLE mouvements_stock (
    id BIGSERIAL PRIMARY KEY,
    produit_id BIGINT,
    commande_fournisseur_id BIGINT,
    FOREIGN KEY (produit_id) REFERENCES produits(id),
    FOREIGN KEY (commande_fournisseur_id) REFERENCES commandes_fournisseur(id)
);
```

### @OneToMany (Un vers Plusieurs)

**Exemple** : Une commande → Plusieurs produits

```java
@Entity
@Table(name = "commandes_fournisseur")
public class CommandeFournisseur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Une commande → Plusieurs produits
    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommandeProduit> commandeProduits = new ArrayList<>();
}

@Entity
@Table(name = "commandes_produits")
public class CommandeProduit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "commande_id")
    private CommandeFournisseur commande;
    
    @ManyToOne
    @JoinColumn(name = "produit_id")
    private Produit produit;
}
```

**Explications** :
- `mappedBy` : Indique que l'autre entité possède la relation
- `cascade` : Opérations en cascade (save, delete)
- `orphanRemoval` : Supprime les enfants orphelins

### Fetch Types

| Type | Comportement | Utilisation |
|------|--------------|-------------|
| `LAZY` | Chargement à la demande | ✅ Recommandé (performance) |
| `EAGER` | Chargement immédiat | ⚠️ Peut causer N+1 queries |

**Exemple** :
```java
@ManyToOne(fetch = FetchType.LAZY)  // ✅ Recommandé
@JoinColumn(name = "produit_id")
private Produit produit;
```

### Cascade Types

| Type | Description |
|------|-------------|
| `PERSIST` | Persiste les entités liées |
| `MERGE` | Fusionne les entités liées |
| `REMOVE` | Supprime les entités liées |
| `REFRESH` | Rafraîchit les entités liées |
| `DETACH` | Détache les entités liées |
| `ALL` | Toutes les opérations |

---

## Repositories

### Interface JpaRepository

```java
@Repository
public interface ProduitRepository extends JpaRepository<Produit, Long> {
    // Méthodes héritées automatiquement :
    // - save(entity)
    // - findById(id)
    // - findAll()
    // - findAll(Pageable)
    // - deleteById(id)
    // - count()
    // - existsById(id)
}
```

### Méthodes personnalisées

**Query Methods** (Spring Data génère la requête) :

```java
@Repository
public interface ProduitRepository extends JpaRepository<Produit, Long> {
    
    // Trouver par catégorie
    List<Produit> findByCategorie(String categorie);
    
    // Trouver par nom (contient, ignore case)
    List<Produit> findByNomContainingIgnoreCase(String nom);
    
    // Trouver par prix entre deux valeurs
    List<Produit> findByPrixUnitaireBetween(BigDecimal min, BigDecimal max);
    
    // Trouver par stock inférieur à un seuil
    List<Produit> findByStockActuelLessThan(Integer seuil);
}
```

**@Query (JPQL personnalisé)** :

```java
@Repository
public interface ProduitRepository extends JpaRepository<Produit, Long> {
    
    @Query("SELECT p FROM Produit p WHERE p.stockActuel < :seuil")
    List<Produit> findProduitsEnRupture(@Param("seuil") Integer seuil);
    
    @Query("SELECT p FROM Produit p WHERE p.categorie = :categorie ORDER BY p.prixUnitaire DESC")
    List<Produit> findByCategorieSortedByPrice(@Param("categorie") String categorie);
}
```

**Native SQL** :

```java
@Query(value = "SELECT * FROM produits WHERE stock_actuel = 0", nativeQuery = true)
List<Produit> findProduitsEpuises();
```

---

## Transactions

### @Transactional

**Définition** : Garantit l'atomicité des opérations (tout ou rien).

```java
@Service
@RequiredArgsConstructor
public class CommandeFournisseurService {
    
    @Transactional  // Si erreur → ROLLBACK de toutes les opérations
    public CommandeFournisseurDetailDTO create(CommandeFournisseurDTO dto) {
        // 1. Créer la commande
        CommandeFournisseur commande = commandeRepository.save(...);
        
        // 2. Diminuer le stock
        produit.setStockActuel(produit.getStockActuel() - quantite);
        produitRepository.save(produit);
        
        // 3. Créer les mouvements
        mouvementStockRepository.save(mouvement);
        
        // Si erreur ici → ROLLBACK de 1, 2 et 3
        return commandeMapper.toDetailDTO(commande);
    }
}
```

### Propriétés de @Transactional

| Propriété | Description | Exemple |
|-----------|-------------|---------|
| `readOnly` | Optimisation lecture | `@Transactional(readOnly = true)` |
| `rollbackFor` | Exceptions rollback | `@Transactional(rollbackFor = Exception.class)` |
| `noRollbackFor` | Exceptions sans rollback | `@Transactional(noRollbackFor = CustomException.class)` |
| `propagation` | Comportement propagation | `@Transactional(propagation = Propagation.REQUIRES_NEW)` |
| `isolation` | Niveau d'isolation | `@Transactional(isolation = Isolation.READ_COMMITTED)` |

---

## Questions fréquentes

### Q1 : Qu'est-ce que JPA ?

**Réponse** : JPA (Java Persistence API) est une spécification Java pour gérer la persistance des données relationnelles. C'est un ORM (Object-Relational Mapping) qui permet de mapper des objets Java vers des tables de base de données. Hibernate est l'implémentation la plus populaire de JPA.

### Q2 : Quelle est la différence entre JPA et Hibernate ?

**Réponse** : JPA est une spécification (ensemble de règles), tandis qu'Hibernate est une implémentation de cette spécification. Spring Data JPA utilise Hibernate par défaut pour communiquer avec la base de données.

### Q3 : Pourquoi utiliser LAZY loading ?

**Réponse** : Le LAZY loading charge les données uniquement quand elles sont nécessaires, ce qui améliore les performances en évitant de charger des données inutiles. Par exemple, si on récupère un produit, on ne charge pas automatiquement tous ses mouvements de stock.

### Q4 : Qu'est-ce qu'une transaction ?

**Réponse** : Une transaction est un ensemble d'opérations qui doivent toutes réussir ou toutes échouer. Si une erreur survient, toutes les opérations sont annulées (rollback). Cela garantit la cohérence des données.

### Q5 : Comment Spring Data JPA génère les requêtes ?

**Réponse** : Spring Data JPA analyse le nom des méthodes (comme `findByNom`) et génère automatiquement la requête SQL correspondante. On peut aussi utiliser `@Query` pour des requêtes personnalisées.

---

[← Spring Boot](01-SPRING-BOOT.md) | [Suivant : MapStruct →](03-MAPSTRUCT.md)
