# Plan de tests officiel du POC

## Baseline avant migration

La baseline technique à établir avant toute transformation est la suivante :

| Composant | Version de référence |
|---|---:|
| Java | 17 |
| Spring Boot | 3.5.16 |
| `poc-spring-boot-platform` | 1.0.0 |
| Maven via Wrapper | 3.9.16 |

Depuis la racine du repository, la commande de référence est :

```powershell
.\mvnw.cmd clean verify
```

Les validations attendues sont :

- téléchargement et démarrage de Maven 3.9.16 par le Wrapper ;
- détection d'un JDK 17 correctement configuré ;
- construction de tous les modules du reactor ;
- compilation de la plateforme et du service avec `release` 17 ;
- résolution des starters internes depuis le reactor ;
- exécution de tous les tests par Maven Surefire 3.5.2 ;
- résultat Maven `BUILD SUCCESS` sans échec ni erreur de test.

Le succès de cette commande doit être enregistré avant la première migration. La seule présence de rapports ou d'artefacts dans des répertoires `target` ne suffit pas à qualifier toute la baseline de verte.

## Tests unitaires

### Auto-configuration

`PocPlatformAutoConfigurationTest` utilise `ApplicationContextRunner` pour charger `PocPlatformAutoConfiguration`. Il vérifie qu'un unique bean `PlatformInfo` existe, avec le nom `POC Spring Boot Platform` et la version `1.0.0`.

### `CustomerService`

`CustomerServiceTest` isole le service avec Mockito. Les cas présents vérifient :

- la conversion d'une requête en entité, l'appel au repository et la réponse produite lors d'une création ;
- la lecture et la conversion de la liste retournée par le repository.

### `CustomerController`

`CustomerControllerTest` utilise `@WebMvcTest`, MockMvc et un `CustomerService` remplacé par un mock. Les cas présents vérifient :

- la création d'un client et le statut HTTP 201 ;
- la consultation de la liste et le statut HTTP 200 ;
- le rejet d'une requête invalide avec le statut HTTP 400.

### Chargement du contexte et `PlatformInfo`

`CustomerServiceApplicationTest` utilise `@SpringBootTest`. Un test vérifie le chargement du contexte complet ; un second récupère `PlatformInfo` depuis ce contexte et contrôle son nom et sa version. Ce dernier test confirme que l'auto-configuration interne arrive bien jusqu'au service par les starters.

## Tests fonctionnels manuels

### Préparation et démarrage

Un JDK 17 doit être actif. Pour rendre les composants internes disponibles dans le repository Maven local, puis démarrer le service :

```powershell
.\mvnw.cmd install
.\mvnw.cmd -f services/customer-service/pom.xml spring-boot:run
```

Les appels suivants s'exécutent dans un second terminal PowerShell pendant que l'application écoute sur `http://localhost:8080`.

### Santé Actuator

```powershell
$health = Invoke-RestMethod -Method Get -Uri 'http://localhost:8080/actuator/health'
$health
```

Résultat attendu : réponse HTTP 200 et propriété `status` égale à `UP`.

### Création d'un client valide

```powershell
$newCustomer = @{
    firstName = 'Ada'
    lastName  = 'Lovelace'
    email     = 'ada@example.com'
} | ConvertTo-Json

$created = Invoke-RestMethod `
    -Method Post `
    -Uri 'http://localhost:8080/api/customers' `
    -ContentType 'application/json' `
    -Body $newCustomer

$created
```

Résultat attendu : réponse HTTP 201, identifiant généré et valeurs identiques à la requête.

### Consultation des clients

```powershell
$customers = Invoke-RestMethod -Method Get -Uri 'http://localhost:8080/api/customers'
$customers
```

Résultat attendu : réponse HTTP 200 et présence du client précédemment créé.

### Rejet d'un email invalide

```powershell
$invalidCustomer = @{
    firstName = 'Ada'
    lastName  = 'Lovelace'
    email     = 'email-invalide'
} | ConvertTo-Json

try {
    Invoke-RestMethod `
        -Method Post `
        -Uri 'http://localhost:8080/api/customers' `
        -ContentType 'application/json' `
        -Body $invalidCustomer
} catch {
    [int]$_.Exception.Response.StatusCode
}
```

Résultat attendu : statut HTTP 400. L'appel ne doit créer aucun client.

## Tests après migration

Après la migration vers Java 21 et Spring Boot 4, la commande `clean verify` et tous les scénarios fonctionnels manuels doivent être réexécutés avec le JDK 21 ciblé. Les résultats doivent être comparés à la baseline enregistrée, sans supposer par avance que la migration est valide.

| Test | Baseline | Après migration | Résultat attendu |
|---|---|---|---|
| `clean verify` du reactor | À enregistrer | À réaliser | Build réussi, aucun test en échec |
| Auto-configuration avec `ApplicationContextRunner` | À enregistrer | À réaliser | Un bean `PlatformInfo` conforme |
| Tests unitaires de `CustomerService` | À enregistrer | À réaliser | Tous réussis |
| Tests MVC de `CustomerController` | À enregistrer | À réaliser | Statuts et réponses inchangés |
| Chargement du contexte Spring Boot | À enregistrer | À réaliser | Contexte démarré sans erreur |
| Présence de `PlatformInfo` dans le service | À enregistrer | À réaliser | Bean présent, valeurs inchangées |
| `GET /actuator/health` | À enregistrer | À réaliser | HTTP 200, état `UP` |
| `POST /api/customers` valide | À enregistrer | À réaliser | HTTP 201, client créé |
| `GET /api/customers` | À enregistrer | À réaliser | HTTP 200, client retrouvé |
| `POST /api/customers` avec email invalide | À enregistrer | À réaliser | HTTP 400, aucune création |

## Non-régression

La migration n'est considérée comme valide que si le comportement fonctionnel observable reste équivalent. Une compilation réussie ne suffit pas : les tests automatisés doivent rester verts et les endpoints doivent conserver leurs contrats HTTP, leurs règles de validation et leurs effets sur les données.

Toute différence volontaire doit être documentée comme une décision de migration. Toute différence involontaire constitue une régression à corriger avant validation.

## Métriques du POC

Le tableau suivant doit être renseigné pour chaque expérience. Les valeurs ne sont pas encore mesurées.

| Métrique | Valeur | Méthode ou commentaire |
|---|---:|---|
| Temps de migration manuelle estimé | À mesurer | Estimation par un développeur connaissant le socle |
| Temps d'exécution OpenRewrite | À mesurer | Durée du `dryRun` et du `run` |
| Nombre de fichiers analysés | À mesurer | Statistiques OpenRewrite ou inventaire reproductible |
| Nombre de fichiers modifiés automatiquement | À mesurer | Diff Git après `run` |
| Nombre de transformations | À mesurer | Résultats des recipes et analyse du diff |
| Erreurs de compilation après Rewrite | À mesurer | Sortie Maven après transformation |
| Tests cassés | À mesurer | Rapports Surefire |
| Corrections manuelles | À mesurer | Commits ou journal de migration qualifié |
| Temps de correction manuelle | À mesurer | Mesure entre diagnostic et retour au vert |
| Taux approximatif d'automatisation | À calculer | Part des changements requis réalisés automatiquement |

## Navigation

- [Présentation du POC](README.md)
- [Architecture du POC](architecture.md)
- [Stratégie de migration OpenRewrite](migration-openrewrite.md)

