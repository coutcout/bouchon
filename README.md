# bouchon
[![Snapshot workflow](https://github.com/coutcout/bouchon/actions/workflows/snapshot-workflow.yml/badge.svg?branch=main)](https://github.com/coutcout/bouchon/actions/workflows/snapshot-workflow.yml)
[![codecov](https://codecov.io/gh/coutcout/bouchon/branch/main/graph/badge.svg?token=CJPS2U6W16)](https://codecov.io/gh/coutcout/bouchon)
[![Maintainability](https://api.codeclimate.com/v1/badges/ef92d97e97b2b59e4998/maintainability)](https://codeclimate.com/github/coutcout/bouchon/maintainability)

## Quick Start
### Récupération de l'image
L'image est disponible sur le [hub docker](https://hub.docker.com) à l'adresse [https://hub.docker.com/r/coutcout/bouchon](https://hub.docker.com/r/coutcout/bouchon).

Pour récupérer l'image, exécuter la commande suivante: <code>docker pull coutcout/bouchon:TAG</code>

### Configuration de l'application
#### Configurer un endpoint
##### <a name="fichier_conf_endpoint"></a>Création du fichier de définition des endpoints
Un endpoint doit être définit dans un fichier yml qui sera envoyé via le service <a href="#post_conf_endpoint">POST /config/endpoint</a>.

Il est possible de mettre plusieurs endpoint dans un même fichier yaml.

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
    <dd>Nom du sous-dossier (à l'intérieur du dossier <a href="#application_bouchon_yml">data</a>) dans lequel devront être stockées toutes les réponses</dd>
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
        Le type doit correspondre à un type de <a href="#regex">regex</a>.
    </dd>
</dl>

##### Upload du fichier de définition des endpoints
Une fois le fichier fini, l'uploader via le service <a href="#post_conf_endpoint">POST /config/endpoint</a>.

###### Rechargement des endpoints
Une fois l'upload terminé, recharger les endpoints de l'application en appelant le service <a href="#reload_endpoints">POST /config/endpoint/reload</a>.

Le bouchon est prêt à être utilisé.

#### Dossier de stockage des fichiers de données

Lorsqu'un endpoint est appelé, l'application renvoie le contenu du fichier associé au endpoint.

Les fichiers doivent être stockés dans le répertoire [data](#application_bouchon_yml).

Actuellement, les fichiers data doivent être forcément déposés à la main dans le container.

Il est possible de le faire via un mapping de dossiers.

## Services
<details><summary><a name="post_conf_endpoint"></a>Upload d'un fichier de définition de endpoint</summary>
<i><b>POST</b> /config/endpoint</i>

Service permettant d'uploader un ou plusieurs <a href="#fichier_conf_endpoint">fichiers de définition de endpoints</a>.

Paramètres dans le body de la requête:

| Nom du paramètre | Description du paramètre                                                                                                                                                                                                                                                                                         |
|------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| files            | Champs de type file, accepte plusieurs fichier yaml/yml                                                                                                                                                                                                                                                          |
| name             | Nom du fichier de définition tel qu'il sera stocké sur le serveur.<br/>Le nom du fichier sera préfixé de la date du jour au format <b>yyyyMMdd</b>.<br/>Lorsque plusieurs fichiers sont envoyés, les noms seront incrémentés.<b>Example:</b><ul><li>test</li><li>test_001</li><li>test_002</li><li>...</li></ul> |
</details>

<details><summary><a name="delete_conf_endpoint"></a>Suppression d'un fichier de définition de endpoint</summary>
<i><b>DELETE</b> /config/endpoint/{NOM_FICHIER_DEFINITION}</i>

Service permettant de supprimer un <a href="#fichier_conf_endpoint">fichier de définition de endpoints</a>.

Paramètres dans l'URL de la requête:

| Nom du paramètre       | Description du paramètre                                                       |
|------------------------|--------------------------------------------------------------------------------|
| NOM_FICHIER_DEFINITION | Nom du fichier donné via le <a href="#post_conf_endpoint">service d'upload</a> |
</details>

<details><summary><a name="get_conf_endpoint"></a>Lister les fichiers de définition de endpoint</summary>
<i><b>GET</b> /config/endpoint</i>

Service permettant de lister l'ensemble des <a href="#fichier_conf_endpoint">fichiers de définition de endpoints</a>
</details>

<details><summary><a name="reload_endpoint"></a>Recharger les endpoints</summary>
<i><b>POST</b> /config/endpoint/reload</i>

Service permettant de recharger l'ensemble des endpoints disponibles à la suite d'un <a href="#post_conf_endpoint">ajout</a>/<a href="#delete_conf_endpoint">retrait</a>/<a href="#activate_conf_endpoint">activation</a>/<a href="#deactivate_conf_endpoint">désactivation</a> de <a href="#fichier_conf_endpoint">fichier de définition de endpoints</a>.
</details>

<details><summary><a name="activate_conf_endpoint"></a>Activer un fichier de définition de endpoint</summary>
<i><b>PUT</b> /config/endpoint/{NOM_FICHIER_DEFINITION}/activate</i>

Service permettant d'activer un <a href="#fichier_conf_endpoint">fichier de définition de endpoints</a>.

Paramètres dans l'URL de la requête:

| Nom du paramètre       | Description du paramètre                                                                                                                                                                                                                                                                |
|------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| NOM_FICHIER_DEFINITION | Nom du fichier donné via le <a href="#post_conf_endpoint">service d'upload</a>.<br>Dans le cadre de d'un fichier désactivé, sur le serveur, il est suffixé d'un <b>.deactivated</b>. Pour autant, il est nécessaire de passer uniquement le nom du fichier sans extension à ce service. |
</details>

<details><summary><a name="deactivate_conf_endpoint"></a>Désactiver un fichier de définition de endpoint</summary>
<i><b>PUT</b> /config/endpoint/{NOM_FICHIER_DEFINITION}/deactivate</i>

Service permettant de désactiver un <a href="#fichier_conf_endpoint">fichier de définition de endpoints</a>.

Afin de le désactiver, un fichier est suffixé de l'extention **.deactivated**.

Paramètres dans l'URL de la requête:

| Nom du paramètre       | Description du paramètre                                                       |
|------------------------|--------------------------------------------------------------------------------|
| NOM_FICHIER_DEFINITION | Nom du fichier donné via le <a href="#post_conf_endpoint">service d'upload</a>. |
</details>

## Liste des fichiers de configurations
Les fichiers de configuration de l'application sont stockés dans le répertoire **/home/config**

<details><summary>application.yml</summary>
Fichier de configuration racine, il permet de:

* Importer les autres fichiers de configuration
* Définir le port d'exposition (par défaut:8080)

[Disponible ici](./config/application.yml)

</details>

<details><summary><a name="application_bouchon_yml"></a>application-bouchon.yml</summary>

**Profil spring**: bouchon

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

[Disponible ici](./config/application-bouchon.yml)
</details>

<details><summary><a name="regex"></a>application-custom-regex.yml</summary>

**Profil spring**: custom-regex

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

[Disponible ici](./config/application-custom-regex.yml)
</details>

<details><summary>application-log.yml</summary>

**Profil spring**: log

Ce fichier détermine les loggers utilisés ainsi que leur level pour les logs applicatifs

Par défaut, les logs sont stockés dans le répertoire **/mnt/logs**.

[Disponible ici](./config/application-log.yml)
</details>

<details><summary>application-messages.yml</summary>

**Profil spring**: messages

Ce fichier détermine l'ensemble des messages de log.

Dans un but internationalisation de l'application, il faudrait remplacer ce fichier par celui de la langue voulue.

[Disponible ici](./config/application-messages.yml)
</details>

## Développement

### Lancement de l'application

Les fichiers de configuration étant séparés de l'application, il est nécessaire de spécifier leur emplacement à l'aide de l'option jvm suivante:
>--spring.config.location=<PATH_TO_APPLICATION_YAML>

## TODO
<details><summary>Cliquez pour voir</summary>

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
  
</details>
