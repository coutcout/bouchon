# bouchon

[![codecov](https://codecov.io/gh/coutcout/bouchon/branch/develop/graph/badge.svg?token=CJPS2U6W16)](https://codecov.io/gh/coutcout/bouchon)

## Utilisation

### Démarrage
#### Récupération de l'image
L'image est disponible sur le [hub docker](https://hub.docker.com) à l'adresse [https://hub.docker.com/r/coutcout/bouchon](https://hub.docker.com/r/coutcout/bouchon).

Pour récupérer l'image, exécuter la commande suivante: <code>docker pull coutcout/bouchon:TAG</code>

#### Dossier de stockage des fichiers de données

Lorsqu'un endpoint est appelé, l'application renvoie le contenu du fichier associé au endpoint.

Les fichiers doivent être stockés dans le répertoire ['data'](#data_folder)

#### Configuration de l'application
##### Configurer un endpoint
Un endpoint doit être définit dans un fichier yml qui sera envoyé via le service POST /config/endpoint.

```yaml
- name: test1
  folderName: folder1
  requestParameterPlace: URL
  urlTemplate: url{numberA}{title}
  fileTemplate: file{numberA}{title}
  parameters:
    - tag: numberA
      type: number
    - tag: title
      type: string
```

> Dans l'exemple ci-dessus, un appel sur l'url .../url234hello devra retourné le contenu du fichier file234hello dans le sous-dossier folder1 du répertoire data.
>
> Le paramètre numberA correspond à 123.
> 
> Le paramètre titre correspond à hello.
<dl>
    <dt>name</dt>
    <dd>Nom du endpoint</dd> 
    <dt>folderName</dt>
    <dd>Nom du sous-dossier (à l'intérieur du dossier <a href="#data_folder">data</a>) dans lequel devront être stockées toutes les réponses</dd>
    <dt>requestParameterPlace</dt>
    <dd>
        Emplacement des paramètres de la requête.<br/>
        Deux choix possibles:
        <ul>
            <li>URL: Les paramètres sont à récupérer dans l'URL de la requêtes</li>
            <li><b>Indisonible pour le moment</b> - <i>BODY: Les paramètres sont à récupérer dans le body de la requêtes</i></li>
        </ul>
    </dd>
    <dt>urlTemplate</dt>
    <dd>
        Template de l'URL qui devra être bouchonnée.<br/>
        Les paramètres du bouchon doivent être de la forme {nom_param} et doivent exister dans le template du nom du fichier
    </dd>
    <dt>fileTemplate</dt>
    <dd>
        Template du nom du fichier qui devra être retourné à l'appel de l'URL bouchonnée.<br/>
        Les paramètres du bouchon doivent être de la forme {nom_param} et doivent exister dans le template du nom du fichier
    </dd>
    <dt>parameters</dt>
    <dd>
        Liste des paramètres qui sont utilisé dans les templates avec leur type.<br/>
        Le type doit correspondre à un type de <a href="">regex</a>.
    </dd>
</dl>


##### Liste des fichiers de configurations
Les fichiers de configuration de l'application sont stockés dans le répertoire **/home/config**

###### application.yml
Fichier de configuration racine, il permet de:
* Importer les autres fichiers de configuration
* Définir le port d'exposition (par défaut:8080)

###### application-bouchon.yml
Profil spring: bouchon

Ce fichier détermine deux propriétés:
* <a name="data_folder"></a>bouchon.folder.data
    > Dossier dans lequel les fichiers renvoyés par le bouchon doivent être stockés.
    > 
    > Chaque endpoint dispose de son sous-dossier défini dans le fichier de configuration du endpoint.
    >
    > Valeur par défaut: **/mnt/data** 
* bouchon.folder.config
    > Dossier dans lequel les fichiers de configuration des endpoints seront stockés
    >
    > Les fichiers de configuration peuvent être uploader soit par mapping de dossier entre le container et la machine host, soit par le service **POST /config/endpoint**.
    > 
    > Valeur par défaut: **/home/bouchon/config** 

###### application-custom-regex.yml
Profil spring: custom-regex

Ce fichier décrit des potentielles regex personnalisées sous la propriété **bouchon.regex**.

Chaque regex devra être définie de la manière suivante: <code>nom_regex: regex</code>
> exemple
> 
> bouchon.regex:
>   regex1: "\d{2}"

Par défaut, des regex existent déjà:
* Dans l'application:

| Nom de la regex | Regex                      |
|-----------------|----------------------------|
| string          | <code>\w+</code>           |
| number          | <code>\d+</code>           |
| boolean         | <code>true\|false</code>   |

* Dans le fichier application-custom-regex.yml

| Nom de la regex | Regex                             |
|-----------------|-----------------------------------|
| date-yyyymmdd   | <code>\d{8}</code>                |
| date-yyyy-mm-dd | <code>\d{4}-\d{2}-\d{2}</code>    |

Pour rajouter des regex, il est donc nécessaire de remplacer le fichier application-custom-regex.yml.

###### application-log.yml
Profil spring: log

Ce fichier détermine les loggers utilisés ainsi que leur level pour les logs applicatifs

Par défaut, les logs sont stockés dans le répertoire **/mnt/logs**.

###### application-messages.yml
Profil spring: messages

Ce fichier détermine l'ensemble des messages de log.

Dans un but d'internationnalisation de l'application, il faudrait remplacer ce fichier par celui de la langue voulue.

## Développement

### Lancement de l'application

Les fichiers de configuration étant séparés de l'application, il est nécessaire de spécifier leur emplacement à l'aide de l'option jvm suivante:
>--spring.config.location=<PATH_TO_APPLICATION_YAML>
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
  
* ~~Gestions des appels en POST avec paramètres dans le body en JSON~~
  
* ~~Chargement d'endpoints à partir de fichiers yaml dans un dossier~~
    * ~~Paramétrage du dossier~~
    * ~~Lecture du dossier~~
    * ~~Chargement d'un fichier~~
    * ~~Vérification du fichier~~
    * ~~Parsing des endpoints~~
    * ~~Chargement des endpoints~~
    * ~~Chargement des fichiers au démarrage~~
  
*  ~~Création d'un service de manipulation des fichiers d'endpoints~~
    * ~~Service qui liste les fichiers disponibles~~
    * ~~Service qui supprime un fichier~~
    * ~~Service qui envoie un fichier~~
      * ~~Possibilité d'envoyer plusieurs fichiers~~
      * ~~Lecture du fichier~~
    * ~~Service qui recharge les endpoints à partir du dossier de configuration~~
    * ~~Service qui désactive un fichier de endpoint~~
  
* ~~Retrait de la création des endpoints via le fichier de propriétés~~

* ~~Dockerisation de l'application~~
  * ~~Création du dockerfile~~
  * ~~Publication de l'image~~
  
### 1.0.1

* ~~Création des TUs~~

### 1.2.0

* ~~Implémation d'un CI/CD~~

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
  
