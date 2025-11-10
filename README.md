<div align="center">

# 🏭 TricolSupplyPro-Boot

### 📦 Système de Gestion des Approvisionnements

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.7-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-12+-blue.svg)](https://www.postgresql.org/)
[![License](https://img.shields.io/badge/License-Internal-red.svg)]()

*API REST moderne pour la gestion complète des commandes fournisseurs et du stock*

[Fonctionnalités](#-fonctionnalités) • [Installation](#-installation) • [API Documentation](#-api-rest) • [Technologies](#-stack-technique)

</div>

---

## 📋 Table des matières

- [À propos](#-à-propos)
- [Fonctionnalités](#-fonctionnalités)
- [Stack Technique](#-stack-technique)
- [Architecture](#-architecture)
- [Installation](#-installation)
- [API REST](#-api-rest)
- [Base de données](#-base-de-données)
- [Configuration](#-configuration)
- [Règles métier](#-règles-métier)

---

## 🎯 À propos

**TricolSupplyPro-Boot** est une application de gestion des approvisionnements développée pour **Tricol**, entreprise spécialisée dans la conception et la fabrication de vêtements professionnels.

### 🏢 Contexte

Après avoir mis en place le module de gestion des fournisseurs, Tricol souhaite digitaliser la gestion de ses commandes fournisseurs pour assurer un suivi rigoureux des approvisionnements en matières premières et équipements.

### 🎯 Objectif

Développer une **API REST complète** permettant de gérer l'ensemble du cycle de vie des commandes fournisseurs, depuis leur création jusqu'à leur suivi, avec une **gestion automatique des mouvements de stock** et de la **valorisation des coûts** (méthode CUMP).

---

## ✨ Fonctionnalités

### 👥 Gestion des Fournisseurs
- ✅ CRUD complet (Créer, Lire, Modifier, Supprimer)
- ✅ Pagination et tri dynamique
- ✅ Validation des données (email, ICE unique)
- 📊 **Informations gérées** : société, adresse, contact, email, téléphone, ville, ICE

### 📦 Gestion des Produits
- ✅ CRUD complet avec validation
- ✅ Suivi du stock en temps réel
- ✅ Calcul automatique du coût unitaire moyen (CUMP)
- ✅ Pagination et filtrage avancé
- 📊 **Informations gérées** : nom, description, prix unitaire, catégorie, stock actuel

### 🛒 Gestion des Commandes Fournisseurs
- ✅ Création de commandes multi-produits
- ✅ Calcul automatique du montant total
- ✅ Modification (uniquement si `EN_ATTENTE`)
- ✅ Suppression sécurisée (uniquement si `EN_ATTENTE` ou `ANNULEE`)
- ✅ Gestion des statuts : `EN_ATTENTE` → `VALIDEE` → `LIVREE` / `ANNULEE`
- ✅ Consultation par fournisseur
- ✅ Pagination et tri personnalisable

### 📊 Gestion des Mouvements de Stock
- ✅ Création automatique lors de la livraison d'une commande
- ✅ Mise à jour automatique des quantités en stock
- ✅ Types de mouvements : `ENTREE`, `SORTIE`, `AJUSTEMENT`
- ✅ Historique complet par produit ou par commande
- ✅ Traçabilité totale avec horodatage

### 💰 Valorisation du Stock (CUMP)
- ✅ Calcul automatique du **Coût Unitaire Moyen Pondéré**
- ✅ Formule : `CUMP = (Valeur stock ancien + Valeur entrée) / Stock total`
- ✅ Mise à jour en temps réel à chaque entrée de stock
- ✅ Précision décimale (2 chiffres après la virgule)

---

## 🛠 Stack Technique

### 🔧 Backend & Framework
| Technologie | Version | Description |
|------------|---------|-------------|
| ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.7-6DB33F?logo=spring-boot&logoColor=white) | 3.5.7 | Framework principal |
| ![Spring Data JPA](https://img.shields.io/badge/Spring%20Data%20JPA-3.5.7-6DB33F?logo=spring&logoColor=white) | 3.5.7 | Accès aux données |
| ![Java](https://img.shields.io/badge/Java-17-ED8B00?logo=openjdk&logoColor=white) | 17 | Langage de programmation |

### 🗄️ Base de données
| Technologie | Version | Description |
|------------|---------|-------------|
| ![PostgreSQL](https://img.shields.io/badge/PostgreSQL-12+-4169E1?logo=postgresql&logoColor=white) | 12+ | Base de données relationnelle |
| ![Liquibase](https://img.shields.io/badge/Liquibase-Latest-2962FF?logo=liquibase&logoColor=white) | Latest | Gestion des migrations |

### 📝 Mapping & Validation
| Technologie | Version | Description |
|------------|---------|-------------|
| ![MapStruct](https://img.shields.io/badge/MapStruct-1.5.5-FF6C37?logo=java&logoColor=white) | 1.5.5 | Mapping Entity ↔ DTO |
| ![Lombok](https://img.shields.io/badge/Lombok-Latest-BC4521?logo=lombok&logoColor=white) | Latest | Réduction du code boilerplate |
| ![Jakarta Validation](https://img.shields.io/badge/Jakarta%20Validation-3.0-007396?logo=java&logoColor=white) | 3.0 | Validation des données |

### 📚 Documentation
| Technologie | Version | Description |
|------------|---------|-------------|
| ![Swagger](https://img.shields.io/badge/Swagger-2.3.0-85EA2D?logo=swagger&logoColor=black) | 2.3.0 | Documentation API interactive |
| ![OpenAPI](https://img.shields.io/badge/OpenAPI-3.0-6BA539?logo=openapiinitiative&logoColor=white) | 3.0 | Spécification API |

---

## 🏗 Architecture

### 📐 Architecture en couches

```
┌─────────────────────────────────────────────────┐
│              🌐 REST Controllers                │
│         (Endpoints API - Validation)            │
└────────────────┬────────────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────────────┐
│              📋 DTOs (Data Transfer)            │
│         (FournisseurDTO, ProduitDTO...)         │
└────────────────┬────────────────────────────────┘
                 │
                 ▼ MapStruct
┌─────────────────────────────────────────────────┐
│              🔄 Mappers                         │
│    (Conversion automatique Entity ↔ DTO)        │
└────────────────┬────────────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────────────┐
│              💼 Services                        │
│         (Logique métier - CUMP)                 │
└────────────────┬────────────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────────────┐
│              🗃️ Repositories                    │
│         (Spring Data JPA)                       │
└────────────────┬────────────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────────────┐
│              🐘 PostgreSQL Database             │
│         (Tables + Liquibase)                    │
└─────────────────────────────────────────────────┘
```

### 📦 Structure des packages

```
com.tricol.supply
├── 📂 controller/          # Contrôleurs REST
├── 📂 service/             # Logique métier
├── 📂 repository/          # Accès aux données
├── 📂 model/
│   ├── 📂 entity/          # Entités JPA
│   └── 📂 enums/           # Énumérations
├── 📂 dto/                 # Data Transfer Objects
├── 📂 mapper/              # Mappers MapStruct
└── 📂 exception/           # Gestion des exceptions
```

---

## 🚀 Installation

### 📋 Prérequis

Avant de commencer, assurez-vous d'avoir installé :

- ☕ **Java 17** ou supérieur
- 🐘 **PostgreSQL 12** ou supérieur
- 📦 **Maven 3.8** ou supérieur
- 🔧 **Git** (pour cloner le projet)

### 📥 Étape 1 : Cloner le projet

```bash
git clone https://github.com/votre-repo/TricolSupplyPro-Boot.git
cd TricolSupplyPro-Boot
```

### 🗄️ Étape 2 : Créer la base de données

Connectez-vous à PostgreSQL et exécutez :

```sql
CREATE DATABASE tricolsupplyboot;
```

### ⚙️ Étape 3 : Configuration

Modifiez le fichier `src/main/resources/application.properties` :

```properties
# Configuration PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/tricolsupplyboot
spring.datasource.username=votre_username
spring.datasource.password=votre_password
```

### 🔨 Étape 4 : Compiler et lancer

```bash
# Compiler le projet
mvn clean install

# Lancer l'application
mvn spring-boot:run
```

### ✅ Étape 5 : Vérifier l'installation

L'application sera accessible sur :

- 🌐 **API** : http://localhost:8080
- 📚 **Swagger UI** : http://localhost:8080/swagger-ui.html
- 📄 **API Docs** : http://localhost:8080/api-docs

---

## 🌐 API REST

### 📚 Documentation interactive

Une fois l'application démarrée, accédez à la documentation Swagger :

🔗 **http://localhost:8080/swagger-ui.html**

### 🔌 Endpoints disponibles

#### 👥 Fournisseurs

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `GET` | `/api/v1/fournisseurs` | 📋 Liste paginée des fournisseurs |
| `GET` | `/api/v1/fournisseurs/{id}` | 🔍 Détails d'un fournisseur |
| `POST` | `/api/v1/fournisseurs` | ➕ Créer un fournisseur |
| `PUT` | `/api/v1/fournisseurs/{id}` | ✏️ Modifier un fournisseur |
| `DELETE` | `/api/v1/fournisseurs/{id}` | 🗑️ Supprimer un fournisseur |

#### 📦 Produits

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `GET` | `/api/v1/produits` | 📋 Liste paginée des produits |
| `GET` | `/api/v1/produits/{id}` | 🔍 Détails d'un produit |
| `POST` | `/api/v1/produits` | ➕ Créer un produit |
| `PUT` | `/api/v1/produits/{id}` | ✏️ Modifier un produit |
| `DELETE` | `/api/v1/produits/{id}` | 🗑️ Supprimer un produit |

#### 🛒 Commandes Fournisseurs

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `GET` | `/api/v1/commandes` | 📋 Liste paginée des commandes |
| `GET` | `/api/v1/commandes/{id}` | 🔍 Détails d'une commande |
| `GET` | `/api/v1/commandes/fournisseur/{id}` | 🏢 Commandes par fournisseur |
| `POST` | `/api/v1/commandes` | ➕ Créer une commande |
| `PUT` | `/api/v1/commandes/{id}` | ✏️ Modifier une commande |
| `DELETE` | `/api/v1/commandes/{id}` | 🗑️ Supprimer une commande |
| `PATCH` | `/api/v1/commandes/{id}/statut` | 🔄 Changer le statut |

#### 📊 Mouvements de Stock

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `GET` | `/api/v1/mouvements/produit/{produitId}` | 📦 Mouvements par produit |
| `GET` | `/api/v1/mouvements/commande/{commandeId}` | 🛒 Mouvements par commande |

### 📄 Exemples de requêtes

#### Créer un fournisseur

```http
POST /api/v1/fournisseurs
Content-Type: application/json

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

#### Créer une commande

```http
POST /api/v1/commandes
Content-Type: application/json

{
  "fournisseurId": 1,
  "produits": [
    {
      "produitId": 1,
      "quantite": 100,
      "prixUnitaireCommande": 25.50
    },
    {
      "produitId": 2,
      "quantite": 50,
      "prixUnitaireCommande": 15.00
    }
  ]
}
```

#### Changer le statut d'une commande

```http
PATCH /api/v1/commandes/1/statut?statut=LIVREE
```

### 🔍 Pagination et tri

Tous les endpoints de liste supportent la pagination :

```http
GET /api/v1/fournisseurs?page=0&size=10&sort=societe,asc
```

**Paramètres** :
- `page` : Numéro de la page (commence à 0)
- `size` : Nombre d'éléments par page (défaut : 10)
- `sort` : Champ de tri + direction (`asc` ou `desc`)

**Réponse** :
```json
{
  "content": [...],
  "totalElements": 50,
  "totalPages": 5,
  "size": 10,
  "number": 0
}
```

---

## 🗄️ Base de données

### 📊 Modèle de données

```mermaid
erDiagram
    FOURNISSEUR ||--o{ COMMANDE_FOURNISSEUR : possede
    COMMANDE_FOURNISSEUR ||--o{ COMMANDE_PRODUIT : contient
    PRODUIT ||--o{ COMMANDE_PRODUIT : inclus_dans
    PRODUIT ||--o{ MOUVEMENT_STOCK : a_des_mouvements
    COMMANDE_FOURNISSEUR ||--o{ MOUVEMENT_STOCK : genere
    
    FOURNISSEUR {
        bigint id PK
        varchar societe
        varchar adresse
        varchar contact
        varchar email
        varchar telephone
        varchar ville
        varchar ice UK
        timestamp created_at
        timestamp updated_at
    }
    
    PRODUIT {
        bigint id PK
        varchar nom UK
        text description
        decimal prix_unitaire
        varchar categorie
        int stock_actuel
        decimal cout_unitaire_moyen
        timestamp created_at
        timestamp updated_at
    }
    
    COMMANDE_FOURNISSEUR {
        bigint id PK
        timestamp date_commande
        varchar statut
        decimal montant_total
        bigint fournisseur_id FK
        timestamp created_at
        timestamp updated_at
    }
    
    COMMANDE_PRODUIT {
        bigint commande_id PK,FK
        bigint produit_id PK,FK
        int quantite
        decimal prix_unitaire_commande
    }
    
    MOUVEMENT_STOCK {
        bigint id PK
        timestamp date_mouvement
        varchar type_mouvement
        int quantite
        decimal prix_unitaire
        bigint produit_id FK
        bigint commande_fournisseur_id FK
        timestamp created_at
    }
```

### 🔄 Migrations Liquibase

Les migrations de base de données sont gérées par **Liquibase** :

```
src/main/resources/db/changelog/
├── db.changelog-master.yaml       # Fichier principal
└── migrations/                    # Dossier des migrations
    ├── 003-create-fournisseurs-table.yaml
    ├── 004-create-fournisseurs-indexes.yaml
    ├── 005-create-produits-table.yaml
    ├── 006-create-produits-indexes.yaml
    ├── 007-create-commandes-fournisseur-table.yaml
    ├── 008-create-commandes-fournisseur-indexes.yaml
    ├── 009-create-commandes-produits-table.yaml
    ├── 010-create-mouvements-stock-table.yaml
    └── 011-create-mouvements-stock-indexes.yaml
```

Liquibase s'exécute automatiquement au démarrage de l'application.

---

## ⚙️ Configuration

### 📝 application.properties

```properties
# ========================================
# Configuration PostgreSQL
# ========================================
spring.datasource.url=jdbc:postgresql://localhost:5432/tricolsupplyboot
spring.datasource.username=postgres
spring.datasource.password=admin
spring.datasource.driver-class-name=org.postgresql.Driver

# ========================================
# Configuration JPA/Hibernate
# ========================================
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# ========================================
# Configuration Liquibase
# ========================================
spring.liquibase.enabled=true
spring.liquibase.change-log=classpath:db/changelog/db.changelog-master.yaml

# ========================================
# Configuration Swagger/OpenAPI
# ========================================
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.operationsSorter=method

# ========================================
# Configuration Serveur
# ========================================
server.port=8080

# ========================================
# Configuration Logging
# ========================================
logging.level.com.tricol.supply=DEBUG
logging.level.org.springframework.web=INFO
logging.level.org.hibernate.SQL=DEBUG
```

---

## 📜 Règles métier

### 🛒 Gestion des Commandes

#### ✏️ Modification
- ⚠️ Seules les commandes avec le statut `EN_ATTENTE` peuvent être modifiées
- ✅ Tous les champs peuvent être mis à jour (fournisseur, produits, quantités)

#### 🗑️ Suppression
- ⚠️ Seules les commandes `EN_ATTENTE` ou `ANNULEE` peuvent être supprimées
- ❌ Les commandes `VALIDEE` ou `LIVREE` ne peuvent pas être supprimées

#### 🔄 Changement de statut
- `EN_ATTENTE` → `VALIDEE` → `LIVREE` ✅
- `EN_ATTENTE` → `ANNULEE` ✅
- `VALIDEE` → `ANNULEE` ✅
- `LIVREE` → Statut final (aucun changement possible) 🔒

### 📊 Gestion du Stock

#### 📥 Entrées automatiques
- Lorsqu'une commande passe au statut `LIVREE`, des mouvements de type `ENTREE` sont créés automatiquement
- Le stock de chaque produit est mis à jour : `stock_actuel += quantite`

#### 💰 Calcul du CUMP
Formule appliquée à chaque entrée :

```
CUMP = (Valeur stock ancien + Valeur entrée) / Stock total

Où :
- Valeur stock ancien = stock_ancien × cout_ancien
- Valeur entrée = quantite_entree × prix_unitaire_entree
- Stock total = stock_ancien + quantite_entree
```

**Exemple** :
- Stock actuel : 100 unités à 10€ = 1000€
- Entrée : 50 unités à 12€ = 600€
- Nouveau CUMP = (1000€ + 600€) / 150 = **10,67€**

#### 📜 Traçabilité
- Chaque mouvement est horodaté avec `date_mouvement` et `created_at`
- Référence à la commande source (`commande_fournisseur_id`)
- Historique complet consultable par produit ou par commande

---

## ✅ Validation des données

### 🔒 Contraintes de validation

#### Fournisseur
- ✅ `societe` : obligatoire, max 255 caractères
- ✅ `email` : format email valide
- ✅ `ice` : unique, 15 caractères
- ✅ `telephone` : format valide

#### Produit
- ✅ `nom` : obligatoire, unique, max 255 caractères
- ✅ `prixUnitaire` : obligatoire, > 0
- ✅ `stockActuel` : >= 0

#### Commande
- ✅ `fournisseurId` : obligatoire, doit exister
- ✅ `produits` : liste non vide
- ✅ `quantite` : > 0
- ✅ `prixUnitaireCommande` : > 0

### 🚨 Gestion des erreurs

L'API retourne des réponses JSON structurées :

```json
{
  "timestamp": "2025-11-07T21:23:45",
  "status": 400,
  "error": "Bad Request",
  "message": "Le fournisseur avec l'ID 999 n'existe pas",
  "path": "/api/v1/commandes"
}
```

**Codes HTTP** :
- `200` : Succès
- `201` : Ressource créée
- `400` : Erreur de validation
- `404` : Ressource non trouvée
- `500` : Erreur serveur

### 📮 Collection Postman

Une collection Postman complète est disponible pour tester tous les endpoints de l'API :

📁 **Fichier** : `TricolSupplyPro.postman_collection.json`

**Comment l'utiliser** :
1. Ouvrir Postman
2. Cliquer sur **Import**
3. Sélectionner le fichier `TricolSupplyPro.postman_collection.json`
4. La collection contient tous les endpoints avec des exemples de requêtes

**Endpoints inclus** :
- ✅ **Fournisseurs** : CRUD complet (5 requêtes)
- ✅ **Produits** : CRUD complet (5 requêtes)
- ✅ **Commandes Fournisseurs** : CRUD + changement de statut (7 requêtes)
- ✅ **Mouvements de Stock** : Consultation par produit/commande (2 requêtes)

**Variable d'environnement** :
- `baseUrl` : `http://localhost:8080/api/v1` (modifiable selon votre configuration)

**📋 Valeurs des Enums** :
- Consultez le fichier `API_ENUMS.md` pour la liste complète des valeurs acceptées pour `StatutCommande` et `TypeMouvement`

**📦 Logique des Mouvements de Stock** :
- Consultez le fichier `LOGIQUE_MOUVEMENTS_STOCK.md` pour comprendre le workflow complet des mouvements de stock

---

## 📚 Documentation complémentaire

### 📄 Documentation projet

- 📦 **[LOGIQUE_MOUVEMENTS_STOCK.md](LOGIQUE_MOUVEMENTS_STOCK.md)** - Workflow détaillé des mouvements de stock
- 📋 **[API_ENUMS.md](API_ENUMS.md)** - Liste des valeurs d'énumérations
- 📊 **[Diagram/classDiagram.png](Diagram/classDiagram.png)** - Diagramme de classes UML

### 🔗 Ressources utiles

- 📖 [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- 📖 [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- 📖 [MapStruct Documentation](https://mapstruct.org/)
- 📖 [Liquibase Documentation](https://docs.liquibase.com/)
- 📖 [Swagger/OpenAPI](https://swagger.io/specification/)

---

## 👨‍💻 Auteur

Développée avec ❤️ par **Fatima-Ezzahra Alouane** pour **Tricol SupplyPro**

### 🔗 Connectez-vous avec moi

[![LinkedIn](https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/fatima-ezzahra-alouane)
[![Portfolio](https://img.shields.io/badge/Portfolio-FF5722?style=for-the-badge&logo=google-chrome&logoColor=white)](https://fatima-ezzahra-alouane.vercel.app)

---

<div align="center">

### 🌟 Si ce projet vous a aidé, n'oubliez pas de lui donner une étoile ! ⭐

**[⬆ Retour en haut](#-tricolsupplypro-boot)**

</div>
