# 📚 Documentation Technique - TricolSupplyPro-Boot

*Guide complet pour le débriefing technique*

---

## 📖 Table des matières

Chaque technologie est expliquée en détail dans un fichier séparé :

### 🌟 Technologies Principales

1. **[Spring Boot](01-SPRING-BOOT.md)**
   - Qu'est-ce que Spring Boot ?
   - Les 4 piliers (Auto-Configuration, Starters, Serveur Embarqué, Production-Ready)
   - IoC et Dependency Injection
   - Annotations essentielles
   - Cycle de vie d'une application

2. **[Spring Data JPA](02-SPRING-DATA-JPA.md)**
   - Qu'est-ce que JPA ?
   - Entités et annotations
   - Relations (@ManyToOne, @OneToMany)
   - Repositories Spring Data
   - Transactions avec @Transactional

3. **[MapStruct](03-MAPSTRUCT.md)**
   - Pourquoi utiliser des DTOs ?
   - Mapping automatique Entity ↔ DTO
   - Mappings personnalisés
   - Configuration Maven

4. **[Liquibase](04-LIQUIBASE.md)**
   - Gestion des migrations de base de données
   - Structure des fichiers
   - Types de changements
   - Rollback

5. **[Pagination et Tri](05-PAGINATION-TRI.md)**
   - Interface Pageable
   - Paramètres de requête
   - Objet Page
   - Exemples concrets

6. **[Validation Jakarta](06-VALIDATION.md)**
   - Annotations de validation
   - Validation dans les Controllers
   - Messages d'erreur personnalisés
   - Gestion des erreurs

7. **[Swagger/OpenAPI](07-SWAGGER.md)**
   - Documentation automatique de l'API
   - Annotations Swagger
   - Interface Swagger UI
   - Configuration

8. **[Architecture du Projet](08-ARCHITECTURE.md)**
   - Architecture en couches
   - Structure des packages
   - Flow d'une requête HTTP
   - Bonnes pratiques

9. **[CUMP (Coût Unitaire Moyen Pondéré)](09-CUMP.md)**
   - Qu'est-ce que le CUMP ?
   - Formule mathématique et calcul
   - Exemples concrets avec chiffres
   - Implémentation dans le projet
   - Comparaison CUMP vs FIFO
   - Tests et validation

---

## 🎯 Comment utiliser cette documentation ?

### Pour le débriefing

1. **Lisez chaque fichier dans l'ordre** pour une compréhension complète
2. **Pratiquez les explications** à voix haute
3. **Notez les exemples** qui vous semblent les plus clairs
4. **Préparez des réponses** aux questions fréquentes

### Questions types à préparer

- "Qu'est-ce que Spring Boot et pourquoi l'utiliser ?"
- "Expliquez l'IoC et la Dependency Injection"
- "Comment fonctionne JPA et Hibernate ?"
- "Pourquoi utiliser MapStruct ?"
- "Comment gérez-vous les migrations de base de données ?"
- "Expliquez la pagination dans votre API"
- "**Qu'est-ce que le CUMP et comment le calculez-vous ?**"
- "**Donnez un exemple concret de calcul CUMP avec des chiffres**"
- "**Pourquoi avoir choisi CUMP plutôt que FIFO ?**"

---

## 📝 Conseils pour le débriefing

### ✅ À faire

- Expliquer avec vos propres mots
- Donner des exemples concrets du projet
- Montrer que vous comprenez les concepts
- Être précis dans les termes techniques
- Faire le lien entre les technologies

### ❌ À éviter

- Réciter par cœur sans comprendre
- Utiliser des termes sans les expliquer
- Rester trop vague
- Confondre les concepts
- Oublier de mentionner les avantages

---

## 🚀 Bon débriefing !

Cette documentation a été créée spécifiquement pour vous aider à réussir votre débriefing technique. Prenez le temps de bien comprendre chaque concept et n'hésitez pas à revenir sur les sections qui vous semblent complexes.

**Bonne chance ! 🍀**
