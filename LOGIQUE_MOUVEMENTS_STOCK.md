# 📦 Logique des Mouvements de Stock

## 🎯 Contexte

**TricolSupplyPro** est un fabricant de vêtements professionnels qui vend ses produits à des **fournisseurs/grossistes** (clients de Tricol).

Une `CommandeFournisseur` représente une **commande passée par un fournisseur** (= vente pour Tricol).

---

## 🔄 Workflow des Mouvements de Stock

### 1️⃣ **Ajout d'un nouveau produit** → Mouvement `ENTREE`

**Quand** : Lors de la création d'un produit avec `POST /api/v1/produits`

**Action automatique** :
- ✅ Création d'un mouvement de type `ENTREE`
- ✅ `produit_id` = ID du produit créé
- ✅ `commande_fournisseur_id` = `null` (pas de commande associée)
- ✅ `quantite` = `stockActuel` du produit
- ✅ `prixUnitaire` = `prixUnitaire` du produit

**Exemple** :
```json
POST /api/v1/produits
{
  "nom": "Veste professionnelle",
  "prixUnitaire": 150.00,
  "stockActuel": 100,
  "categorie": "Vêtements"
}
```
→ Crée automatiquement un mouvement ENTREE de 100 unités

---

### 2️⃣ **Création d'une commande** → Réservation de stock

**Quand** : Lors de la création d'une commande avec `POST /api/v1/commandes`

**Action automatique** :
- ✅ Vérification de la disponibilité du stock
- ✅ **Diminution immédiate du `stockActuel`** (réservation)
- ❌ Aucun mouvement créé à cette étape

**Exemple** :
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
→ `stockActuel` passe de 100 à 80 (réservation de 20 unités)

---

### 3️⃣ **Commande passe au statut `LIVRÉE`** → Mouvement `SORTIE`

**Quand** : Lors du changement de statut avec `PATCH /api/v1/commandes/{id}/statut?statut=LIVREE`

**Action automatique** :
- ✅ Création d'un mouvement de type `SORTIE`
- ✅ `produit_id` = ID du produit concerné
- ✅ `commande_fournisseur_id` = ID de la commande
- ✅ `quantite` = quantité commandée
- ✅ `prixUnitaire` = `prixUnitaireCommande`
- ⚠️ **Le stock n'est PAS diminué** (déjà fait lors de la création)

**Exemple** :
```
PATCH /api/v1/commandes/1/statut?statut=LIVREE
```
→ Crée un mouvement SORTIE de 20 unités (le stock reste à 80)

---

### 4️⃣ **Commande annulée** → Restauration du stock

**Quand** : Lors du changement de statut avec `PATCH /api/v1/commandes/{id}/statut?statut=ANNULEE`

**Action automatique** :
- ✅ **Augmentation du `stockActuel`** (annulation de la réservation)
- ✅ Création d'un mouvement de type `AJUSTEMENT`
- ✅ `produit_id` = ID du produit concerné
- ✅ `commande_fournisseur_id` = ID de la commande
- ✅ `quantite` = quantité à restaurer

**Exemple** :
```
PATCH /api/v1/commandes/1/statut?statut=ANNULEE
```
→ `stockActuel` passe de 80 à 100 (restauration de 20 unités)

---

### 5️⃣ **Modification manuelle du stock** → Mouvement `AJUSTEMENT`

**Quand** : Lors de la modification d'un produit avec `PUT /api/v1/produits/{id}` et changement de `stockActuel`

**Action automatique** :
- ✅ Création d'un mouvement de type `AJUSTEMENT`
- ✅ `produit_id` = ID du produit modifié
- ✅ `commande_fournisseur_id` = `null`
- ✅ `quantite` = valeur absolue de la différence
- ✅ `prixUnitaire` = `prixUnitaire` du produit

**Exemple** :
```json
PUT /api/v1/produits/1
{
  "nom": "Veste professionnelle",
  "prixUnitaire": 150.00,
  "stockActuel": 85,  // Modifié de 80 à 85
  "categorie": "Vêtements"
}
```
→ Crée un mouvement AJUSTEMENT de 5 unités

---

## 📊 Résumé des Types de Mouvements

| Type | Quand | Stock | Commande liée |
|------|-------|-------|---------------|
| **ENTREE** | Ajout produit | Augmente | ❌ Non |
| **SORTIE** | Commande LIVRÉE | Déjà diminué | ✅ Oui |
| **AJUSTEMENT** | Modification manuelle OU Annulation | Variable | Variable |

---

## ⚠️ Points importants

1. **Le stock diminue lors de la CRÉATION de la commande**, pas lors de la livraison
2. **La livraison crée seulement un mouvement SORTIE** pour tracer l'opération
3. **L'annulation restaure le stock** et crée un mouvement AJUSTEMENT
4. **Chaque mouvement est tracé** dans la table `mouvements_stock`

---

## 🔍 Consultation des mouvements

```
GET /api/v1/mouvements/produit/{produitId}
GET /api/v1/mouvements/commande/{commandeId}
```

Ces endpoints permettent de consulter l'historique complet des mouvements.
