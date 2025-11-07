# TricolSupplyPro-Boot

## Description du projet

Application de gestion des approvisionnements pour l'entreprise Tricol, spécialisée dans la conception et la fabrication de vêtements professionnels.

Cette API REST développée avec Spring Boot permet de gérer l'ensemble du cycle de vie des commandes fournisseurs, depuis leur création jusqu'à leur suivi, avec une gestion automatique des mouvements de stock et de la valorisation des coûts.

## Technologies utilisées

- **Spring Boot 3.5.7**
- **Spring Data JPA**
- **MapStruct 1.5.5** - Mapping entre Entités et DTOs
- **Lombok** - Réduction du code boilerplate
- **Liquibase** - Gestion des migrations de base de données
- **Swagger/OpenAPI 2.3.0** - Documentation automatique de l'API
- **Jakarta Validation** - Validation des champs
- **PostgreSQL** - Base de données

## Architecture

L'application suit une architecture en couches :

```
Controller → Service → Repository → Database
     ↓         ↓
   DTO ← MapStruct
```

### Packages

- `entity` : Entités JPA (Fournisseur, Produit, CommandeFournisseur, MouvementStock)
- `dto` : Data Transfer Objects
- `mapper` : Interfaces MapStruct pour le mapping
- `repository` : Interfaces Spring Data JPA
- `service` : Logique métier
- `controller` : Contrôleurs REST
- `exception` : Gestion globale des exceptions
- `enumeration` : Énumérations (StatutCommande, TypeMouvement)

## Fonctionnalités principales

### 1. Gestion des Fournisseurs

- ✅ Création, modification, suppression et consultation
- ✅ Pagination et tri

**Informations gérées :** société, adresse, contact, email, téléphone, ville, ICE

### 2. Gestion des Produits

- ✅ Création, modification, suppression et consultation
- ✅ Pagination et tri
- ✅ Suivi du stock actuel
- ✅ Gestion du coût unitaire moyen

**Informations gérées :** nom, description, prix unitaire, catégorie, stock

### 3. Gestion des Commandes Fournisseurs

- ✅ Création de commandes avec calcul automatique du montant total
- ✅ Modification (seulement EN_ATTENTE)
- ✅ Suppression (seulement EN_ATTENTE ou ANNULEE)
- ✅ Consultation des détails
- ✅ Changement de statut
- ✅ Pagination et tri

**Statuts :** EN_ATTENTE, VALIDEE, LIVREE, ANNULEE

### 4. Gestion des Mouvements de Stock

- ✅ Création automatique d'entrées lors de la livraison d'une commande
- ✅ Mise à jour automatique des quantités en stock
- ✅ Consultation de l'historique par produit ou par commande

**Types :** ENTREE, SORTIE, AJUSTEMENT

### 5. Valorisation du Stock

Deux méthodes de valorisation configurable :

- **CUMP (Coût Unitaire Moyen Pondéré)** - Par défaut
- **FIFO (First In, First Out)**

La méthode est paramétrable via `application.properties` :
```properties
app.valorisation-stock=CUMP  # ou FIFO
```

## Base de données

### Schéma principal

- `fournisseur` : Informations sur les fournisseurs
- `produit` : Catalogue des produits avec stock et coûts
- `commande_fournisseur` : Commandes fournisseurs
- `commande_produit` : Détail des produits par commande (quantité, prix)
- `mouvement_stock` : Historique des mouvements de stock

### Migration Liquibase

Les scripts de migration sont dans `src/main/resources/db/changelog/` :
- `db.changelog-master.yaml` - Fichier principal
- `db.changelog-v1.0.yaml` - Création des tables et enums

## API REST

### Documentation Swagger

Une fois l'application démarrée, la documentation Swagger est disponible à :
- **Swagger UI** : http://localhost:8080/swagger-ui.html
- **API Docs** : http://localhost:8080/api-docs

### Endpoints principaux

#### Fournisseurs
```
GET    /api/v1/fournisseurs           # Liste paginée
GET    /api/v1/fournisseurs/{id}      # Détails
POST   /api/v1/fournisseurs           # Créer
PUT    /api/v1/fournisseurs/{id}      # Modifier
DELETE /api/v1/fournisseurs/{id}      # Supprimer
```

#### Produits
```
GET    /api/v1/produits               # Liste paginée
GET    /api/v1/produits/{id}          # Détails
POST   /api/v1/produits               # Créer
PUT    /api/v1/produits/{id}          # Modifier
DELETE /api/v1/produits/{id}          # Supprimer
```

#### Commandes Fournisseurs
```
GET    /api/v1/commandes                            # Liste paginée
GET    /api/v1/commandes/{id}                       # Détails
GET    /api/v1/commandes/fournisseur/{id}           # Par fournisseur
POST   /api/v1/commandes                            # Créer
PUT    /api/v1/commandes/{id}                       # Modifier
DELETE /api/v1/commandes/{id}                       # Supprimer
PATCH  /api/v1/commandes/{id}/statut?statut=XXX     # Changer statut
```

#### Mouvements de Stock
```
GET    /api/v1/mouvements/produit/{produitId}    # Par produit
GET    /api/v1/mouvements/commande/{commandeId}  # Par commande
```

### Pagination et tri

Tous les endpoints de liste acceptent les paramètres standards de Spring Data :
```
?page=0&size=10&sort=nom,asc
```

## Configuration

### application.properties

```properties
# Base de données PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5433/tricolsupplyboot
spring.datasource.username=postgres
spring.datasource.password=admin

# Liquibase
spring.liquibase.enabled=true
spring.liquibase.change-log=classpath:db/changelog/db.changelog-master.yaml

# Swagger/OpenAPI
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html

# Valorisation du stock
app.valorisation-stock=CUMP
```

## Lancement de l'application

### Prérequis
- Java 17+
- PostgreSQL 12+
- Maven 3.8+

### Installation

1. Cloner le projet
2. Créer la base de données PostgreSQL :
```sql
CREATE DATABASE tricolsupplyboot;
```

3. Configurer les paramètres de connexion dans `application.properties`

4. Compiler et lancer :
```bash
mvn clean install
mvn spring-boot:run
```

L'application sera accessible sur http://localhost:8080

## Validation et gestion d'erreurs

- Validation Jakarta pour tous les DTOs
- Gestion globale des exceptions avec messages JSON structurés
- Codes HTTP appropriés (200, 201, 400, 404, 500)

## Règles métier

### Commandes
- Seules les commandes EN_ATTENTE peuvent être modifiées
- Seules les commandes EN_ATTENTE ou ANNULEES peuvent être supprimées
- Lors du passage en LIVREE, des mouvements de stock ENTREE sont créés automatiquement

### Stock
- Le stock est mis à jour automatiquement lors des entrées
- Le coût unitaire moyen est recalculé selon la méthode configurée
- Consultation de l'historique complet des mouvements

## Tests

Les tests unitaires et d'intégration sont à implémenter dans `src/test/java`.

## Auteur

Développé pour Tricol SupplyPro - Gestion des Approvisionnements

## Licence

Projet interne - Tricol

