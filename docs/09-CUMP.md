# 💰 CUMP (Coût Unitaire Moyen Pondéré) - Guide Complet

[← Retour à l'index](README.md) | [← Architecture](08-ARCHITECTURE.md)

---

## 📖 Table des matières

1. [Qu'est-ce que le CUMP ?](#quest-ce-que-le-cump)
2. [Pourquoi utiliser le CUMP ?](#pourquoi-utiliser-le-cump)
3. [Formule mathématique](#formule-mathématique)
4. [Exemples concrets](#exemples-concrets)
5. [Implémentation dans le projet](#implémentation-dans-le-projet)
6. [Cas particuliers](#cas-particuliers)
7. [Comparaison CUMP vs FIFO](#comparaison-cump-vs-fifo)
8. [Tests et validation](#tests-et-validation)

---

## Qu'est-ce que le CUMP ?

### Définition

Le **CUMP (Coût Unitaire Moyen Pondéré)** est une méthode de valorisation des stocks qui calcule le coût moyen d'une unité de produit en tenant compte de toutes les entrées de stock, pondérées par leurs quantités respectives.

### Principe

À chaque nouvelle entrée de stock, le CUMP est recalculé en faisant la moyenne pondérée entre :
- Le stock existant (quantité × CUMP actuel)
- La nouvelle entrée (quantité × prix unitaire d'achat)

### Avantages

✅ **Simplicité** : Calcul direct et facile à comprendre  
✅ **Lissage des variations** : Les variations de prix sont lissées sur l'ensemble du stock  
✅ **Gestion facilitée** : Pas besoin de tracer les lots individuels  
✅ **Conformité comptable** : Méthode reconnue et acceptée en comptabilité

---

## Pourquoi utiliser le CUMP ?

### Contexte métier

Dans le projet **TricolSupplyPro-Boot**, le CUMP permet de :

1. **Valoriser le stock** de manière précise et cohérente
2. **Calculer les coûts** lors des sorties de stock
3. **Suivre l'évolution** des prix d'achat dans le temps
4. **Faciliter la gestion** sans avoir à gérer des lots séparés

### Exemple de besoin

Imaginez que Tricol achète du tissu :
- **Achat 1** : 100 mètres à 15€/mètre
- **Achat 2** : 50 mètres à 18€/mètre

Sans CUMP, il faudrait gérer deux lots séparés. Avec CUMP, on a un coût moyen unique qui simplifie la gestion.

---

## Formule mathématique

### Formule générale

```
CUMP = (Valeur stock ancien + Valeur nouvelle entrée) / Quantité totale

Où :
- Valeur stock ancien = stock_actuel × cout_unitaire_moyen_actuel
- Valeur nouvelle entrée = quantite_entree × prix_unitaire_entree
- Quantité totale = stock_actuel + quantite_entree
```

### Formule développée

```
CUMP_nouveau = (stock_actuel × CUMP_ancien + quantite_entree × prix_unitaire_entree) 
               / (stock_actuel + quantite_entree)
```

### Précision

Le CUMP est calculé avec **2 décimales** (arrondi au centime près) pour correspondre aux standards comptables.

---

## Exemples concrets

### Exemple 1 : Premier achat

**Situation initiale** :
- Stock : 0 unité
- CUMP : 0€

**Achat** :
- 100 unités à 10€/unité

**Calcul** :
```
CUMP = (0 × 0€ + 100 × 10€) / (0 + 100)
     = 1000€ / 100
     = 10,00€
```

**Résultat** :
- Stock : 100 unités
- CUMP : 10,00€
- Valeur du stock : 100 × 10€ = 1 000€

---

### Exemple 2 : Deuxième achat (prix différent)

**Situation initiale** :
- Stock : 100 unités
- CUMP : 10,00€
- Valeur stock : 1 000€

**Nouvel achat** :
- 50 unités à 12€/unité
- Valeur entrée : 50 × 12€ = 600€

**Calcul** :
```
CUMP = (100 × 10€ + 50 × 12€) / (100 + 50)
     = (1 000€ + 600€) / 150
     = 1 600€ / 150
     = 10,67€
```

**Résultat** :
- Stock : 150 unités
- CUMP : 10,67€
- Valeur du stock : 150 × 10,67€ = 1 600,50€

---

### Exemple 3 : Troisième achat (prix inférieur)

**Situation initiale** :
- Stock : 150 unités
- CUMP : 10,67€
- Valeur stock : 1 600,50€

**Nouvel achat** :
- 75 unités à 8€/unité
- Valeur entrée : 75 × 8€ = 600€

**Calcul** :
```
CUMP = (150 × 10,67€ + 75 × 8€) / (150 + 75)
     = (1 600,50€ + 600€) / 225
     = 2 200,50€ / 225
     = 9,78€
```

**Résultat** :
- Stock : 225 unités
- CUMP : 9,78€ (le CUMP a baissé car le nouveau prix est inférieur)
- Valeur du stock : 225 × 9,78€ = 2 200,50€

---

### Exemple 4 : Tableau récapitulatif

| Étape | Stock | CUMP | Achat | Prix Achat | Nouveau Stock | Nouveau CUMP |
|-------|-------|------|-------|------------|---------------|--------------|
| Initial | 0 | 0,00€ | - | - | 0 | 0,00€ |
| Achat 1 | 0 | 0,00€ | 100 | 10,00€ | 100 | 10,00€ |
| Achat 2 | 100 | 10,00€ | 50 | 12,00€ | 150 | 10,67€ |
| Achat 3 | 150 | 10,67€ | 75 | 8,00€ | 225 | 9,78€ |

---

## Implémentation dans le projet

### Structure de données

Le CUMP est stocké dans l'entité `Produit` :

```java
@Entity
@Table(name = "produits")
public class Produit {
    
    @Column(name = "stock_actuel")
    private Integer stockActuel = 0;
    
    @Column(name = "cout_unitaire_moyen", precision = 19, scale = 2)
    private BigDecimal coutUnitaireMoyen = BigDecimal.ZERO;
    
    // ... autres champs
}
```

### Méthode de calcul

```java
private void calculerEtMettreAJourCUMP(Produit produit, int quantite, BigDecimal prixUnitaire) {
    // Valeur du stock actuel
    BigDecimal valeurStockActuel = produit.getCoutUnitaireMoyen()
            .multiply(new BigDecimal(produit.getStockActuel()));
    
    // Valeur de la nouvelle entrée
    BigDecimal valeurNouvelleEntree = prixUnitaire
            .multiply(new BigDecimal(quantite));
    
    // Stock total après entrée
    int stockTotal = produit.getStockActuel() + quantite;
    
    // Calcul du nouveau CUMP avec arrondi à 2 décimales
    BigDecimal nouveauCUMP = valeurStockActuel
            .add(valeurNouvelleEntree)
            .divide(new BigDecimal(stockTotal), 2, RoundingMode.HALF_UP);
    
    // Mise à jour du produit
    produit.setCoutUnitaireMoyen(nouveauCUMP);
    produit.setStockActuel(stockTotal);
}
```

### Moment du calcul

Le CUMP est recalculé automatiquement lors de :

1. **Livraison d'une commande** (`StatutCommande.LIVREE`)
   - Chaque produit de la commande déclenche un recalcul
   - Le prix utilisé est `prixUnitaireCommande` de la commande

2. **Création d'un produit avec stock initial**
   - Le CUMP initial = `prixUnitaire` du produit

3. **Ajustement manuel du stock**
   - Si un ajustement est fait, le CUMP peut être recalculé selon les règles métier

### Workflow complet

```
1. Commande créée (EN_ATTENTE)
   └─> Stock réservé (diminué)

2. Commande validée (VALIDEE)
   └─> Aucun changement de stock

3. Commande livrée (LIVREE)
   └─> Mouvement de stock créé (SORTIE)
   └─> CUMP recalculé pour chaque produit
   └─> Stock mis à jour
```

---

## Cas particuliers

### Cas 1 : Stock initial à zéro

**Situation** : Premier achat d'un produit

**Traitement** :
```java
if (produit.getStockActuel() == 0 || produit.getCoutUnitaireMoyen().equals(BigDecimal.ZERO)) {
    // Premier achat : CUMP = prix d'achat
    produit.setCoutUnitaireMoyen(prixUnitaire);
    produit.setStockActuel(quantite);
} else {
    // Calcul normal du CUMP
    calculerEtMettreAJourCUMP(produit, quantite, prixUnitaire);
}
```

### Cas 2 : Division par zéro

**Protection** :
```java
if (stockTotal == 0) {
    throw new IllegalArgumentException("Le stock total ne peut pas être zéro");
}
```

### Cas 3 : Prix négatif ou nul

**Validation** :
```java
if (prixUnitaire == null || prixUnitaire.compareTo(BigDecimal.ZERO) <= 0) {
    throw new IllegalArgumentException("Le prix unitaire doit être strictement positif");
}
```

### Cas 4 : Quantité négative

**Validation** :
```java
if (quantite <= 0) {
    throw new IllegalArgumentException("La quantité doit être strictement positive");
}
```

---

## Comparaison CUMP vs FIFO

### CUMP (Coût Unitaire Moyen Pondéré)

**Principe** : Calcul d'une moyenne pondérée de tous les achats

**Avantages** :
- ✅ Simple à implémenter
- ✅ Lisse les variations de prix
- ✅ Pas besoin de tracer les lots
- ✅ Conforme aux standards comptables

**Inconvénients** :
- ⚠️ Ne reflète pas le coût réel des sorties
- ⚠️ Peut masquer des variations importantes de prix

**Exemple** :
```
Achat 1 : 100 unités à 10€ → CUMP = 10€
Achat 2 : 50 unités à 20€ → CUMP = 13,33€
Sortie : 30 unités → Coût = 30 × 13,33€ = 400€
```

---

### FIFO (First In, First Out)

**Principe** : Les premières entrées sont les premières sorties

**Avantages** :
- ✅ Reflète le coût réel des sorties
- ✅ Suit l'ordre chronologique des achats

**Inconvénients** :
- ⚠️ Complexe à implémenter (gestion des lots)
- ⚠️ Nécessite de tracer chaque lot d'achat
- ⚠️ Plus coûteux en ressources

**Exemple** :
```
Achat 1 : 100 unités à 10€
Achat 2 : 50 unités à 20€
Sortie : 30 unités → Coût = 30 × 10€ = 300€ (du lot 1)
```

---

### Choix pour ce projet

**Pourquoi CUMP ?**

1. **Simplicité** : Le cahier des charges mentionnait CUMP
2. **Gestion facilitée** : Pas besoin de gérer des lots
3. **Performance** : Calcul direct sans recherche de lots
4. **Standard** : Méthode largement utilisée en comptabilité

**Note** : Le système pourrait être étendu pour supporter FIFO si nécessaire, mais cela nécessiterait une refonte de la gestion des mouvements de stock.

---

## Tests et validation

### Tests unitaires

Les tests unitaires vérifient :

1. **Calcul correct du CUMP**
   ```java
   @Test
   void testCalculCUMP_PremierAchat() {
       // Stock initial : 0, CUMP : 0
       // Achat : 100 unités à 10€
       // Résultat attendu : CUMP = 10€
   }
   
   @Test
   void testCalculCUMP_DeuxiemeAchat() {
       // Stock initial : 100 unités, CUMP : 10€
       // Achat : 50 unités à 12€
       // Résultat attendu : CUMP = 10,67€
   }
   ```

2. **Gestion des cas limites**
   - Stock à zéro
   - Prix négatif
   - Quantité négative
   - Division par zéro

3. **Précision décimale**
   - Vérification de l'arrondi à 2 décimales
   - Test avec des valeurs décimales

### Tests d'intégration

Les tests d'intégration vérifient :

1. **Workflow complet**
   - Création d'une commande
   - Livraison de la commande
   - Vérification du CUMP mis à jour

2. **Cohérence des données**
   - Le CUMP est bien sauvegardé en base
   - Les mouvements de stock sont créés correctement

### Exemple de test

```java
@Test
@DisplayName("Test calcul CUMP lors de la livraison d'une commande")
void testCalculCUMP_LivraisonCommande() {
    // Arrange
    Produit produit = creerProduit("Tissu", 100, new BigDecimal("10.00"));
    
    CommandeFournisseur commande = creerCommande(produit, 50, new BigDecimal("12.00"));
    
    // Act
    commandeService.changerStatut(commande.getId(), StatutCommande.LIVREE);
    
    // Assert
    Produit produitMisAJour = produitRepository.findById(produit.getId()).orElseThrow();
    assertEquals(150, produitMisAJour.getStockActuel());
    assertEquals(new BigDecimal("10.67"), produitMisAJour.getCoutUnitaireMoyen());
}
```

---

## 📝 Points clés à retenir

### Pour le débriefing

1. **Définition** : Le CUMP est une moyenne pondérée de tous les achats
2. **Formule** : `CUMP = (Valeur stock ancien + Valeur entrée) / Stock total`
3. **Avantage** : Simplicité et conformité comptable
4. **Moment** : Calculé automatiquement lors de la livraison d'une commande
5. **Précision** : 2 décimales (arrondi au centime)

### Questions fréquentes

**Q : Pourquoi CUMP et pas FIFO ?**  
R : Le CUMP est plus simple à implémenter et correspond au cahier des charges. Il lisse les variations de prix et facilite la gestion.

**Q : Quand le CUMP est-il recalculé ?**  
R : À chaque livraison de commande, pour chaque produit de la commande.

**Q : Que se passe-t-il si le stock est à zéro ?**  
R : Le CUMP prend directement la valeur du prix d'achat.

**Q : Comment gérer les arrondis ?**  
R : Arrondi à 2 décimales avec `RoundingMode.HALF_UP` (arrondi au centime supérieur si ≥ 0.5).

---

## 🎯 Conclusion

Le CUMP est une méthode de valorisation des stocks **simple, efficace et conforme** aux standards comptables. Son implémentation dans **TricolSupplyPro-Boot** permet de :

- ✅ Valoriser précisément le stock
- ✅ Suivre l'évolution des prix d'achat
- ✅ Faciliter la gestion sans lots complexes
- ✅ Respecter les normes comptables

Cette méthode est particulièrement adaptée pour une entreprise comme Tricol qui gère des approvisionnements réguliers avec des variations de prix.

---

**📚 Ressources complémentaires** :
- [Architecture du projet](08-ARCHITECTURE.md)
- [Gestion des mouvements de stock](../README.md#-gestion-des-mouvements-de-stock)
- [Règles métier](../README.md#-règles-métier)

