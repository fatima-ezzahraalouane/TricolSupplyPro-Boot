# 🌟 Spring Boot - Guide Complet

[← Retour à l'index](README.md)

---

## 📖 Table des matières

1. [Qu'est-ce que Spring Boot ?](#quest-ce-que-spring-boot)
2. [Les 4 piliers](#les-4-piliers-de-spring-boot)
3. [Concepts clés](#concepts-clés)
4. [Annotations essentielles](#annotations-essentielles)
5. [Cycle de vie](#cycle-de-vie)

---

## Qu'est-ce que Spring Boot ?

### Définition

**Spring Boot** est un framework Java open-source qui simplifie le développement d'applications Java enterprise, en particulier les applications web et les microservices.

**Créé par** : Pivotal (maintenant VMware)  
**Première version** : 2014  
**Version du projet** : 3.5.7

### Pourquoi Spring Boot existe-t-il ?

#### Avant Spring Boot ❌

```xml
<!-- Configuration XML complexe (100+ lignes) -->
<beans>
    <bean id="dataSource" class="org.apache.commons.dbcp.BasicDataSource">
        <property name="driverClassName" value="org.postgresql.Driver"/>
        <property name="url" value="jdbc:postgresql://localhost:5432/mydb"/>
        <!-- ... beaucoup de configuration -->
    </bean>
    
    <bean id="entityManagerFactory" class="...">
        <!-- ... encore plus de configuration -->
    </bean>
    
    <!-- Des dizaines d'autres beans... -->
</beans>
```

**Problèmes** :
- Configuration XML verbeuse
- Serveur externe à installer (Tomcat)
- Gestion manuelle des dépendances
- Temps de démarrage long

#### Avec Spring Boot ✅

```java
@SpringBootApplication
public class TricolSupplyProApplication {
    public static void main(String[] args) {
        SpringApplication.run(TricolSupplyProApplication.class, args);
    }
}
```

```properties
# application.properties
spring.datasource.url=jdbc:postgresql://localhost:5432/tricolsupplyboot
spring.datasource.username=postgres
spring.datasource.password=admin
```

**Avantages** :
- Configuration minimale
- Serveur embarqué (Tomcat inclus)
- Dépendances pré-configurées
- Démarrage rapide

---

## Les 4 piliers de Spring Boot

### 1️⃣ Auto-Configuration

**Définition** : Spring Boot configure automatiquement votre application en fonction des dépendances présentes.

**Comment ça marche ?**

1. **Scan du classpath** : Spring Boot détecte les bibliothèques
2. **Conditions** : Vérifie si certaines classes existent
3. **Configuration auto** : Active les configurations nécessaires

**Exemple** :

```xml
<!-- Vous ajoutez cette dépendance -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```

**Spring Boot configure automatiquement** :
- ✅ DataSource (connexion base de données)
- ✅ EntityManagerFactory (gestionnaire JPA)
- ✅ TransactionManager (gestion transactions)
- ✅ JPA Repositories

**Vous écrivez seulement** :

```java
@Repository
public interface ProduitRepository extends JpaRepository<Produit, Long> {
    // Spring Data génère l'implémentation !
}
```

### 2️⃣ Starter Dependencies

**Définition** : Les starters regroupent toutes les dépendances nécessaires pour une fonctionnalité.

**Starters du projet** :

| Starter | Contient | Utilité |
|---------|----------|---------|
| `spring-boot-starter-web` | Spring MVC, Tomcat, Jackson | API REST |
| `spring-boot-starter-data-jpa` | Hibernate, Spring Data JPA | Accès données |
| `spring-boot-starter-validation` | Hibernate Validator | Validation |

**Avantage** : Un seul starter remplace 20+ dépendances !

```xml
<!-- Un seul starter -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- Inclut automatiquement :
     - spring-web
     - spring-webmvc
     - tomcat-embed-core
     - jackson-databind
     - hibernate-validator
     - Et toutes leurs dépendances !
-->
```

### 3️⃣ Serveur Embarqué

**Définition** : Tomcat est intégré directement dans l'application JAR.

**Avant Spring Boot** :
```
1. Installer Tomcat séparément
2. Configurer Tomcat
3. Créer un WAR
4. Déployer le WAR
5. Démarrer Tomcat
```

**Avec Spring Boot** :
```bash
java -jar tricolsupplypro-boot.jar
# Tomcat démarre automatiquement !
```

**Architecture** :

```
┌─────────────────────────────────┐
│  Application JAR Exécutable     │
│  ┌───────────────────────────┐  │
│  │   Votre Code              │  │
│  └───────────────────────────┘  │
│  ┌───────────────────────────┐  │
│  │   Spring Boot             │  │
│  └───────────────────────────┘  │
│  ┌───────────────────────────┐  │
│  │   Tomcat Embarqué         │  │
│  └───────────────────────────┘  │
└─────────────────────────────────┘
```

**Avantages** :
- ✅ Déploiement simple (1 fichier)
- ✅ Docker-friendly
- ✅ Cloud-ready
- ✅ Idéal microservices

### 4️⃣ Production-Ready Features

**Définition** : Spring Boot Actuator fournit des endpoints pour monitorer l'application.

**Endpoints disponibles** :

| Endpoint | Description |
|----------|-------------|
| `/actuator/health` | État de santé |
| `/actuator/metrics` | Métriques (CPU, mémoire) |
| `/actuator/info` | Informations application |
| `/actuator/loggers` | Gestion des logs |

**Exemple de réponse** :

```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "PostgreSQL"
      }
    }
  }
}
```

---

## Concepts clés

### Inversion of Control (IoC)

**Définition** : Le framework gère la création des objets au lieu du développeur.

**Analogie** : 
- **Sans IoC** : Vous êtes un chef qui va chercher les ingrédients au marché
- **Avec IoC** : Les ingrédients sont livrés, vous cuisinez seulement

**Exemple** :

**❌ Sans IoC** :
```java
public class ProduitService {
    private ProduitRepository repository;
    
    public ProduitService() {
        this.repository = new ProduitRepository(); // Vous créez
    }
}
```

**✅ Avec IoC** :
```java
@Service
public class ProduitService {
    private final ProduitRepository repository;
    
    public ProduitService(ProduitRepository repository) {
        this.repository = repository; // Spring injecte
    }
}
```

**Avantages** :
- ✅ Couplage faible
- ✅ Facile à tester
- ✅ Code maintenable

### Dependency Injection (DI)

**Définition** : Mécanisme d'injection des dépendances par Spring.

**Types d'injection** :

**1. Par constructeur** (✅ Recommandé) :
```java
@Service
@RequiredArgsConstructor // Lombok génère le constructeur
public class ProduitService {
    private final ProduitRepository repository;
    private final ProduitMapper mapper;
    // Spring injecte automatiquement
}
```

**2. Par setter** (⚠️ Acceptable) :
```java
@Service
public class ProduitService {
    private ProduitRepository repository;
    
    @Autowired
    public void setRepository(ProduitRepository repository) {
        this.repository = repository;
    }
}
```

**3. Par champ** (❌ Déconseillé) :
```java
@Service
public class ProduitService {
    @Autowired
    private ProduitRepository repository;
}
```

**Pourquoi l'injection par constructeur est recommandée ?**
- ✅ Dépendances immutables (`final`)
- ✅ Obligatoires (pas de null)
- ✅ Facilite les tests
- ✅ Thread-safe

---

## Annotations essentielles

| Annotation | Rôle | Exemple |
|------------|------|---------|
| `@SpringBootApplication` | Point d'entrée | Classe principale |
| `@RestController` | Contrôleur REST | Controllers |
| `@Service` | Logique métier | Services |
| `@Repository` | Accès données | Repositories |
| `@Component` | Composant Spring | Classes utilitaires |
| `@Configuration` | Configuration | Classes de config |
| `@Autowired` | Injection dépendance | (préférer constructeur) |
| `@Transactional` | Gestion transactions | Méthodes service |
| `@GetMapping` | Endpoint HTTP GET | Récupérer données |
| `@PostMapping` | Endpoint HTTP POST | Créer ressource |
| `@PutMapping` | Endpoint HTTP PUT | Modifier ressource |
| `@DeleteMapping` | Endpoint HTTP DELETE | Supprimer ressource |
| `@PathVariable` | Variable URL | `/produits/{id}` |
| `@RequestParam` | Paramètre requête | `?page=0&size=10` |
| `@RequestBody` | Corps requête JSON | Données envoyées |
| `@Valid` | Validation données | Avec `@RequestBody` |

---

## Cycle de vie

### Démarrage d'une application Spring Boot

```
1. Démarrage de l'application
   └── SpringApplication.run()

2. Chargement de la configuration
   └── Lecture de application.properties

3. Auto-configuration
   └── Spring Boot configure les beans

4. Scan des composants
   └── Détection des @Component, @Service, @Repository

5. Création du contexte Spring (IoC Container)
   └── Instanciation de tous les beans

6. Injection des dépendances
   └── Spring injecte les dépendances

7. Exécution des migrations Liquibase
   └── Application des migrations

8. Démarrage du serveur embarqué
   └── Tomcat démarre sur le port 8080

9. Application prête
   └── L'API est accessible
```

---

## Spring vs Spring Boot

| Aspect | Spring Framework | Spring Boot |
|--------|-----------------|-------------|
| **Configuration** | XML ou Java Config manuelle | Auto-configuration |
| **Serveur** | Externe (Tomcat à installer) | Embarqué (inclus) |
| **Dépendances** | Gestion manuelle | Starters pré-configurés |
| **Démarrage** | Complexe, long | Simple, rapide |
| **Déploiement** | WAR sur serveur externe | JAR exécutable |
| **Courbe d'apprentissage** | Élevée | Modérée |

---

## Questions fréquentes pour le débriefing

### Q1 : Qu'est-ce que Spring Boot ?

**Réponse** : Spring Boot est un framework Java qui simplifie le développement d'applications enterprise en fournissant une auto-configuration, un serveur embarqué et des dépendances pré-configurées. Il permet de créer des applications standalone prêtes pour la production avec une configuration minimale.

### Q2 : Quels sont les avantages de Spring Boot ?

**Réponse** :
- Configuration automatique basée sur les dépendances
- Serveur web embarqué (Tomcat)
- Starters pour grouper les dépendances
- Production-ready avec Actuator
- Déploiement simplifié (JAR exécutable)
- Idéal pour les microservices

### Q3 : Expliquez l'IoC et la DI

**Réponse** : L'IoC (Inversion of Control) est un principe où le framework gère la création des objets au lieu du développeur. La DI (Dependency Injection) est le mécanisme par lequel Spring implémente l'IoC en injectant automatiquement les dépendances nécessaires dans les objets.

### Q4 : Qu'est-ce qu'un starter Spring Boot ?

**Réponse** : Un starter est un descripteur de dépendances Maven qui regroupe toutes les bibliothèques nécessaires pour une fonctionnalité spécifique. Par exemple, `spring-boot-starter-web` inclut Spring MVC, Tomcat, Jackson et toutes leurs dépendances.

### Q5 : Comment fonctionne l'auto-configuration ?

**Réponse** : Au démarrage, Spring Boot scanne le classpath, détecte les bibliothèques présentes (comme PostgreSQL ou Hibernate), et active automatiquement les configurations correspondantes. On peut toujours surcharger ces configurations si nécessaire.

---

[← Retour à l'index](README.md) | [Suivant : Spring Data JPA →](02-SPRING-DATA-JPA.md)
