# Guide d'exécution des tests avec Maven dans IntelliJ IDEA

## Comment utiliser Maven dans IntelliJ IDEA

IntelliJ IDEA intègre Maven par défaut. Voici plusieurs méthodes pour exécuter les tests et générer les rapports.

## Méthode 1 : Utiliser la fenêtre Maven (Recommandé)

1. **Ouvrir la fenêtre Maven** :
   - Menu : `View` → `Tool Windows` → `Maven`
   - Ou raccourci : `Alt + 1` puis sélectionner `Maven`

2. **Exécuter les tests** :
   - Dans la fenêtre Maven, développez : `TricolSupplyPro-Boot` → `Lifecycle`
   - Double-cliquez sur `test` pour exécuter tous les tests

3. **Générer le rapport JaCoCo** :
   - Dans la fenêtre Maven, développez : `TricolSupplyPro-Boot` → `Plugins` → `jacoco`
   - Double-cliquez sur `jacoco:report` pour générer le rapport
   - Le rapport sera disponible dans : `target/site/jacoco/index.html`
   - Pour l'ouvrir : Clic droit sur `target/site/jacoco/index.html` → `Open in Browser`

4. **Exécuter uniquement les tests unitaires** :
   - Dans la fenêtre Maven, cliquez sur l'icône avec un `M` (Execute Maven Goal)
   - Ou Menu : `View` → `Tool Windows` → `Maven` → Icône `Execute Maven Goal`
   - Entrez : `test -Dtest=*ServiceTest`
   - Cliquez sur `OK`

5. **Exécuter uniquement les tests d'intégration** :
   - Utilisez `Execute Maven Goal` comme ci-dessus
   - Entrez : `test -Dtest=*IntegrationTest`
   - Cliquez sur `OK`

## Méthode 2 : Utiliser le terminal intégré d'IntelliJ

1. **Ouvrir le terminal** :
   - Menu : `View` → `Tool Windows` → `Terminal`
   - Ou raccourci : `Alt + F12`

2. **Exécuter les commandes Maven** :
   ```bash
   # Exécuter tous les tests
   ./mvnw test
   
   # Exécuter uniquement les tests unitaires
   ./mvnw test -Dtest=*ServiceTest
   
   # Exécuter uniquement les tests d'intégration
   ./mvnw test -Dtest=*IntegrationTest
   
   # Générer le rapport JaCoCo
   ./mvnw test jacoco:report
   ```

   **Note** : Utilisez `mvnw` (Maven Wrapper) qui est inclus dans le projet. Si ce fichier n'existe pas, utilisez `mvn` (nécessite Maven installé).

## Méthode 3 : Créer des Run Configurations personnalisées

1. **Créer une configuration pour les tests unitaires** :
   - Menu : `Run` → `Edit Configurations...`
   - Cliquez sur `+` → `Maven`
   - Nom : `Run Unit Tests`
   - Command line : `test -Dtest=*ServiceTest`
   - Cliquez sur `OK`

2. **Créer une configuration pour les tests d'intégration** :
   - Même processus
   - Nom : `Run Integration Tests`
   - Command line : `test -Dtest=*IntegrationTest`

3. **Créer une configuration pour JaCoCo** :
   - Nom : `Generate JaCoCo Report`
   - Command line : `test jacoco:report`

4. **Exécuter les configurations** :
   - Sélectionnez la configuration dans la barre d'outils
   - Cliquez sur le bouton `Run` (ou `Shift + F10`)

## Méthode 4 : Exécuter les tests directement depuis les fichiers de test

1. **Exécuter un test individuel** :
   - Ouvrez le fichier de test (ex: `FournisseurServiceTest.java`)
   - Cliquez sur l'icône ▶️ à gauche du nom de la classe ou de la méthode
   - Ou clic droit sur le nom → `Run 'FournisseurServiceTest'`

2. **Exécuter tous les tests d'un package** :
   - Clic droit sur le dossier `service` ou `integration` dans l'explorateur de projet
   - Sélectionnez `Run 'All Tests in service'`

## Voir les résultats des tests

1. **Fenêtre Run** :
   - Après exécution, les résultats s'affichent dans la fenêtre `Run`
   - Vous verrez : nombre de tests réussis, échoués, durée d'exécution

2. **Rapport JaCoCo** :
   - Après exécution de `jacoco:report`, ouvrez le fichier :
     - `target/site/jacoco/index.html`
   - Ou dans IntelliJ : `Project` → `target` → `site` → `jacoco` → `index.html`
   - Clic droit → `Open in Browser` ou `Open with` → `Browser`

## Commandes Maven utiles

| Commande | Description |
|----------|-------------|
| `mvn test` | Exécute tous les tests |
| `mvn test -Dtest=*ServiceTest` | Exécute uniquement les tests unitaires |
| `mvn test -Dtest=*IntegrationTest` | Exécute uniquement les tests d'intégration |
| `mvn test jacoco:report` | Exécute les tests et génère le rapport JaCoCo |
| `mvn clean test` | Nettoie et exécute les tests |
| `mvn clean test jacoco:report` | Nettoie, teste et génère le rapport |

## Structure des tests créés

### Tests unitaires (service/)
- ✅ `FournisseurServiceTest.java`
- ✅ `ProduitServiceTest.java` (avec tests CUMP)
- ✅ `CommandeFournisseurServiceTest.java`
- ✅ `MouvementStockServiceTest.java`

### Tests d'intégration (integration/)
- ✅ `FournisseurControllerIntegrationTest.java`
- ✅ `ProduitControllerIntegrationTest.java` (avec tests CUMP)
- ✅ `CommandeFournisseurControllerIntegrationTest.java`
- ✅ `MouvementStockControllerIntegrationTest.java`

## Notes importantes

1. **H2** : Le projet utilise H2 pour les tests d'intégration

2. **Profil de test** : Les tests d'intégration utilisent le profil `test` défini dans `application-test.properties`

3. **Couverture JaCoCo** : Le seuil minimum de couverture est fixé à 50% dans `pom.xml`


