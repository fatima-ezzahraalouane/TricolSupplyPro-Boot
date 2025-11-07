# 📋 Valeurs des Enums pour l'API TricolSupplyPro

Ce document liste toutes les valeurs acceptées pour les enums de l'API.

---

## 🔄 StatutCommande

Utilisé pour le statut des commandes fournisseurs.

### Valeurs acceptées :
- `EN_ATTENTE` - Commande en attente de validation
- `VALIDEE` - Commande validée
- `LIVREE` - Commande livrée
- `ANNULEE` - Commande annulée

### Exemple d'utilisation :

**Créer une commande :**
```json
{
  "fournisseurId": 1,
  "dateCommande": "2025-11-07T20:00:00",
  "statut": "EN_ATTENTE",
  "produits": [
    {
      "produitId": 1,
      "quantite": 50,
      "prixUnitaireCommande": 5200.00
    }
  ]
}
```

**Changer le statut :**
```
PATCH /api/v1/commandes/1/statut?statut=VALIDEE
```

---

## 📦 TypeMouvement

Utilisé pour le type de mouvement de stock.

### Valeurs acceptées :
- `ENTREE` - Entrée de stock (réception de marchandises)
- `SORTIE` - Sortie de stock (vente ou utilisation)
- `AJUSTEMENT` - Ajustement de stock (correction d'inventaire)

### Exemple d'utilisation :

Les mouvements de stock sont générés automatiquement lors de la validation d'une commande fournisseur.

---

## ⚠️ Important

- Les valeurs sont **sensibles à la casse** (majuscules uniquement)
- Utilisez exactement les valeurs listées ci-dessus
- N'utilisez pas de libellés français comme "En attente" ou "Validée"

---

## 🔗 Ressources

- Collection Postman : `TricolSupplyPro.postman_collection.json`
- Documentation Swagger : http://localhost:8080/swagger-ui.html (quand l'application est lancée)
