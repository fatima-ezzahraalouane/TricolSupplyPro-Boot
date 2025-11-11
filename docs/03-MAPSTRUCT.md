# 🔄 MapStruct - Guide Complet

[← Retour à l'index](README.md) | [← Spring Data JPA](02-SPRING-DATA-JPA.md)

---

## 📖 Table des matières

1. [Qu'est-ce que MapStruct ?](#quest-ce-que-mapstruct)
2. [Pourquoi utiliser des DTOs ?](#pourquoi-utiliser-des-dtos)
3. [Mapping automatique](#mapping-automatique)
4. [Mappings personnalisés](#mappings-personnalisés)
5. [Configuration](#configuration)

---

## Qu'est-ce que MapStruct ?

### Définition

**MapStruct** est un générateur de code qui crée automatiquement des mappers pour convertir Entity ↔ DTO.

**Problème résolu** : Éviter le code répétitif de mapping manuel.

### Pourquoi MapStruct ?

**❌ Sans MapStruct (code répétitif)** :

```java
public class ProduitMapper {
    
    public ProduitDTO toDTO(Produit produit) {
        if (produit == null) {
            return null;
        }
        
        ProduitDTO dto = new ProduitDTO();
        dto.setId(produit.getId());
        dto.setNom(produit.getNom());
        dto.setDescription(produit.getDescription());
        dto.setPrixUnitaire(produit.getPrixUnitaire());
        dto.setCategorie(produit.getCategorie());
        dto.setStockActuel(produit.getStockActuel());
        dto.setCoutUnitaireMoyen(produit.getCoutUnitaireMoyen());
        
        return dto;
    }
    
    public Produit toEntity(ProduitDTO dto) {
        // ... encore plus de code répétitif
    }
}
```

**✅ Avec MapStruct (simple et automatique)** :

```java
@Mapper(componentModel = "spring")
public interface ProduitMapper {
    
    ProduitDTO toDTO(Produit produit);
    
    Produit toEntity(ProduitDTO dto);
    
    // MapStruct génère automatiquement l'implémentation !
}
```

---

## Pourquoi utiliser des DTOs ?

### Définition

**DTO** (Data Transfer Object) : Objet utilisé pour transférer des données entre les couches.

### Avantages

| Avantage | Explication |
|----------|-------------|
| 🛡️ **Sécurité** | Ne pas exposer toute l'entité (mots de passe, etc.) |
| 🎯 **Flexibilité** | Différentes vues des données selon le contexte |
| ⚡ **Performance** | Éviter lazy loading issues |
| ✅ **Validation** | Validation spécifique à l'API |
| 📦 **Découplage** | Séparer la couche API de la couche persistance |

### Exemple

**Entité** (ne jamais exposer directement) :
```java
@Entity
public class Produit {
    private Long id;
    private String nom;
    private BigDecimal prixUnitaire;
    private Integer stockActuel;
    private LocalDateTime createdAt;      // Données internes
    private LocalDateTime updatedAt;      // Données internes
    private List<MouvementStock> mouvements;  // Relations complexes
}
```

**DTO** (exposé dans l'API) :
```java
@Data
public class ProduitDTO {
    private Long id;
    private String nom;
    private BigDecimal prixUnitaire;
    private Integer stockActuel;
    // Pas de createdAt, updatedAt, mouvements
}
```

---

## Mapping automatique

### Interface Mapper

```java
@Mapper(componentModel = "spring")
public interface ProduitMapper {
    
    // Entity → DTO
    ProduitDTO toDTO(Produit produit);
    
    // DTO → Entity
    Produit toEntity(ProduitDTO dto);
    
    // Liste Entity → Liste DTO
    List<ProduitDTO> toDTOList(List<Produit> produits);
}
```

### Code généré par MapStruct

**Fichier généré** : `target/generated-sources/annotations/.../ProduitMapperImpl.java`

```java
@Component
public class ProduitMapperImpl implements ProduitMapper {
    
    @Override
    public ProduitDTO toDTO(Produit produit) {
        if (produit == null) {
            return null;
        }
        
        ProduitDTO dto = new ProduitDTO();
        dto.setId(produit.getId());
        dto.setNom(produit.getNom());
        dto.setPrixUnitaire(produit.getPrixUnitaire());
        dto.setStockActuel(produit.getStockActuel());
        dto.setCategorie(produit.getCategorie());
        dto.setCoutUnitaireMoyen(produit.getCoutUnitaireMoyen());
        
        return dto;
    }
    
    @Override
    public Produit toEntity(ProduitDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Produit produit = new Produit();
        produit.setId(dto.getId());
        produit.setNom(dto.getNom());
        // ... mapping automatique
        
        return produit;
    }
}
```

### Utilisation dans les Services

```java
@Service
@RequiredArgsConstructor
public class ProduitService {
    
    private final ProduitRepository produitRepository;
    private final ProduitMapper produitMapper;  // Injecté par Spring
    
    public ProduitDTO findById(Long id) {
        Produit produit = produitRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Produit", id));
        
        return produitMapper.toDTO(produit);  // Conversion automatique
    }
    
    public ProduitDTO create(ProduitDTO dto) {
        Produit produit = produitMapper.toEntity(dto);  // DTO → Entity
        Produit saved = produitRepository.save(produit);
        return produitMapper.toDTO(saved);  // Entity → DTO
    }
}
```

---

## Mappings personnalisés

### Cas 1 : Noms de champs différents

```java
@Mapper(componentModel = "spring")
public interface CommandeFournisseurMapper {
    
    @Mapping(source = "fournisseur.id", target = "fournisseurId")
    @Mapping(source = "fournisseur.societe", target = "nomFournisseur")
    CommandeFournisseurDTO toDTO(CommandeFournisseur commande);
}
```

**Explication** :
- `source` : Champ dans l'entité
- `target` : Champ dans le DTO
- `fournisseur.id` : Navigation dans les relations

### Cas 2 : Ignorer des champs

```java
@Mapper(componentModel = "spring")
public interface ProduitMapper {
    
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "mouvements", ignore = true)
    Produit toEntity(ProduitDTO dto);
}
```

### Cas 3 : Mapper des relations

```java
@Mapper(componentModel = "spring")
public interface MouvementStockMapper {
    
    @Mapping(source = "produit.id", target = "produitId")
    @Mapping(source = "produit.nom", target = "nomProduit")
    @Mapping(source = "commandeFournisseur.id", target = "commandeFournisseurId")
    MouvementStockDTO toDTO(MouvementStock mouvement);
}
```

### Cas 4 : Méthodes personnalisées

```java
@Mapper(componentModel = "spring")
public interface CommandeFournisseurMapper {
    
    @Mapping(source = "fournisseur.id", target = "fournisseurId")
    @Mapping(source = "commandeProduits", target = "produits")
    CommandeFournisseurDTO toDTO(CommandeFournisseur commande);
    
    // Méthode personnalisée pour mapper les produits
    default List<ProduitCommandeDTO> mapCommandeProduits(List<CommandeProduit> commandeProduits) {
        if (commandeProduits == null) {
            return new ArrayList<>();
        }
        
        return commandeProduits.stream()
            .map(cp -> ProduitCommandeDTO.builder()
                .produitId(cp.getProduit().getId())
                .quantite(cp.getQuantite())
                .prixUnitaireCommande(cp.getPrixUnitaireCommande())
                .build())
            .toList();
    }
}
```

### Cas 5 : Expressions

```java
@Mapper(componentModel = "spring")
public interface ProduitMapper {
    
    @Mapping(target = "disponible", expression = "java(produit.getStockActuel() > 0)")
    ProduitDTO toDTO(Produit produit);
}
```

---

## Configuration

### Configuration Maven

**pom.xml** :

```xml
<dependencies>
    <!-- MapStruct -->
    <dependency>
        <groupId>org.mapstruct</groupId>
        <artifactId>mapstruct</artifactId>
        <version>1.5.5.Final</version>
    </dependency>
</dependencies>

<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.11.0</version>
            <configuration>
                <source>17</source>
                <target>17</target>
                <annotationProcessorPaths>
                    <!-- MapStruct Processor -->
                    <path>
                        <groupId>org.mapstruct</groupId>
                        <artifactId>mapstruct-processor</artifactId>
                        <version>1.5.5.Final</version>
                    </path>
                    <!-- Lombok -->
                    <path>
                        <groupId>org.projectlombok</groupId>
                        <artifactId>lombok</artifactId>
                        <version>1.18.30</version>
                    </path>
                    <!-- Lombok + MapStruct Binding -->
                    <path>
                        <groupId>org.projectlombok</groupId>
                        <artifactId>lombok-mapstruct-binding</artifactId>
                        <version>0.2.0</version>
                    </path>
                </annotationProcessorPaths>
            </configuration>
        </plugin>
    </plugins>
</build>
```

### Génération du code

**Commandes Maven** :

```bash
# Compiler le projet (génère les mappers)
mvn clean compile

# Les mappers générés se trouvent dans :
target/generated-sources/annotations/com/tricol/supply/mapper/
```

### Vérifier les mappers générés

```
target/generated-sources/annotations/
└── com/tricol/supply/mapper/
    ├── ProduitMapperImpl.java
    ├── FournisseurMapperImpl.java
    ├── CommandeFournisseurMapperImpl.java
    └── MouvementStockMapperImpl.java
```

---

## Avantages de MapStruct

| Avantage | Explication |
|----------|-------------|
| ⚡ **Performance** | Code généré à la compilation (pas de réflexion) |
| 🛡️ **Type-safe** | Erreurs détectées à la compilation |
| 🧹 **Moins de code** | Pas de mapping manuel répétitif |
| 🔧 **Maintenable** | Changements automatiques si entité change |
| 📝 **Lisible** | Interface claire et simple |
| 🎯 **Flexible** | Mappings personnalisés possibles |
| 🔗 **Intégration Spring** | `componentModel = "spring"` |

---

## Questions fréquentes

### Q1 : Pourquoi utiliser des DTOs ?

**Réponse** : Les DTOs permettent de séparer la couche API de la couche persistance. Ils offrent plus de sécurité (ne pas exposer toutes les données), de flexibilité (différentes vues), et évitent les problèmes de lazy loading. Ils permettent aussi d'avoir une validation spécifique à l'API.

### Q2 : Comment MapStruct génère le code ?

**Réponse** : MapStruct est un annotation processor qui génère le code Java à la compilation. Il analyse les interfaces annotées avec `@Mapper` et crée automatiquement les implémentations dans `target/generated-sources/annotations/`.

### Q3 : Quelle est la différence avec d'autres mappers ?

**Réponse** : Contrairement à ModelMapper ou Dozer qui utilisent la réflexion au runtime, MapStruct génère du code à la compilation. Cela le rend beaucoup plus performant et permet de détecter les erreurs de mapping dès la compilation.

### Q4 : Comment mapper des relations complexes ?

**Réponse** : On utilise `@Mapping` avec `source` pour naviguer dans les relations (ex: `source = "fournisseur.id"`), ou on crée des méthodes personnalisées avec `default` pour des mappings plus complexes.

### Q5 : Que faire si les noms de champs sont différents ?

**Réponse** : On utilise `@Mapping(source = "champEntity", target = "champDTO")` pour indiquer explicitement la correspondance entre les champs.

---

[← Spring Data JPA](02-SPRING-DATA-JPA.md) | [Suivant : Liquibase →](04-LIQUIBASE.md)
