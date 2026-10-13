CREATE TABLE IF NOT EXISTS users (
    id           VARCHAR(32)  PRIMARY KEY,
    nom          VARCHAR(100) NOT NULL,
    prenom       VARCHAR(100) NOT NULL,
    email        VARCHAR(255) NOT NULL,
    mot_de_passe VARCHAR(100) NOT NULL,
    photo        VARCHAR(255),
    role         VARCHAR(10)  NOT NULL DEFAULT 'user' CHECK (role IN ('user', 'admin')),
    active       BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at   TIMESTAMP    NOT NULL DEFAULT NOW()
);
CREATE UNIQUE INDEX IF NOT EXISTS ux_users_email ON users (LOWER(email));

CREATE TABLE IF NOT EXISTS medias (
    id               VARCHAR(32)  PRIMARY KEY,
    titre            VARCHAR(255) NOT NULL,
    description      TEXT         NOT NULL DEFAULT '',
    categorie        VARCHAR(10)  NOT NULL CHECK (categorie IN ('image', 'video', 'audio')),
    fichier          VARCHAR(255) NOT NULL,
    auteur_id        VARCHAR(32)  REFERENCES users (id) ON DELETE SET NULL,
    date_publication TIMESTAMP    NOT NULL DEFAULT NOW(),
    valide           BOOLEAN      NOT NULL DEFAULT TRUE
);
CREATE INDEX IF NOT EXISTS ix_medias_date ON medias (date_publication DESC);
CREATE INDEX IF NOT EXISTS ix_medias_auteur ON medias (auteur_id);

CREATE TABLE IF NOT EXISTS commentaires (
    id               VARCHAR(32)  PRIMARY KEY,
    media_id         VARCHAR(32)  NOT NULL REFERENCES medias (id) ON DELETE CASCADE,
    user_id          VARCHAR(32)  REFERENCES users (id) ON DELETE SET NULL,
    texte            TEXT         NOT NULL,
    date_commentaire TIMESTAMP    NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS ix_commentaires_media ON commentaires (media_id);

CREATE TABLE IF NOT EXISTS likes (
    user_id  VARCHAR(32) NOT NULL REFERENCES users (id)  ON DELETE CASCADE,
    media_id VARCHAR(32) NOT NULL REFERENCES medias (id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, media_id)
);
CREATE INDEX IF NOT EXISTS ix_likes_media ON likes (media_id);

CREATE TABLE IF NOT EXISTS favoris (
    user_id  VARCHAR(32) NOT NULL REFERENCES users (id)  ON DELETE CASCADE,
    media_id VARCHAR(32) NOT NULL REFERENCES medias (id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, media_id)
);
