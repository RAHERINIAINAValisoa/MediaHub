# MediaHub — Java 21 + HTML/CSS + PostgreSQL

Version Java de MediaHub (ancienne version : PHP + fichiers JSON).

- **Java 21**, Spring Boot 3.5 (Spring MVC + Thymeleaf pour les pages HTML)
- **HTML/CSS** : les pages et la feuille de style d'origine sont conservées
- **PostgreSQL local** (accès JDBC, tables créées automatiquement)
- **Configuration par fichier `.env`**

## Prérequis

- JDK 21
- Maven : **inutile à installer**, le wrapper inclus (`mvnw` / `mvnw.cmd`) télécharge Maven automatiquement
- PostgreSQL installé en local

## Installation

1. Créer la base :

   ```sql
   CREATE DATABASE mediahub;
   ```

2. Créer le fichier `.env` à la racine du projet :

   ```bash
   cp .env.example .env
   ```

   Puis renseigner `DB_USER` et `DB_PASSWORD` (format `CLE=valeur`, sans guillemets).

3. Lancer l'application :

   ```bash
   # Windows (PowerShell / CMD)
   .\mvnw.cmd spring-boot:run

   # Linux / macOS
   ./mvnw spring-boot:run
   ```

4. Ouvrir <http://localhost:8080>

Au premier démarrage (table `users` vide) :

- `SEED_DEMO_DATA=true` : les données de l'ancienne version sont importées (6 comptes, 15 médias, commentaires, J'aime, favoris). Les fichiers correspondants sont déjà dans `uploads/`. Les mots de passe existants (hash bcrypt PHP) restent valables ; le compte administrateur est `admin@mediahub.com` avec le mot de passe de l'ancienne version (`AdminPass123!`), **à changer depuis « Mon profil »**.
- `SEED_DEMO_DATA=false` : seul le compte administrateur défini par `ADMIN_EMAIL` / `ADMIN_PASSWORD` est créé (mot de passe aléatoire affiché dans les logs si `ADMIN_PASSWORD` est vide).

## Variables du `.env`

| Variable | Rôle | Défaut |
|---|---|---|
| `DB_HOST`, `DB_PORT`, `DB_NAME` | Connexion PostgreSQL | `localhost`, `5432`, `mediahub` |
| `DB_USER`, `DB_PASSWORD` | Identifiants PostgreSQL | `postgres`, (vide) |
| `SERVER_PORT` | Port HTTP | `8080` |
| `UPLOAD_DIR` | Dossier des fichiers téléversés | `./uploads` |
| `MAX_FILE_SIZE` | Taille maximale d'un fichier | `20MB` |
| `SEED_DEMO_DATA` | Import des données de l'ancienne version | `true` |
| `ADMIN_EMAIL`, `ADMIN_PASSWORD` | Compte administrateur (si `SEED_DEMO_DATA=false`) | `admin@mediahub.com`, (vide) |

Les variables d'environnement du système sont prioritaires sur le `.env`. Le fichier `.env` est ignoré par Git (`.gitignore`).

## Structure

```
src/main/java/com/mediahub/
  config/       session, protection des pages, service des fichiers /uploads
  model/        User, MediaItem, Commentaire, Flash
  repository/   accès PostgreSQL (JdbcTemplate)
  service/      stockage des fichiers, utilisateurs, import initial
  web/          contrôleurs (accueil, galerie, média, upload, favoris, profil, admin)
  util/         identifiants, URLs, vérification du contenu des fichiers
src/main/resources/
  schema.sql    tables (users, medias, commentaires, likes, favoris)
  seed.sql      données importées de l'ancienne version
database/mediahub.sql   schéma + données dans un seul fichier SQL (psql / pgAdmin)
  templates/    pages HTML (Thymeleaf)
  static/assets/  css, js, images
uploads/        fichiers téléversés (hors du jar)
```

## Correspondance avec l'ancienne version

| PHP | Java |
|---|---|
| `index.php` | `GET /` |
| `pages/galerie.php` | `GET /galerie?q=&cat=&sort=` |
| `pages/media.php` | `GET /media/{id}` + `POST /media/{id}/like`, `/favori`, `/commentaires` |
| `pages/login.php`, `register.php`, `logout.php` | `/login`, `/register`, `POST /logout` |
| `pages/upload.php` | `/upload` |
| `pages/favoris.php` | `/favoris` |
| `pages/profile.php` | `/profile` |
| `pages/dashboard.php` | `/admin` |

## Production

```bash
./mvnw clean package      # Windows : .\mvnw.cmd clean package
java -jar target/mediahub-1.0.0.jar
```

Lancer la commande depuis le dossier qui contient le `.env`.

## Dépannage

| Message | Cause | Solution |
|---|---|---|
| `Le terme 'mvn' n'est pas reconnu...` | Maven non installé | Utiliser `.\mvnw.cmd` à la place de `mvn` |
| `Le terme 'java' n'est pas reconnu...` | JDK 21 non installé ou absent du PATH | Installer un JDK 21, rouvrir le terminal, vérifier avec `java -version` |
| `JAVA_HOME ... not defined correctly` | `JAVA_HOME` incorrect | Le faire pointer vers le dossier du JDK 21 |
| `Le terme 'psql' n'est pas reconnu...` | PostgreSQL absent du PATH | Utiliser pgAdmin, ou ajouter `...\PostgreSQL\<version>\bin` au PATH |
| `password authentication failed` | Mauvais `DB_USER` / `DB_PASSWORD` dans `.env` | Corriger le `.env` |
| `database "mediahub" does not exist` | Base non créée | `CREATE DATABASE mediahub;` |
