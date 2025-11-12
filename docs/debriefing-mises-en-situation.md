# Débriefing – Mises en situation possibles

Ce document regroupe les scénarios les plus probables pour le débrief. Chaque mise en situation liste les objectifs, le périmètre technique et, lorsque pertinent, un exemple de code ou de migration prêt à l’emploi.

---

## 1. Calcul du montant total d’une commande
- **Objectif** : recalculer le montant d’une commande (`CommandeFournisseur`) à partir des lignes `CommandeProduit`.
- **Classes** : `CommandeFournisseurService`, `CommandeProduit`, `ProduitCommandeDTO`.
- **Snippet** :
```java
BigDecimal montantTotal = commandeProduits.stream()
    .map(cp -> cp.getPrixUnitaireCommande().multiply(BigDecimal.valueOf(cp.getQuantite())))
    .reduce(BigDecimal.ZERO, BigDecimal::add);
```
- **Points à vérifier** : cohérence du stock, mise à jour du champ `montantTotal`, protection contre les doublons.

## 2. Calcul du montant total de toutes les commandes
- **Objectif** : obtenir le chiffre d’affaires global.
- **Classes** : `CommandeFournisseurService`, `CommandeFournisseurRepository`.
- **Snippet** :
```java
BigDecimal total = commandeRepository.findAll().stream()
    .map(CommandeFournisseur::getMontantTotal)
    .filter(Objects::nonNull)
    .reduce(BigDecimal.ZERO, BigDecimal::add);
```
- **Variante SQL** : requête `SELECT SUM(montant_total) FROM commandes_fournisseur`.

## 3. Ajouter une colonne via une migration Liquibase
- **Objectif** : enrichir la table `fournisseurs` (ex. colonne `reference_interne`).
- **Étapes** :
  1. Créer `src/main/resources/db/changelog/migrations/010-ajout-reference-fournisseur.yaml`.
  2. L’ajouter dans `db.changelog-master.yaml`.
  3. Mettre à jour l’entité JPA + DTO + formulaires.
  4. Démarrer l’application pour exécuter la migration.
- **Migration type** :
```yaml
databaseChangeLog:
  - changeSet:
      id: 010-ajout-reference-fournisseur
      author: youco
      changes:
        - addColumn:
            tableName: fournisseurs
            columns:
              - column:
                  name: reference_interne
                  type: VARCHAR(100)
                  constraints:
                    nullable: true
      rollback:
        - dropColumn:
            tableName: fournisseurs
            columnName: reference_interne
```

## 4. Filtrer les mouvements de stock (UI & service)
- **Objectif** : filtrer par produit et/ou type (`ENTREE`, `SORTIE`, `AJUSTEMENT`).
- **Classes** : `MouvementStockViewController`, `MouvementStockService`, `MouvementStockRepository`.
- **Démarche** : lecture des paramètres, utilisation de `findByFilters(...)`, mise à jour de `templates/mouvements/list.html`.

## 5. Cloner un produit ou dupliquer une commande
- **Objectif** : accélérer la saisie.
- **Étapes** :
  - récupérer l’entité à cloner.
  - mapper en DTO, vider l’ID et les champs à régénérer.
  - pré-remplir le formulaire Thymeleaf.
- **Points sensibles** : lignes de commande (`CommandeProduit`) et recalcul des montants/stock.

## 6. Gestion d’erreurs côté interface Thymeleaf
- **Objectif** : expliquer la boucle validation → retour formulaire.
- **Éléments** :
  - `@Valid` sur les DTO.
  - Messages d’erreur localisés (`th:errors`).
  - Flash message et redirection en cas de succès.

## 7. Ajouter une API REST supplémentaire
- **Exemple** : exposer `GET /api/v1/mouvements`.
- **Étapes** :
  1. Créer un `MouvementStockRestController`.
  2. Injecter `MouvementStockService`.
  3. Retourner des `MouvementStockDTO`.
  4. Gérer la pagination et les filtres en query parameters.

## 8. Suivi des migrations Liquibase
- **Objectif** : montrer comment tracer les évolutions du schéma.
- **Points** :
  - Table `DATABASECHANGELOG`.
  - Convention de nommage (`001-`, `002-`, etc.).
  - Gestion des `rollback`.

## 9. Recherche côté UI (ville / société)
- **Objectif** : filtrer les fournisseurs depuis la vue.
- **Étapes** :
  - Champ de recherche dans `fournisseurs/list.html`.
  - Méthode `findBySocieteContainingIgnoreCase` et/ou `findByVilleIgnoreCase` dans `FournisseurRepository`.
  - Adapter `FournisseurViewController`.

## 10. Sécurisation rapide
- **Scénario** : ajouter une authentification simple.
- **Pistes** :
  - Ajouter Spring Security.
  - Définir un utilisateur en mémoire.
  - Restreindre `/ui/**`.

---

## Annexes – Snippets prêts à l’emploi

### Calculs & agrégations
- **Montant total d’une commande** : voir section 1.
- **Montant total de toutes les commandes** : voir section 2.

### Filtres sur les mouvements de stock
```java
List<MouvementStock> mouvementsEntree = mouvements.stream()
    .filter(m -> m.getTypeMouvement() == TypeMouvement.ENTREE)
    .toList();

List<MouvementStock> mouvementsSortie = mouvements.stream()
    .filter(m -> m.getTypeMouvement() == TypeMouvement.SORTIE)
    .toList();

List<MouvementStock> mouvementsAjustement = mouvements.stream()
    .filter(m -> m.getTypeMouvement() == TypeMouvement.AJUSTEMENT)
    .toList();

Long produitIdRecherche = ...;
List<MouvementStock> mouvementsProduit = mouvements.stream()
    .filter(m -> m.getProduit() != null
        && Objects.equals(m.getProduit().getId(), produitIdRecherche))
    .toList();
```
> **Note** : le filtre `ENTREE` est volontairement répété pour rappeler le scénario demandé.

### Recherche fournisseurs par ville ou société
```java
String villeRecherche = ...;       // exemple : "Casablanca"
String societeRecherche = ...;     // exemple : "Acme"

List<Fournisseur> resultat = fournisseurs.stream()
    .filter(f -> (villeRecherche == null || villeRecherche.isBlank()
            || (f.getVille() != null && f.getVille().equalsIgnoreCase(villeRecherche)))
        && (societeRecherche == null || societeRecherche.isBlank()
            || (f.getSociete() != null
                && f.getSociete().toLowerCase().contains(societeRecherche.toLowerCase()))))
    .toList();
```

---

## Préparation personnelle
- Réviser l’architecture globale (REST, Thymeleaf, services, repositories).
- Être prêt à dérouler un flux complet (“je crée une commande, que se passe-t-il ?”).
- Justifier les choix techniques (MapStruct, DTO, validation, Liquibase).
- Préparer quelques requêtes SQL illustratives (jointures, agrégats).

Bon débriefing !