# Stratégie de migration OpenRewrite

## État actuel

| Élément | Statut | Observation |
|---|---|---|
| Baseline Java 17 / Spring Boot 3.5.16 | En cours | Le code et les tests existent ; le résultat complet de référence reste à enregistrer |
| Configuration du plugin Maven OpenRewrite | Validé | Plugin 6.45.1 déclaré dans le POM racine |
| Sélection des recipes Java | À réaliser | Aucune recipe n'est configurée |
| Sélection des recipes Spring Boot | À réaliser | Aucune recipe n'est configurée |
| Migration Java 21 | À réaliser | Aucune transformation exécutée |
| Migration Spring Boot 4 | À réaliser | Aucune transformation exécutée |

Les statuts utilisés dans ce document sont `À réaliser`, `En cours` et `Validé`. Un statut `Validé` signifie que l'élément décrit a été constaté ou vérifié ; il ne préjuge pas du succès des expériences futures.

## Workflow cible

```mermaid
flowchart LR
    baseline["Baseline"] --> dryRun["dryRun OpenRewrite"]
    dryRun --> diff["Analyse du diff"]
    diff --> application["Application des recipes"]
    application --> compilation["Compilation"]
    compilation --> tests["Tests"]
    tests --> corrections["Corrections manuelles"]
    corrections --> validation["Validation"]
    validation --> pr["Pull Request"]
```

Le workflow commence par une baseline reproductible et verte. Le `dryRun` produit un aperçu des changements sans modifier les sources. Après revue, les recipes sont appliquées, puis le projet est compilé et testé. Les corrections manuelles portent uniquement sur les incompatibilités ou décisions que les recipes ne peuvent pas traiter de manière sûre. La Pull Request contient le diff, les résultats de tests et les métriques collectées.

À ce stade, aucune de ces étapes de migration n'a encore été exécutée.

## Expérience 1 — Java 17 vers Java 21

**Statut initial : À réaliser**

Objectifs :

- tester la recipe `UpgradeToJava21` qui sera sélectionnée ;
- identifier les modifications apportées aux POM Maven et à la configuration du compilateur ;
- identifier les modifications éventuelles du code Java ;
- compiler l'ensemble du reactor avec un JDK 21 ;
- réexécuter tous les tests automatisés et fonctionnels ;
- mesurer les transformations automatiques et les corrections manuelles.

La version exacte de l'artefact contenant la recipe, sa licence et sa configuration doivent être vérifiées avant l'expérience. La simple présence du plugin Maven ne rend pas cette recipe disponible.

## Expérience 2 — Spring Boot 3.5 vers Spring Boot 4

**Statut initial : À réaliser**

Objectifs :

- migrer le POM parent `poc-spring-boot-platform` ;
- migrer `poc-spring-boot-autoconfigure` et vérifier son mécanisme d'enregistrement ;
- migrer les starters `health` et `web` ;
- construire, puis publier ou rendre disponible une nouvelle version cohérente du parent et des starters ;
- faire consommer cette nouvelle baseline par `customer-service` ;
- migrer le code et la configuration du service ;
- analyser les changements Spring Framework et Spring Boot produits ou non couverts ;
- exécuter tous les tests de non-régression ;
- documenter les adaptations manuelles nécessaires.

Cette expérience sera menée après établissement de la baseline et clarification de l'ordre entre migration Java et migration Spring Boot.

## OpenRewrite

OpenRewrite est un moteur de transformation structurée de code et de fichiers de build. Son modèle ne repose pas sur de simples remplacements textuels.

- Le **Lossless Semantic Tree (LST)** représente la structure syntaxique et sémantique tout en conservant la mise en forme et les informations nécessaires à une réécriture fidèle.
- Une **recipe** décrit une recherche, une transformation ou une composition de transformations.
- Le **`rewrite-maven-plugin`** intègre le moteur au workflow Maven. Le repository le déclare actuellement en version 6.45.1 dans le POM racine.
- Le goal **`dryRun`** calcule les changements proposés sans les appliquer aux fichiers de travail ; il est destiné à la revue préalable.
- Le goal **`run`** applique les recipes actives aux fichiers concernés.
- Une **recipe composite** orchestre plusieurs recipes élémentaires dans un ordre défini pour représenter une politique de migration complète.
- Des **recipes internes** peuvent être développées pour encoder les conventions, starters, propriétés ou API propres à l'organisation lorsque les recipes standard ne suffisent pas.

Le POC ne configure actuellement aucune recipe active, aucun artefact de recipes et aucune exécution OpenRewrite liée au cycle Maven. Les commandes exactes seront documentées après sélection et validation des recipes.

## Stratégie entreprise envisagée

```mermaid
flowchart LR
    version["Nouvelle version<br/>Java / Spring Boot"] --> migrationSocle["Migration du socle"]
    migrationSocle --> validationSocle["Validation du socle"]
    validationSocle --> publication["Publication nouvelle version<br/>parent / starters"]
    publication --> selection["Sélection des services"]
    selection --> rewrite["OpenRewrite"]
    rewrite --> build["Build et tests"]
    build --> pr["Création de Pull Requests"]
    pr --> exceptions["Intervention humaine<br/>uniquement sur les exceptions"]
```

La plateforme est migrée et validée en premier, car elle définit les versions et capacités consommées. Une version immuable du parent, des starters et de l'auto-configuration est ensuite publiée. Les services sélectionnés sont transformés contre cette version, construits et testés avant la création de Pull Requests revues par leurs équipes responsables.

Cette stratégie cible un traitement industrialisé de plusieurs repositories, mais son orchestration n'est pas encore implémentée dans le POC.

## Sécurité et confidentialité

Le moteur OpenRewrite peut être exécuté localement par Maven. Dans ce mode, l'analyse et les modifications ont lieu dans l'environnement de build. Cette exécution implique néanmoins le téléchargement habituel d'artefacts Maven : plugin, moteur, recipes et dépendances transitives, depuis les repositories configurés par l'organisation.

Des services externes éventuels, tels que Moderne, peuvent proposer des fonctions distinctes d'inventaire, d'orchestration ou d'exécution à grande échelle. Leur utilisation peut impliquer des échanges de métadonnées ou de code selon le produit, le mode de déploiement et la configuration retenus. Ces flux doivent être analysés avec les équipes sécurité, juridique et architecture avant toute adoption.

Il ne faut pas supposer que toutes les recipes ou tous les services associés sont gratuits ou couverts par la même licence. Une analyse de licence doit être menée sur les versions exactes du moteur, du plugin, des recipes et des services réellement retenus. Cette analyse doit également couvrir leurs dépendances et les conditions d'usage en entreprise.

## Décisions à prendre après le POC

Les décisions suivantes devront être prises à partir des résultats mesurés :

- retenir OpenRewrite OSS uniquement ou compléter le dispositif avec Moderne ;
- utiliser exclusivement des recipes standard ou maintenir aussi des recipes internes ;
- définir le point d'intégration dans la CI/CD et les contrôles obligatoires ;
- choisir un mécanisme d'orchestration multi-repositories ;
- formaliser la politique de migration, les fenêtres de mise à niveau et les exceptions ;
- sélectionner les métriques opérationnelles et les seuils de succès ;
- définir une stratégie de rollback pour les changements appliqués et les versions publiées ;
- répartir les responsabilités entre l'équipe plateforme et les équipes applicatives ;
- définir les exigences de revue, de sécurité et de conformité des Pull Requests générées.

## Navigation

- [Présentation du POC](README.md)
- [Architecture du POC](architecture.md)
- [Plan de tests](plan-tests.md)

