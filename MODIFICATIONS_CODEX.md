# RuneProfit Touch — ajout de la rentabilité

Cette version ajoute :

- l’enregistrement automatique des prix unitaires des ressources;
- la date et l’heure de chaque mise à jour;
- une pastille de fraîcheur : verte de 0 à 3 jours, jaune de 4 à 6 jours et rouge dès 7 jours;
- un coût total manuel du craft, prioritaire lorsqu’il est renseigné;
- un coefficient de brisage variable, conservé pour le calculateur de runes;
- des filtres à cocher pour les recettes de 2 à 8 cases;
- un onglet Runes avec recherche rapide et prix HDV modifiables;
- une recherche rapide et des prix modifiables dans l’onglet Ressources;
- l’import des caractéristiques minimales et maximales des équipements;
- l’estimation des runes obtenues à partir du jet moyen et du coefficient;
- le calcul de la valeur des runes, du profit et du ROI avec les prix HDV saisis;
- le masquage automatique des ressources qui ne servent à aucune recette;
- de véritables icônes vectorielles Material dans la navigation inférieure;
- les runes de résistances fixes et %, dommages élémentaires, esquives PA/PM,
  résistances critiques/poussée, dommages pièges et renvoi de dommages;
- le niveau de métier déduit des cases : 1–2=0, 3=10, 4=20, 5=40,
  6=60, 7=80 et 8=100;
- un sélecteur de serveur pour Tiliwan, Kelerog, Blair et Talok;
- des prix totalement séparés par serveur;
- des boutons d’accueil reliés aux vrais écrans;
- la suppression de l’onglet Analyse provisoire et inutilisé;
- une migration Room 4 → 5 qui conserve les prix déjà enregistrés;
- des tests unitaires pour les calculs et les seuils de fraîcheur.

La recherche sans accents reste active : `dore` trouve notamment `Doré`.
# Favoris et images

- ajout d'un onglet Favoris dans la navigation;
- ajout d'une étoile sur chaque objet craftable;
- conservation automatique des favoris sur le téléphone;
- accès au calculateur complet depuis les favoris;
- affichage de la véritable vignette de chaque équipement dans Recherche et Favoris;
- migrations Room 7 → 8 → 9 sans perte des prix enregistrés.

# Navigation et tri

- correction du bouton Accueil : il ouvre toujours l'écran d'accueil;
- ajout du choix « Profit ++ en premier » dans Recherche et Favoris;
- calcul du profit pour tous les objets avec les données du serveur sélectionné;
- affichage du profit estimé directement sur chaque fiche de la liste.

# Correction du gel d'affichage

- calcul global des profits déplacé sur un fil d'arrière-plan;
- filtres et tris exécutés hors du fil d'affichage;
- import initial de la base exécuté en arrière-plan;
- transition Recherche → Accueil vérifiée sur l'émulateur.

# Correction des images d'équipements

- réimportation unique de la base embarquée après ajout des URL d'images;
- conservation des prix, analyses et favoris existants;
- vignettes disponibles aussi après une mise à jour de l'application.
- remplacement de l'ancien serveur `s.ankama.com` par le serveur valide
  `static.ankama.com`;
- affichage des images vérifié sur l'émulateur.

# Images des ressources et nettoyage

- ajout des vignettes disponibles pour les ressources et les runes;
- ajout de 1 968 URL d'images de ressources dans la base embarquée;
- conservation uniquement des ressources réellement utilisées par une recette;
- exclusion des identifiants et types d'équipements dans l'onglet Ressources;
- vérification sur émulateur : Abranneau absent, Agathe et runes illustrées présentes.

# Icônes manquantes et recettes

- icône de remplacement automatique pour les anciennes ressources sans image officielle;
- aucun emplacement d'image vide dans Ressources ou Runes;
- ajout de la vignette de chaque ingrédient dans le détail d'une recette;
- utilisation de la même image officielle ou du même visuel de remplacement partout.
