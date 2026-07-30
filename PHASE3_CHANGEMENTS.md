# Dofus Touch Tool — Phase 3

- Sélection multiple des métiers dans Recherche.
- Tuiles fantasy dédiées pour chaque métier, sans icônes Android Material.
- Tailleur représenté par un chapeau.
- Forgeur de pelles représenté par une pelle.
- Textes principaux forcés en blanc sur les écrans Recherche et Plus.
- Fond sombre LuxuryBackground appliqué au menu Plus.
- Typographie RPG (serif) pour les titres et libellés, police lisible pour les données.
- PriceViewModel partagé entre Recherche et Favoris afin de réduire le rechargement au changement d'onglet.
- Logo et icônes Android existants conservés.

Note : compilation locale impossible dans l'environnement de génération, car Gradle 9.5 doit être téléchargé et le réseau est bloqué. Le workflow GitHub Actions doit valider la compilation.
