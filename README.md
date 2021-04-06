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
    
* Appel d'un endpoint (GET)
    * ~~Parsing de l'url à partir de la regex pour récupérer les paramètres~~
    * ~~Génération du nom du fichier recherché~~
    * Récupération du fichier
    * Renvoi du fichier
  
### 1.0.1

* Création des TUs

### 1.1.0

* Dockerisation de l'application
  * Création du dockerfile
  * Publication de l'image
  
### 1.2.0

* Implémation d'un CI/CD

### 1.3.0

* Service de dépot d'un fichier
    * Déposer un fichier dans le dossier du endpoint
    * Ecrasement du fichier si déjà existant
    * Création de la regex du nom de fichier à partir du template du fichier
    * Vérifier le nom du fichier avec la regex
    * Gestion des erreurs

### 1.4.0

* Service de suppression d'un fichier
    * Suppression du fichier dans le bon endpoint
    * Gestion des erreur
  