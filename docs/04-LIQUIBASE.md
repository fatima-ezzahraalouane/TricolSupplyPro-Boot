# 🗄️ Liquibase - Guide Complet

[← Retour à l'index](README.md) | [← MapStruct](03-MAPSTRUCT.md)

---

## 📖 Table des matières

1. [Qu'est-ce que Liquibase ?](#quest-ce-que-liquibase)
2. [Structure des fichiers](#structure-des-fichiers)
3. [Types de changements](#types-de-changements)
4. [Configuration](#configuration)
5. [Rollback](#rollback)

---

## Qu'est-ce que Liquibase ?

### Définition

**Liquibase** est un outil de gestion des migrations de base de données qui permet de versionner et de suivre les changements du schéma de base de données.

### Pourquoi utiliser Liquibase ?

**❌ Sans Liquibase** :
- Scripts SQL manuels difficiles à suivre
- Pas d'historique des changements
- Synchronisation difficile entre environnements (dev/test/prod)
- Risque d'oublier des migrations
- Rollback complexe et manuel

**✅ Avec Liquibase** :
- Historique complet des migrations
- Versioning du schéma
- Rollback automatique possible
- Synchronisation entre environnements
- Traçabilité complète

### Concept clé : ChangeSet

Un **ChangeSet** est une unité de changement de base de données.

**Caractéristiques** :
- ID unique
- Auteur
- Liste de changements
- Exécuté une seule fois
- Tracé dans la table `DATABASECHANGELOG`

---

## Structure des fichiers

### Arborescence du projet

```
src/main/resources/db/changelog/
├── db.changelog-master.yaml       ← Fichier principal
└── migrations/                    ← Dossier des migrations
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

### Fichier Master

**db.changelog-master.yaml** :

```yaml
databaseChangeLog:
  - include:
      file: db/changelog/migrations/003-create-fournisseurs-table.yaml
  - include:
      file: db/changelog/migrations/004-create-fournisseurs-indexes.yaml
  - include:
      file: db/changelog/migrations/005-create-produits-table.yaml
  - include:
      file: db/changelog/migrations/006-create-produits-indexes.yaml
  - include:
      file: db/changelog/migrations/007-create-commandes-fournisseur-table.yaml
  - include:
      file: db/changelog/migrations/008-create-commandes-fournisseur-indexes.yaml
  - include:
      file: db/changelog/migrations/009-create-commandes-produits-table.yaml
  - include:
      file: db/changelog/migrations/010-create-mouvements-stock-table.yaml
  - include:
      file: db/changelog/migrations/011-create-mouvements-stock-indexes.yaml
```

---

## Types de changements

### 1. Créer une table

**005-create-produits-table.yaml** :

```yaml
databaseChangeLog:
  - changeSet:
      id: create-produits-table
      author: fatima-ezzahra
      changes:
        - createTable:
            tableName: produits
            columns:
              - column:
                  name: id
                  type: BIGSERIAL
                  constraints:
                    primaryKey: true
                    nullable: false
              - column:
                  name: nom
                  type: VARCHAR(100)
                  constraints:
                    nullable: false
                    unique: true
              - column:
                  name: description
                  type: TEXT
              - column:
                  name: prix_unitaire
                  type: DECIMAL(10,2)
                  constraints:
                    nullable: false
              - column:
                  name: categorie
                  type: VARCHAR(50)
              - column:
                  name: stock_actuel
                  type: INTEGER
                  defaultValue: 0
              - column:
                  name: cout_unitaire_moyen
                  type: DECIMAL(10,2)
                  defaultValue: 0.00
              - column:
                  name: created_at
                  type: TIMESTAMP
                  defaultValueComputed: CURRENT_TIMESTAMP
              - column:
                  name: updated_at
                  type: TIMESTAMP
                  defaultValueComputed: CURRENT_TIMESTAMP
```

### 2. Créer des index

**006-create-produits-indexes.yaml** :

```yaml
databaseChangeLog:
  - changeSet:
      id: create-produits-indexes
      author: fatima-ezzahra
      changes:
        - createIndex:
            indexName: idx_produits_nom
            tableName: produits
            columns:
              - column:
                  name: nom
        - createIndex:
            indexName: idx_produits_categorie
            tableName: produits
            columns:
              - column:
                  name: categorie
```

### 3. Ajouter une colonne

```yaml
databaseChangeLog:
  - changeSet:
      id: add-column-email-to-fournisseurs
      author: fatima-ezzahra
      changes:
        - addColumn:
            tableName: fournisseurs
            columns:
              - column:
                  name: email
                  type: VARCHAR(255)
                  constraints:
                    nullable: true
```

### 4. Modifier une colonne

```yaml
databaseChangeLog:
  - changeSet:
      id: modify-column-nom-length
      author: fatima-ezzahra
      changes:
        - modifyDataType:
            tableName: produits
            columnName: nom
            newDataType: VARCHAR(200)
```

### 5. Ajouter une contrainte de clé étrangère

```yaml
databaseChangeLog:
  - changeSet:
      id: add-fk-mouvements-produits
      author: fatima-ezzahra
      changes:
        - addForeignKeyConstraint:
            baseTableName: mouvements_stock
            baseColumnNames: produit_id
            constraintName: fk_mouvements_produits
            referencedTableName: produits
            referencedColumnNames: id
            onDelete: CASCADE
```

### 6. Insérer des données

```yaml
databaseChangeLog:
  - changeSet:
      id: insert-initial-data
      author: fatima-ezzahra
      changes:
        - insert:
            tableName: fournisseurs
            columns:
              - column:
                  name: societe
                  value: "Fournisseur Test"
              - column:
                  name: email
                  value: "test@example.com"
```

### 7. SQL personnalisé

```yaml
databaseChangeLog:
  - changeSet:
      id: custom-sql-update
      author: fatima-ezzahra
      changes:
        - sql:
            sql: |
              UPDATE produits 
              SET stock_actuel = 0 
              WHERE stock_actuel IS NULL;
```

---

## Configuration

### application.properties

```properties
# Activer Liquibase
spring.liquibase.enabled=true

# Chemin du changelog master
spring.liquibase.change-log=classpath:db/changelog/db.changelog-master.yaml

# Contextes (optionnel)
spring.liquibase.contexts=dev,test

# Schema par défaut
spring.liquibase.default-schema=public

# Drop first (⚠️ Attention en production !)
spring.liquibase.drop-first=false
```

### Tables de suivi Liquibase

Liquibase crée automatiquement deux tables :

#### 1. DATABASECHANGELOG

Historique de toutes les migrations exécutées :

| Colonne | Description |
|---------|-------------|
| ID | ID du changeSet |
| AUTHOR | Auteur du changeSet |
| FILENAME | Nom du fichier |
| DATEEXECUTED | Date d'exécution |
| ORDEREXECUTED | Ordre d'exécution |
| EXECTYPE | Type d'exécution (EXECUTED, RERAN) |
| MD5SUM | Checksum du changeSet |

**Exemple** :

| ID | AUTHOR | FILENAME | DATEEXECUTED | ORDEREXECUTED |
|----|--------|----------|--------------|---------------|
| create-produits-table | fatima-ezzahra | 005-create-produits-table.yaml | 2025-11-10 21:00:00 | 5 |

#### 2. DATABASECHANGELOGLOCK

Verrou pour éviter les exécutions concurrentes :

| Colonne | Description |
|---------|-------------|
| ID | ID du verrou (toujours 1) |
| LOCKED | Verrou actif (true/false) |
| LOCKGRANTED | Date du verrou |
| LOCKEDBY | Qui a posé le verrou |

---

## Rollback

### Définir un rollback

```yaml
databaseChangeLog:
  - changeSet:
      id: add-column-email
      author: fatima-ezzahra
      changes:
        - addColumn:
            tableName: fournisseurs
            columns:
              - column:
                  name: email
                  type: VARCHAR(255)
      rollback:
        - dropColumn:
            tableName: fournisseurs
            columnName: email
```

### Exécuter un rollback

**Rollback du dernier changeSet** :
```bash
mvn liquibase:rollback -Dliquibase.rollbackCount=1
```

**Rollback jusqu'à une date** :
```bash
mvn liquibase:rollback -Dliquibase.rollbackDate=2025-11-10
```

**Rollback jusqu'à un tag** :
```bash
mvn liquibase:rollback -Dliquibase.rollbackTag=version-1.0
```

---

## Bonnes pratiques

### 1. Nommage des fichiers

```
XXX-description-claire.yaml

Exemples :
- 005-create-produits-table.yaml
- 006-create-produits-indexes.yaml
- 007-add-column-email-to-fournisseurs.yaml
```

### 2. Un changeSet = Une modification

```yaml
# ✅ Bon : Un changeSet par table
- changeSet:
    id: create-produits-table
    changes:
      - createTable: ...

# ❌ Mauvais : Plusieurs tables dans un changeSet
- changeSet:
    id: create-all-tables
    changes:
      - createTable: produits
      - createTable: fournisseurs
      - createTable: commandes
```

### 3. ID unique et descriptif

```yaml
# ✅ Bon
id: create-produits-table

# ❌ Mauvais
id: changeset-1
```

### 4. Toujours indiquer l'auteur

```yaml
author: fatima-ezzahra
```

### 5. Ne jamais modifier un changeSet exécuté

```
❌ Ne pas faire :
- Modifier un changeSet déjà exécuté

✅ À faire :
- Créer un nouveau changeSet pour la modification
```

### 6. Tester les rollbacks

```bash
# Appliquer la migration
mvn liquibase:update

# Tester le rollback
mvn liquibase:rollback -Dliquibase.rollbackCount=1

# Réappliquer
mvn liquibase:update
```

---

## Questions fréquentes

### Q1 : Qu'est-ce que Liquibase ?

**Réponse** : Liquibase est un outil de gestion des migrations de base de données qui permet de versionner et de suivre les changements du schéma. Il garantit que tous les environnements (dev, test, prod) ont le même schéma de base de données.

### Q2 : Comment Liquibase sait quelles migrations exécuter ?

**Réponse** : Liquibase maintient une table `DATABASECHANGELOG` qui enregistre tous les changeSets exécutés. Au démarrage, il compare cette table avec les fichiers de migration et exécute uniquement les nouveaux changeSets.

### Q3 : Peut-on modifier un changeSet déjà exécuté ?

**Réponse** : Non, il ne faut jamais modifier un changeSet déjà exécuté car Liquibase utilise un checksum MD5 pour détecter les modifications. Si on doit faire un changement, il faut créer un nouveau changeSet.

### Q4 : Comment faire un rollback ?

**Réponse** : On définit une section `rollback` dans le changeSet qui indique comment annuler le changement. Ensuite, on utilise la commande Maven `mvn liquibase:rollback` avec le nombre de changeSets à annuler.

### Q5 : Pourquoi séparer les tables et les index ?

**Réponse** : C'est une bonne pratique pour faciliter la maintenance et le rollback. Si on doit annuler la création d'un index, on n'a pas besoin d'annuler toute la table. Cela rend aussi les migrations plus modulaires et testables.

---

[← MapStruct](03-MAPSTRUCT.md) | [Suivant : Pagination et Tri →](05-PAGINATION-TRI.md)
