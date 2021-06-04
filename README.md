# bouchon

## TODO

### 1.0.0

* ~~Initialisation des endpoints~~
    * ~~Création des répertoires s'ils n'existent pas~~
    * ~~Création du mécanisme de regex paramétrable~~
    * ~~Création d'une liste de paramètres commun à l'url et au fichier~~
        * ~~Vérification de la validité de l'URL~~
        * ~~Vérification de la validité du template de fichier~~
        * ~~Comparaison des paramètres de l'URL et du Fichier~~
    * ~~Création de la regex de l'url à partir du template de l'url~~
    
* ~~Appel d'un endpoint (GET)~~
    * ~~Parsing de l'url à partir de la regex pour récupérer les paramètres~~
    * ~~Génération du nom du fichier recherché~~
    * ~~Récupération du fichier~~
    * ~~Renvoi du fichier~~
  
* Chargement d'endpoints à partir de fichiers yaml dans un dossier
    * ~~Paramétrage du dossier~~
    * Lecture du dossier
    * Chargement d'un fichier
    * ~~Vérification du fichier~~
    * ~~Parsing des endpoints~~
    * ~~Chargement des endpoints~~
    * Chargement des fichiers au démarrage
  
* Création d'un service de manipulation des fichiers d'endpoints
    * Service qui liste les fichiers disponibles
    * Service qui supprime un fichier
    * ~~Service qui envoie un fichier~~
      * ~~Possibilité d'envoyer plusieurs fichiers~~
      * ~~Lecture du fichier~~
    * Service qui recharge les endpoints à partir du dossier de configuration
    * Service qui désactive un fichier de endpoint
  
* Retrait de la création des endpoints via le fichier de propriétés

* ~~Dockerisation de l'application~~
  * ~~Création du dockerfile~~
  * ~~Publication de l'image~~
  
### 1.0.1

* Création des TUs

### 1.2.0

* Implémation d'un CI/CD

### 1.3.0

* Service de dépot d'un fichier de données
    * Déposer un fichier dans le dossier du endpoint
    * Ecrasement du fichier si déjà existant
    * Création de la regex du nom de fichier à partir du template du fichier
    * Vérifier le nom du fichier avec la regex
    * Gestion des erreurs

### 1.4.0

* Service de suppression d'un fichier de données
    * Suppression du fichier dans le bon endpoint
    * Gestion des erreur
  