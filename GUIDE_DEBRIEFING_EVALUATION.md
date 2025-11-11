# 🎯 GUIDE DE DÉBRIEFING - ÉVALUATION TRICOLSUPPLYPRO-BOOT

> **Guide complet pour réussir votre présentation et évaluation avec la formatrice**
> 
> **Projet** : Gestion des Approvisionnements pour Tricol  
> **Technologies** : Spring Boot 3.5.7, Spring Data JPA, MapStruct, Liquibase, PostgreSQL  
> **Date de préparation** : Novembre 2025

---

## 📋 TABLE DES MATIÈRES

1. [Préparation avant l'évaluation](#1-préparation-avant-lévaluation)
2. [Présentation du contexte métier](#2-présentation-du-contexte-métier)
3. [Démonstration technique](#3-démonstration-technique)
4. [Points clés à mettre en avant](#4-points-clés-à-mettre-en-avant)
5. [Questions attendues et réponses](#5-questions-attendues-et-réponses)
6. [Architecture et choix techniques](#6-architecture-et-choix-techniques)
7. [Pièges à éviter](#7-pièges-à-éviter)
8. [Checklist finale](#8-checklist-finale)

---

## 1. PRÉPARATION AVANT L'ÉVALUATION

### ✅ Checklist technique (à faire AVANT l'évaluation)

#### A. Vérifier que l'environnement fonctionne

```powershell
# 1. Vérifier que PostgreSQL est démarré
# Ouvrir PowerShell et tester la connexion à la base de données

# 2. Nettoyer et compiler le projet
cd C:\Users\youco\IdeaProjects\TricolSupplyPro-Boot
mvn clean install

# 3. Lancer l'application
mvn spring-boot:run

# 4. Vérifier que l'application démarre sans erreur
# L'application doit être accessible sur http://localhost:8080
```

#### B. Vérifier les accès

- [ ] **Swagger UI** : http://localhost:8080/swagger-ui.html
- [ ] **API Docs** : http://localhost:8080/api-docs
- [ ] **Base de données** : PostgreSQL sur `localhost:5432` (DB: `tricolsupplyboot`)

#### C. Préparer les données de démonstration

**Créer des données de test via Postman ou Swagger :**

1. **Créer 2-3 fournisseurs**
2. **Créer 3-4 produits** avec des stocks variés
3. **Créer 2 commandes** avec différents statuts
4. **Tester le changement de statut** vers LIVREE pour générer des mouvements

#### D. Ouvrir les bons fichiers dans VS Code

Organisez vos onglets pour montrer rapidement :
- `README.md` (documentation)
- `pom.xml` (dépendances)
- `CommandeFournisseurController.java`
- `CommandeFournisseurService.java`
- `Produit.java` (entité)
- `application.properties`
- Un fichier de migration Liquibase

---

## 2. PRÉSENTATION DU CONTEXTE MÉTIER

### 🎤 Introduction (2-3 minutes)

**Script d'introduction :**

> *"Bonjour, je vais vous présenter mon projet **TricolSupplyPro-Boot**, une API REST développée avec Spring Boot pour gérer les approvisionnements de l'entreprise Tricol.*
>
> *Tricol est une entreprise spécialisée dans la fabrication de vêtements professionnels. Après avoir mis en place un premier module de gestion des fournisseurs, la direction souhaitait digitaliser le processus de gestion des commandes fournisseurs.*
>
> *L'objectif de ce projet est de permettre de suivre l'ensemble du cycle de vie d'une commande, depuis sa création jusqu'à la livraison, tout en assurant un suivi précis du stock et de sa valorisation."*

### 🎯 Objectifs métier à expliquer

1. **Gestion complète des commandes fournisseurs**
   - Création, modification, consultation, suppression
   - Gestion des statuts : EN_ATTENTE → VALIDEE → LIVREE / ANNULEE

2. **Suivi du stock en temps réel**
   - Mise à jour automatique lors des opérations
   - Traçabilité complète via les mouvements de stock

3. **Valorisation du stock selon la méthode CUMP**
   - Calcul automatique du Coût Unitaire Moyen Pondéré
   - Permet de connaître le coût réel des produits en stock

### 📊 Les acteurs du système

**Bien expliquer la différence :**
- **Fournisseur** : entreprise qui ACHÈTE à Tricol (= client de Tricol)
- **Produit** : article vendu par Tricol (veste, pantalon professionnel, etc.)
- **Commande** : commande PASSÉE PAR un fournisseur (= vente pour Tricol)
- **Mouvement de stock** : traçabilité des entrées/sorties de marchandises

---

## 3. DÉMONSTRATION TECHNIQUE

### 🖥️ Scénario de démonstration recommandé

#### Étape 1 : Montrer la documentation Swagger (2 minutes)

```
1. Ouvrir http://localhost:8080/swagger-ui.html
2. Montrer l'organisation des endpoints par entité :
   - fournisseur-controller
   - produit-controller
   - commande-fournisseur-controller
   - mouvement-stock-controller
3. Expliquer : "Swagger génère automatiquement la documentation interactive de l'API"
```

#### Étape 2 : Créer un fournisseur (3 minutes)

**Via Swagger UI :**

```json
POST /api/v1/fournisseurs
{
  "societe": "Textile Pro SA",
  "adresse": "123 Rue de l'Industrie",
  "contact": "Mohammed Alami",
  "email": "contact@textilepro.ma",
  "telephone": "0522123456",
  "ville": "Casablanca",
  "ice": "001234567890123"
}
```

**Points à expliquer :**
- ✅ Validation automatique de l'email
- ✅ ICE unique (15 caractères)
- ✅ Retour HTTP 201 Created avec l'objet créé

**Montrer le code :**
```java
// Ouvrir FournisseurController.java
@PostMapping
public ResponseEntity<FournisseurDTO> createFournisseur(@Valid @RequestBody FournisseurDTO dto)
```
- Expliquer `@Valid` pour la validation automatique
- Expliquer le DTO pour séparer API et modèle de données

#### Étape 3 : Créer un produit (3 minutes)

```json
POST /api/v1/produits
{
  "nom": "Veste Professionnelle Bleue",
  "description": "Veste de travail résistante",
  "prixUnitaire": 150.00,
  "categorie": "VETEMENTS",
  "stockActuel": 100
}
```

**Points à expliquer :**
- ✅ Création automatique d'un mouvement de type ENTREE
- ✅ Initialisation du CUMP = prix unitaire
- ✅ Nom unique

**Montrer dans la base de données :**
```sql
SELECT * FROM produits WHERE nom = 'Veste Professionnelle Bleue';
SELECT * FROM mouvements_stock WHERE produit_id = [l'id du produit créé];
```

#### Étape 4 : Créer une commande (5 minutes) ⭐ **POINT CLÉ**

```json
POST /api/v1/commandes
{
  "fournisseurId": 1,
  "produits": [
    {
      "produitId": 1,
      "quantite": 20,
      "prixUnitaireCommande": 150.00
    }
  ]
}
```

**Points ESSENTIELS à expliquer :**

1. **Calcul automatique du montant total**
   ```
   montantTotal = 20 × 150.00 = 3000.00 €
   ```

2. **Diminution immédiate du stock** (réservation)
   ```
   Stock avant : 100
   Stock après : 80 (réservation de 20 unités)
   ```

3. **Statut initial** : EN_ATTENTE

**Montrer le code service :**
```java
// Ouvrir CommandeFournisseurService.java
// Méthode createCommande()
// Expliquer la logique de réservation du stock
```

#### Étape 5 : Changer le statut vers LIVREE (5 minutes) ⭐ **POINT CLÉ**

```
PATCH /api/v1/commandes/1/statut?statut=LIVREE
```

**Points ESSENTIELS à expliquer :**

1. **Création automatique d'un mouvement SORTIE**
   - Type : SORTIE
   - Quantité : 20
   - Prix unitaire : 150.00
   - Référence à la commande

2. **Le stock n'est PAS modifié** (déjà diminué à la création)

3. **Traçabilité complète**

**Montrer les mouvements :**
```
GET /api/v1/mouvements/commande/1
```

**Résultat attendu :**
```json
[
  {
    "id": 2,
    "dateMouvement": "2025-11-10T14:30:00",
    "typeMouvement": "SORTIE",
    "quantite": 20,
    "prixUnitaire": 150.00,
    "produitId": 1,
    "commandeFournisseurId": 1
  }
]
```

#### Étape 6 : Démontrer l'annulation (3 minutes)

**Créer une nouvelle commande puis l'annuler :**

```json
POST /api/v1/commandes
{
  "fournisseurId": 1,
  "produits": [
    {
      "produitId": 1,
      "quantite": 10,
      "prixUnitaireCommande": 150.00
    }
  ]
}
```

Puis :
```
PATCH /api/v1/commandes/2/statut?statut=ANNULEE
```

**Points à expliquer :**
- ✅ Restauration du stock (80 + 10 = 90)
- ✅ Création d'un mouvement AJUSTEMENT
- ✅ La commande ne peut plus être modifiée

#### Étape 7 : Montrer la pagination (2 minutes)

```
GET /api/v1/commandes?page=0&size=5&sort=dateCommande,desc
```

**Montrer la réponse :**
```json
{
  "content": [...],
  "totalElements": 2,
  "totalPages": 1,
  "size": 5,
  "number": 0
}
```

**Expliquer :**
- Mécanisme natif de Spring Data JPA
- Performance optimale (pas de chargement de toutes les données)

---

## 4. POINTS CLÉS À METTRE EN AVANT

### 🌟 Technologies modernes utilisées

| Technologie | Pourquoi / Avantage |
|------------|---------------------|
| **Spring Boot 3.5.7** | Framework moderne, configuration automatique, productivité |
| **Spring Data JPA** | Abstraction de la couche de persistence, moins de code SQL |
| **MapStruct** | Mapping automatique Entity ↔ DTO, type-safe, performance |
| **Liquibase** | Gestion des migrations, versioning de la BDD, traçabilité |
| **Swagger/OpenAPI** | Documentation automatique, interactive, à jour |
| **Jakarta Validation** | Validation déclarative, moins d'erreurs |
| **PostgreSQL** | BDD relationnelle robuste, open-source |

### 🏗️ Architecture en couches

**Expliquer le flux de données :**

```
1. Client HTTP
   ↓
2. Controller (@RestController)
   → Validation des données (@Valid)
   ↓
3. DTO (Data Transfer Object)
   → Découplage API / Modèle
   ↓
4. Mapper (MapStruct)
   → Conversion automatique
   ↓
5. Service (@Service)
   → Logique métier (CUMP, mouvements)
   ↓
6. Repository (Spring Data JPA)
   → Accès aux données
   ↓
7. Base de données PostgreSQL
```

**Avantages :**
- ✅ Séparation des responsabilités (SoC)
- ✅ Testabilité
- ✅ Maintenabilité
- ✅ Évolutivité

### 💰 Logique métier complexe : Calcul du CUMP

**Expliquer la formule :**

```
CUMP = (Valeur stock ancien + Valeur nouvelle entrée) / Quantité totale

Où :
- Valeur stock ancien = stock_actuel × cout_unitaire_moyen_actuel
- Valeur nouvelle entrée = quantite_entree × prix_unitaire_entree
- Quantité totale = stock_actuel + quantite_entree
```

**Exemple concret :**

```
Stock actuel : 100 unités à 150€ = 15 000€
Nouvelle entrée : 50 unités à 180€ = 9 000€

CUMP = (15 000€ + 9 000€) / (100 + 50)
     = 24 000€ / 150
     = 160€
```

**Montrer le code :**
```java
// ProduitService.java
private void calculerEtMettreAJourCUMP(Produit produit, int quantite, BigDecimal prixUnitaire) {
    // Valeur du stock actuel
    BigDecimal valeurStockActuel = produit.getCoutUnitaireMoyen()
            .multiply(new BigDecimal(produit.getStockActuel()));
    
    // Valeur de la nouvelle entrée
    BigDecimal valeurNouvelleEntree = prixUnitaire
            .multiply(new BigDecimal(quantite));
    
    // Stock total après entrée
    int stockTotal = produit.getStockActuel() + quantite;
    
    // Calcul du nouveau CUMP
    BigDecimal nouveauCUMP = valeurStockActuel.add(valeurNouvelleEntree)
            .divide(new BigDecimal(stockTotal), 2, RoundingMode.HALF_UP);
    
    produit.setCoutUnitaireMoyen(nouveauCUMP);
}
```

### 🔄 Gestion des statuts et règles métier

**Matrice des transitions autorisées :**

| Statut actuel | Transitions possibles |
|--------------|----------------------|
| EN_ATTENTE | → VALIDEE, ANNULEE |
| VALIDEE | → LIVREE, ANNULEE |
| LIVREE | ❌ Aucune (statut final) |
| ANNULEE | ❌ Aucune (statut final) |

**Règles de modification/suppression :**

| Opération | Statuts autorisés |
|-----------|------------------|
| **Modifier** | EN_ATTENTE uniquement |
| **Supprimer** | EN_ATTENTE, ANNULEE |

**Montrer le code de validation :**
```java
// CommandeFournisseurService.java
public CommandeFournisseurDTO updateCommande(Long id, CommandeFournisseurDTO dto) {
    CommandeFournisseur commande = commandeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Commande non trouvée"));
    
    // Vérification du statut
    if (commande.getStatut() != StatutCommande.EN_ATTENTE) {
        throw new IllegalStateException(
            "Seules les commandes EN_ATTENTE peuvent être modifiées");
    }
    // ...
}
```

### 📊 Traçabilité complète avec les mouvements de stock

**Types de mouvements :**

| Type | Déclencheur | Stock modifié | Commande liée |
|------|------------|---------------|---------------|
| **ENTREE** | Création produit | ✅ Oui (+) | ❌ Non |
| **SORTIE** | Commande LIVREE | ❌ Non (déjà fait) | ✅ Oui |
| **AJUSTEMENT** | Modification manuelle OU Annulation | ✅ Oui (±) | Selon le cas |

**Avantages :**
- ✅ Historique complet de tous les mouvements
- ✅ Audit trail pour la comptabilité
- ✅ Détection des anomalies
- ✅ Analyse des tendances

---

## 5. QUESTIONS ATTENDUES ET RÉPONSES

### ❓ Questions sur l'architecture

**Q1 : Pourquoi avoir utilisé des DTOs au lieu de renvoyer directement les entités ?**

**R :** 
> *"J'ai utilisé des DTOs pour plusieurs raisons :*
> 
> *1. **Sécurité** : les DTOs permettent de contrôler exactement quelles données sont exposées à l'API. Par exemple, on peut avoir des champs techniques dans l'entité qu'on ne veut pas exposer.*
> 
> *2. **Découplage** : si je modifie ma base de données, je n'impacte pas forcément l'API. Le DTO est un contrat stable avec les clients.*
> 
> *3. **Performance** : avec JPA, renvoyer une entité directement peut déclencher des chargements lazy qui causent des problèmes de performance ou des erreurs (LazyInitializationException).*
> 
> *4. **Validation** : je peux avoir des règles de validation différentes entre la création et la mise à jour grâce aux DTOs."*

**Q2 : Pourquoi utiliser MapStruct plutôt que mapper manuellement ?**

**R :**
> *"MapStruct génère le code de mapping à la compilation, ce qui présente plusieurs avantages :*
> 
> *1. **Performance** : le code généré est aussi rapide qu'un mapping manuel, contrairement aux solutions par réflexion.*
> 
> *2. **Sûreté** : si je modifie un champ et oublie de mettre à jour le mapper, j'ai une erreur de compilation.*
> 
> *3. **Moins de code** : MapStruct génère automatiquement tout le code répétitif.*
> 
> *4. **Maintenabilité** : les mappings sont déclarés de façon claire via des interfaces."*

**Q3 : Pourquoi Liquibase et pas JPA auto-create ?**

**R :**
> *"Liquibase est préférable en production pour plusieurs raisons :*
> 
> *1. **Contrôle** : je sais exactement quelles modifications sont appliquées à la base de données.*
> 
> *2. **Versioning** : chaque changement est tracé et peut être rejoué sur n'importe quel environnement.*
> 
> *3. **Rollback** : je peux annuler des changements si nécessaire.*
> 
> *4. **Collaboration** : plusieurs développeurs peuvent travailler sur la BDD sans conflit.*
> 
> *5. **Production-ready** : Hibernate auto-create est dangereux en production (risque de perte de données)."*

### ❓ Questions sur la logique métier

**Q4 : Pourquoi le stock diminue à la création de la commande et pas à la livraison ?**

**R :**
> *"C'est une décision métier importante :*
> 
> *1. **Réservation** : quand une commande est créée, on réserve le stock pour éviter de vendre les mêmes articles deux fois.*
> 
> *2. **Disponibilité réelle** : le stock affiché est toujours le stock réellement disponible pour de nouvelles commandes.*
> 
> *3. **Traçabilité** : le mouvement de stock SORTIE est créé à la livraison pour tracer le moment exact où la marchandise est partie.*
> 
> *4. **Gestion des annulations** : si une commande est annulée, on restaure simplement le stock."*

**Q5 : Comment gérez-vous les problèmes de concurrence sur le stock ?**

**R :**
> *"Pour éviter les problèmes de concurrence (overselling) :*
> 
> *1. **Transactions** : toutes les opérations sur le stock se font dans des transactions (`@Transactional`).*
> 
> *2. **Vérification avant diminution** : avant de diminuer le stock, je vérifie qu'il y a assez de quantité disponible.*
> 
> *3. **Isolation** : Spring Data JPA gère l'isolation des transactions pour éviter les lectures sales.*
> 
> *Une amélioration possible serait d'utiliser le verrouillage optimiste avec `@Version` pour détecter les modifications concurrentes."*

**Q6 : Pourquoi avoir choisi CUMP plutôt que FIFO ?**

**R :**
> *"Le CUMP (Coût Unitaire Moyen Pondéré) a été choisi pour sa simplicité :*
> 
> *1. **Calcul simple** : une seule valeur moyenne par produit, facile à calculer et à comprendre.*
> 
> *2. **Pas besoin de tracer les lots** : contrairement au FIFO, pas besoin de conserver l'historique des entrées.*
> 
> *3. **Lissage des variations** : les fluctuations de prix sont lissées dans la moyenne.*
> 
> *4. **Moins de stockage** : un seul champ `cout_unitaire_moyen` suffit.*
> 
> *Le cahier des charges mentionnait CUMP par défaut, mais le système pourrait être étendu pour supporter FIFO."*

### ❓ Questions sur les choix techniques

**Q7 : Pourquoi PostgreSQL plutôt que MySQL ?**

**R :**
> *"PostgreSQL offre plusieurs avantages :*
> 
> *1. **Conformité SQL** : meilleur respect des standards SQL.*
> 
> *2. **Types avancés** : support natif de JSON, arrays, etc.*
> 
> *3. **Performance** : excellentes performances sur les requêtes complexes.*
> 
> *4. **Open-source** : complètement gratuit et open-source, contrairement à MySQL (Oracle).*
> 
> *5. **Fiabilité** : très stable en production."*

**Q8 : Comment testeriez-vous cette application ?**

**R :**
> *"Pour tester cette application, je mettrais en place plusieurs niveaux :*
> 
> *1. **Tests unitaires** : tester la logique métier (calcul CUMP, gestion statuts) avec JUnit et Mockito.*
> 
> *2. **Tests d'intégration** : tester les repositories avec `@DataJpaTest`.*
> 
> *3. **Tests API** : tester les endpoints avec `@SpringBootTest` et MockMvc.*
> 
> *4. **Tests de validation** : vérifier que les contraintes de validation fonctionnent.*
> 
> *5. **Postman** : pour les tests manuels et les scénarios complexes.*
> 
> *Le projet contient déjà une structure de tests dans `src/test/java`."*

**Q9 : Comment sécuriseriez-vous cette API ?**

**R :**
> *"Pour sécuriser l'API, je mettrais en place :*
> 
> *1. **Spring Security** : authentification JWT ou OAuth2.*
> 
> *2. **Rôles et permissions** : ADMIN peut tout faire, GESTIONNAIRE peut créer/modifier, LECTEUR peut uniquement consulter.*
> 
> *3. **HTTPS** : chiffrement des communications.*
> 
> *4. **Validation stricte** : empêcher les injections SQL (déjà fait avec JPA).*
> 
> *5. **Rate limiting** : limiter le nombre de requêtes par IP.*
> 
> *6. **CORS** : configurer les origines autorisées.*
> 
> *Actuellement, l'API est ouverte pour faciliter le développement et les tests."*

**Q10 : Comment déploieriez-vous cette application en production ?**

**R :**
> *"Pour un déploiement en production :*
> 
> *1. **Conteneurisation** : créer une image Docker de l'application.*
> 
> *2. **Docker Compose** : orchestrer l'application + PostgreSQL.*
> 
> *3. **Variables d'environnement** : externaliser la configuration (pas de mots de passe en dur).*
> 
> *4. **Logs centralisés** : utiliser ELK (Elasticsearch, Logstash, Kibana) ou Splunk.*
> 
> *5. **Monitoring** : Prometheus + Grafana pour surveiller les performances.*
> 
> *6. **CI/CD** : pipeline avec GitHub Actions ou GitLab CI pour automatiser les tests et déploiements.*
> 
> *7. **Cloud** : déployer sur AWS, Azure ou Google Cloud avec auto-scaling."*

### ❓ Questions sur les limites et améliorations

**Q11 : Quelles sont les limites actuelles du projet ?**

**R :**
> *"Voici les principales limites :*
> 
> *1. **Pas d'authentification** : l'API est ouverte, pas de gestion des utilisateurs.*
> 
> *2. **Pas de tests automatisés** : la couverture de tests pourrait être améliorée.*
> 
> *3. **Gestion de stock simplifiée** : pas de gestion multi-entrepôts.*
> 
> *4. **Pas de notifications** : aucune alerte en cas de stock bas.*
> 
> *5. **Concurrence** : pas de verrouillage optimiste sur les entités.*
> 
> *6. **Exports** : pas d'export PDF/Excel des commandes."*

**Q12 : Quelles améliorations proposeriez-vous ?**

**R :**
> *"Voici les améliorations prioritaires :*
> 
> *1. **Seuil d'alerte stock** : ajouter un champ `seuilAlerte` et envoyer des notifications.*
> 
> *2. **Multi-entrepôts** : gérer plusieurs emplacements de stockage.*
> 
> *3. **Historique des prix** : tracer l'évolution des prix des produits.*
> 
> *4. **Statistiques** : dashboard avec chiffres clés (CA, stock moyen, etc.).*
> 
> *5. **Export PDF** : générer des bons de commande PDF.*
> 
> *6. **API de recherche avancée** : filtres multiples (date, statut, fournisseur).*
> 
> *7. **Websockets** : notifications en temps réel des changements.*
> 
> *8. **Caching** : utiliser Redis pour améliorer les performances."*

---

## 6. ARCHITECTURE ET CHOIX TECHNIQUES

### 🏛️ Pattern Repository

**Expliquer pourquoi :**

```java
public interface FournisseurRepository extends JpaRepository<Fournisseur, Long> {
    Optional<Fournisseur> findByIce(String ice);
    boolean existsByIce(String ice);
}
```

**Avantages :**
- ✅ Abstraction complète de la persistence
- ✅ Méthodes CRUD automatiques
- ✅ Requêtes dérivées des noms de méthodes
- ✅ Pagination native
- ✅ Facilite les tests (mock facile)

### 🎨 Pattern DTO + Mapper

**Flux de données :**

```
API Request (JSON)
    ↓
FournisseurDTO (validation)
    ↓
FournisseurMapper.toEntity()
    ↓
Fournisseur (entité JPA)
    ↓
Database
    ↓
Fournisseur (entité JPA)
    ↓
FournisseurMapper.toDto()
    ↓
FournisseurDTO
    ↓
API Response (JSON)
```

**Code du mapper :**

```java
@Mapper(componentModel = "spring")
public interface FournisseurMapper {
    FournisseurDTO toDto(Fournisseur entity);
    Fournisseur toEntity(FournisseurDTO dto);
    List<FournisseurDTO> toDtoList(List<Fournisseur> entities);
}
```

**Avantages :**
- ✅ Code généré à la compilation (pas de réflexion)
- ✅ Type-safe
- ✅ Performant
- ✅ Maintenable

### 🔄 Gestion transactionnelle

**Annotation `@Transactional` :**

```java
@Service
@Transactional
public class CommandeFournisseurService {
    
    @Transactional
    public CommandeFournisseurDTO createCommande(CommandeFournisseurDTO dto) {
        // Toutes les opérations dans cette méthode sont atomiques
        // Si une exception est levée, tout est annulé (rollback)
    }
}
```

**Principes ACID garantis :**
- **Atomicité** : tout ou rien
- **Cohérence** : les contraintes d'intégrité sont respectées
- **Isolation** : les transactions ne s'interfèrent pas
- **Durabilité** : les changements sont permanents

### 📋 Validation déclarative

**Annotations Jakarta Validation :**

```java
@Data
public class FournisseurDTO {
    
    @NotBlank(message = "La société est obligatoire")
    @Size(max = 255, message = "Max 255 caractères")
    private String societe;
    
    @Email(message = "Format email invalide")
    private String email;
    
    @Size(min = 15, max = 15, message = "ICE doit faire 15 caractères")
    private String ice;
    
    @Pattern(regexp = "^0[5-7][0-9]{8}$", message = "Numéro de téléphone invalide")
    private String telephone;
}
```

**Avantages :**
- ✅ Validation automatique avant traitement
- ✅ Messages d'erreur clairs
- ✅ Code métier non pollué
- ✅ Réutilisable

### 🗄️ Migrations Liquibase

**Structure des fichiers :**

```yaml
# db.changelog-master.yaml
databaseChangeLog:
  - include:
      file: db/changelog/migrations/001-create-fournisseurs-table.yaml
  - include:
      file: db/changelog/migrations/002-create-fournisseurs-indexes.yaml
  # ...
```

**Exemple de migration :**

```yaml
# 001-create-fournisseurs-table.yaml
databaseChangeLog:
  - changeSet:
      id: 001-create-fournisseurs-table
      author: fatima-ezzahra-alouane
      changes:
        - createTable:
            tableName: fournisseurs
            columns:
              - column:
                  name: id
                  type: BIGINT
                  autoIncrement: true
                  constraints:
                    primaryKey: true
              - column:
                  name: societe
                  type: VARCHAR(255)
                  constraints:
                    nullable: false
              # ...
```

**Avantages :**
- ✅ Versioning de la base de données
- ✅ Reproductibilité sur tous les environnements
- ✅ Rollback possible
- ✅ Collaboration facilitée
- ✅ Traçabilité complète

---

## 7. PIÈGES À ÉVITER

### ❌ Erreurs classiques à ne PAS faire

#### 1. Confondre les termes métier

**❌ MAL :**
> "La commande fournisseur, c'est quand Tricol commande chez un fournisseur."

**✅ BIEN :**
> "Une commande fournisseur représente une commande PASSÉE PAR un fournisseur auprès de Tricol. Le fournisseur est en fait un client de Tricol qui achète des vêtements professionnels."

#### 2. Ne pas expliquer POURQUOI le stock diminue à la création

**❌ MAL :**
> "Le stock diminue quand on crée la commande, c'est comme ça."

**✅ BIEN :**
> "Le stock diminue dès la création de la commande pour **réserver** les articles. Cela évite de vendre deux fois le même stock. Le mouvement de stock SORTIE est créé plus tard, à la livraison, pour tracer exactement quand la marchandise est partie."

#### 3. Confondre DTO et entité

**❌ MAL :**
> "Les DTOs c'est pareil que les entités, c'est juste un doublon."

**✅ BIEN :**
> "Les DTOs sont des objets de transfert qui découplent l'API du modèle de données. Ils permettent de contrôler ce qui est exposé, de valider différemment selon l'opération, et d'éviter les problèmes de lazy loading."

#### 4. Ne pas connaître la différence CUMP/FIFO

**❌ MAL :**
> "CUMP et FIFO c'est la même chose."

**✅ BIEN :**
> "**CUMP** calcule une moyenne pondérée de tous les achats, ce qui donne un coût moyen simple à gérer. **FIFO** (First In, First Out) considère que les premières entrées sont les premières sorties, ce qui nécessite de tracer les lots d'achat."

#### 5. Ne pas savoir expliquer MapStruct

**❌ MAL :**
> "MapStruct c'est pour mapper, c'est tout."

**✅ BIEN :**
> "MapStruct génère automatiquement le code de mapping à la compilation, ce qui est plus performant que la réflexion et plus sûr qu'un mapping manuel grâce aux vérifications du compilateur."

#### 6. Ignorer les règles de modification/suppression

**❌ MAL :**
> "On peut modifier ou supprimer n'importe quelle commande."

**✅ BIEN :**
> "Seules les commandes EN_ATTENTE peuvent être modifiées. On peut supprimer uniquement les commandes EN_ATTENTE ou ANNULEES. Une fois qu'une commande est VALIDEE ou LIVREE, elle est figée pour garantir l'intégrité des données."

#### 7. Ne pas comprendre Liquibase

**❌ MAL :**
> "Liquibase crée juste les tables."

**✅ BIEN :**
> "Liquibase gère le versioning complet de la base de données. Chaque changement est tracé dans un changelog, peut être rejoué sur n'importe quel environnement, et peut être annulé si nécessaire. C'est essentiel pour travailler en équipe et pour les déploiements en production."

### ⚠️ Questions pièges et comment y répondre

**Q : "Pourquoi ne pas tout faire en SQL pur ?"**

**R :**
> "JPA/Hibernate apporte plusieurs avantages : abstraction de la base de données (je peux changer de PostgreSQL à MySQL facilement), mapping objet-relationnel automatique, cache de premier et second niveau, et moins de code boilerplate. Pour des requêtes très complexes, je peux toujours utiliser des requêtes natives avec `@Query`."

**Q : "Cette API est-elle prête pour la production ?"**

**R :**
> "Techniquement, l'architecture est solide et suit les bonnes pratiques. Cependant, pour la production, il faudrait ajouter : l'authentification/autorisation avec Spring Security, une couverture de tests complète, un système de monitoring, de la documentation pour les opérations, et une stratégie de déploiement avec Docker."

**Q : "Combien de temps avez-vous passé sur ce projet ?"**

**R :**
> "J'ai passé environ [durée réelle] sur ce projet, en incluant l'analyse du cahier des charges, la conception de l'architecture, le développement, les tests manuels avec Postman, et la documentation. Le point le plus chronophage a été la logique de gestion des mouvements de stock avec le calcul du CUMP."

---

## 8. CHECKLIST FINALE

### ✅ Avant de commencer la présentation

- [ ] L'application Spring Boot est démarrée et accessible
- [ ] PostgreSQL est démarré
- [ ] Swagger UI s'affiche correctement : http://localhost:8080/swagger-ui.html
- [ ] Des données de test sont présentes (au moins 2 fournisseurs, 3 produits, 2 commandes)
- [ ] La collection Postman fonctionne
- [ ] VS Code est ouvert avec les fichiers clés
- [ ] Le README.md est à jour
- [ ] Vous avez testé le scénario de démonstration complet

### ✅ Documents à avoir sous la main

- [ ] Cahier des charges (version PDF si disponible)
- [ ] README.md (votre documentation)
- [ ] LOGIQUE_MOUVEMENTS_STOCK.md
- [ ] API_ENUMS.md
- [ ] Diagramme de classes (si vous en avez un)
- [ ] Ce guide de débriefing

### ✅ Connaissances à maîtriser

- [ ] Expliquer le contexte métier de Tricol
- [ ] Différencier fournisseur (client de Tricol) et commande
- [ ] Expliquer le workflow complet d'une commande (EN_ATTENTE → VALIDEE → LIVREE)
- [ ] Détailler la logique de gestion du stock (réservation, mouvements)
- [ ] Calculer un CUMP avec un exemple chiffré
- [ ] Expliquer l'architecture en couches
- [ ] Justifier le choix de chaque technologie
- [ ] Citer 3 avantages de Spring Boot
- [ ] Citer 3 avantages de MapStruct
- [ ] Expliquer le rôle de Liquibase
- [ ] Décrire la différence entre DTO et entité
- [ ] Lister les règles de modification/suppression des commandes

### ✅ Démonstration technique

- [ ] Créer un fournisseur via Swagger
- [ ] Créer un produit (observer le mouvement ENTREE généré)
- [ ] Créer une commande (observer la diminution du stock)
- [ ] Changer le statut vers LIVREE (observer le mouvement SORTIE)
- [ ] Annuler une commande (observer la restauration du stock)
- [ ] Montrer la pagination sur une liste
- [ ] Consulter les mouvements d'un produit
- [ ] Tenter de modifier une commande VALIDEE (montrer l'erreur)
- [ ] Tenter de supprimer une commande LIVREE (montrer l'erreur)

### ✅ Code à montrer

- [ ] Un Controller avec annotations REST
- [ ] Un Service avec logique métier
- [ ] Une entité JPA avec annotations
- [ ] Un DTO avec validations
- [ ] Un Mapper MapStruct
- [ ] Un Repository Spring Data JPA
- [ ] Le fichier application.properties
- [ ] Un fichier de migration Liquibase

### ✅ Attitude pendant la présentation

- [ ] Parler clairement et à un rythme modéré
- [ ] Regarder la formatrice (pas seulement l'écran)
- [ ] Expliquer POURQUOI et pas seulement COMMENT
- [ ] Utiliser un vocabulaire technique précis
- [ ] Rester calme si une erreur survient
- [ ] Admettre les limites du projet sans se dévaloriser
- [ ] Proposer des améliorations futures
- [ ] Montrer de l'enthousiasme pour le projet

---

## 🎯 PLAN DE PRÉSENTATION SUGGÉRÉ (20-30 minutes)

### 1. Introduction (2 minutes)
- Présentation personnelle
- Contexte de Tricol
- Objectif du projet

### 2. Technologies utilisées (3 minutes)
- Stack technique
- Justification des choix

### 3. Architecture (3 minutes)
- Schéma en couches
- Pattern Repository, DTO, Mapper

### 4. Démonstration live (15 minutes)
- Swagger UI
- Scénario complet (fournisseur → produit → commande → livraison)
- Mouvements de stock
- Calcul CUMP
- Règles métier

### 5. Code (5 minutes)
- Controller
- Service (logique métier)
- Mapper
- Liquibase

### 6. Questions/réponses (temps restant)

---

## 🌟 DERNIERS CONSEILS

### ✨ Ce qui fera la différence

1. **Montrez que vous comprenez le métier**, pas seulement la technique
2. **Expliquez vos choix** : chaque décision doit être justifiée
3. **Soyez transparent** sur les limites et proposez des améliorations
4. **Démontrez la maîtrise technique** : utilisez le vocabulaire précis
5. **Restez professionnel** : calme, posé, confiant

### 💡 Si vous bloquez sur une question

1. **Respirez**, prenez 2 secondes pour réfléchir
2. **Reformulez** la question pour vérifier que vous avez compris
3. **Soyez honnête** : "Je ne suis pas sûr(e), mais je pense que..."
4. **Proposez de chercher** : "Je n'ai pas la réponse en tête, mais je sais où trouver l'information"
5. **Rebondissez** : "Ce que je peux dire, c'est que..."

### 🔥 Phrases clés à placer

- "J'ai suivi une architecture en couches pour séparer les responsabilités"
- "J'ai utilisé des DTOs pour découpler l'API du modèle de données"
- "MapStruct génère le code à la compilation, ce qui est plus performant"
- "Liquibase permet de versionner la base de données comme du code"
- "Le CUMP lisse les variations de prix et simplifie la gestion"
- "Spring Data JPA abstrait la couche de persistence"
- "La pagination native améliore les performances"
- "Les transactions ACID garantissent l'intégrité des données"

---

## 📞 EN CAS DE PROBLÈME TECHNIQUE PENDANT LA DÉMO

### Problème : L'application ne démarre pas

**Solutions :**
1. Vérifier que PostgreSQL est démarré
2. Vérifier les credentials dans `application.properties`
3. Regarder les logs pour identifier l'erreur
4. Avoir une vidéo de backup de la démo

### Problème : Swagger ne s'affiche pas

**Solutions :**
1. Vérifier l'URL : http://localhost:8080/swagger-ui.html
2. Vérifier dans `pom.xml` que springdoc-openapi est présent
3. Redémarrer l'application
4. Utiliser Postman en backup

### Problème : Erreur lors de la création d'une commande

**Solutions :**
1. Vérifier que le fournisseur existe
2. Vérifier que les produits existent
3. Vérifier qu'il y a assez de stock
4. Regarder le message d'erreur et l'expliquer

### Problème : Les mouvements ne se créent pas

**Solutions :**
1. Vérifier les logs de l'application
2. Vérifier que la transaction n'a pas été rollback
3. Interroger la base directement : `SELECT * FROM mouvements_stock;`
4. Expliquer ce qui devrait se passer même si ça ne fonctionne pas

---

## ✅ VALIDATION FINALE

**Vous êtes prêt(e) si vous pouvez répondre OUI à ces questions :**

- [ ] Je peux expliquer le métier de Tricol et le rôle de chaque entité
- [ ] Je peux faire une démo complète sans notes
- [ ] Je peux calculer un CUMP de tête
- [ ] Je connais toutes les technologies du `pom.xml`
- [ ] Je peux expliquer chaque annotation dans le code
- [ ] Je sais quand le stock diminue et quand les mouvements sont créés
- [ ] Je connais les règles de modification/suppression des commandes
- [ ] Je peux citer 5 améliorations possibles du projet
- [ ] Je sais répondre à "Pourquoi Spring Boot ?"
- [ ] Je sais répondre à "Pourquoi MapStruct ?"
- [ ] Je sais répondre à "Pourquoi Liquibase ?"

---

## 🎓 MESSAGE DE FIN

**Vous avez créé un projet solide, professionnel et complet.**

Les points forts de votre projet :
- ✅ Architecture propre et maintenable
- ✅ Technologies modernes et pertinentes
- ✅ Logique métier complexe bien implémentée
- ✅ Documentation complète
- ✅ Respect du cahier des charges

**Soyez fier(e) de votre travail et montrez-le avec confiance !**

---

**Bon courage pour votre évaluation ! 🚀**

---

*Ce guide a été préparé pour maximiser vos chances de réussite. Relisez-le la veille et le matin de l'évaluation.*

*Si vous avez des questions ou des doutes, n'hésitez pas à les noter et à chercher les réponses avant le jour J.*

**Vous êtes prêt(e). Vous allez réussir ! 💪**
