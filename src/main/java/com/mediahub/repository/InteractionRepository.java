package com.mediahub.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/** "J'aime" et favoris : même mécanique de bascule (ajout / retrait). */
@Repository
public class InteractionRepository {

    private final JdbcTemplate jdbc;

    public InteractionRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /** @return true si le "J'aime" vient d'être ajouté, false s'il vient d'être retiré. */
    public boolean toggleLike(String userId, String mediaId) {
        return toggle("likes", userId, mediaId);
    }

    public boolean toggleFavori(String userId, String mediaId) {
        return toggle("favoris", userId, mediaId);
    }

    public boolean hasLiked(String userId, String mediaId) {
        return exists("likes", userId, mediaId);
    }

    public boolean isFavori(String userId, String mediaId) {
        return exists("favoris", userId, mediaId);
    }

    public int likesCount(String mediaId) {
        Integer n = jdbc.queryForObject("SELECT COUNT(*) FROM likes WHERE media_id = ?", Integer.class, mediaId);
        return n == null ? 0 : n;
    }

    // Le nom de table provient uniquement des deux constantes ci-dessus (jamais de l'utilisateur).
    private boolean toggle(String table, String userId, String mediaId) {
        int removed = jdbc.update("DELETE FROM " + table + " WHERE user_id = ? AND media_id = ?", userId, mediaId);
        if (removed > 0) {
            return false;
        }
        jdbc.update("INSERT INTO " + table + " (user_id, media_id) VALUES (?, ?) ON CONFLICT DO NOTHING", userId, mediaId);
        return true;
    }

    private boolean exists(String table, String userId, String mediaId) {
        Integer n = jdbc.queryForObject(
                "SELECT COUNT(*) FROM " + table + " WHERE user_id = ? AND media_id = ?", Integer.class, userId, mediaId);
        return n != null && n > 0;
    }
}
