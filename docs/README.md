# POC OpenRewrite — Industrialisation des migrations Java et Spring Boot

## Contexte

L'organisation possède plusieurs services web Spring Boot qui reposent sur un socle commun. Ce socle regroupe un POM parent Maven, des starters Spring Boot internes, des auto-configurations internes ainsi que des règles communes de build et de qualité.

Les montées de version de Java et de Spring Boot sont réalisées régulièrement. Elles demandent d'abord une intervention sur la plateforme partagée, puis sur chacun des services consommateurs. Une partie importante de ce travail consiste à reproduire les mêmes changements de configuration et de code dans plusieurs repositories.

Le présent repository reproduit cet écosystème à une échelle réduite : une plateforme Maven/Spring Boot et un service consommateur sont réunis dans un même reactor Maven afin de permettre une expérimentation contrôlée.

## Problématique

Le processus actuel présente plusieurs difficultés :

- le coût des migrations répétitives augmente avec le nombre de services ;
- des modifications similaires doivent être appliquées dans plusieurs repositories ;
- les opérations manuelles introduisent un risque d'oubli ou d'erreur humaine ;
- il est difficile de garantir une configuration homogène pour tous les services ;
- une part excessive du temps est consacrée aux modifications mécaniques plutôt qu'à l'analyse des incompatibilités réelles ;
- la reproductibilité et la traçabilité de la migration dépendent fortement des pratiques de chaque équipe.

## Objectif du POC

Le POC doit évaluer dans quelle mesure OpenRewrite peut automatiser une migration :

- de Java 17 vers Java 21 ;
- de Spring Boot 3.5 vers Spring Boot 4 ;
- d'abord sur la plateforme partagée ;
- ensuite sur les services web qui consomment cette plateforme.

L'évaluation doit distinguer explicitement :

- les transformations entièrement automatisables ;
- les transformations qui nécessitent une décision ou une intervention humaine ;
- les validations pouvant être automatisées par le build et les tests ;
- les limitations techniques observées ;
- les contraintes de licence des recipes et services effectivement retenus ;
- les conditions d'une future intégration dans la chaîne CI/CD.

À l'état actuel, le plugin Maven OpenRewrite est configuré à la racine, mais aucune recipe de migration n'est déclarée et aucune migration n'a été exécutée.

## Critères de succès

Le POC sera considéré concluant si les résultats suivants peuvent être démontrés et mesurés :

1. le build complet avant migration est vert avec Java 17 et Spring Boot 3.5.16 ;
2. tous les tests automatisés avant migration sont verts ;
3. l'application répond conformément aux scénarios fonctionnels avant migration ;
4. les recipes OpenRewrite sélectionnées peuvent être appliquées de manière contrôlée ;
5. le projet compile après migration avec Java 21 et Spring Boot 4 ;
6. tous les tests automatisés sont réexécutés après migration ;
7. le comportement fonctionnel couvert reste équivalent ;
8. les modifications manuelles restantes sont identifiées et qualifiées ;
9. le nombre de fichiers et de transformations traités automatiquement est mesuré ;
10. la migration peut être reproduite à partir d'un état propre ;
11. la même démarche peut être appliquée à plusieurs services sans réécriture complète du processus.

Ces critères constituent des objectifs. Ils ne sont pas encore tous validés à ce stade du POC.

## Hors portée initiale

La première phase du POC ne couvre pas :

- la migration massive de tous les services de production ;
- un déploiement en production ;
- le remplacement des tests existants par OpenRewrite ;
- la génération automatique complète de tests métier ;
- l'utilisation de l'intelligence artificielle comme mécanisme principal de migration ;
- la résolution automatique de toutes les incompatibilités fonctionnelles ou architecturales.

## Documentation

- [Architecture du POC](architecture.md)
- [Plan de tests](plan-tests.md)
- [Stratégie de migration OpenRewrite](migration-openrewrite.md)

