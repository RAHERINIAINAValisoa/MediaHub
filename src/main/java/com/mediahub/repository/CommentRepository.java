package com.mediahub.repository;

import com.mediahub.model.Commentaire;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CommentRepository {

    private static final String BASE = """
            SELECT c.id, c.media_id, c.user_id, c.texte, c.date_commentaire,
                   u.prenom AS auteur_prenom, u.nom AS auteur_nom, u.photo AS auteur_photo
            FROM commentaires c
            LEFT JOIN users u ON u.id = c.user_id
            """;

    private static final RowMapper<Commentaire> MAPPER = (rs, i) -> new Commentaire(
            rs.getString("id"), rs.getString("media_id"), rs.getString("user_id"), rs.getString("texte"),
            rs.getTimestamp("date_commentaire").toLocalDateTime(),
            rs.getString("auteur_prenom"), rs.getString("auteur_nom"), rs.getString("auteur_photo"));

    private final JdbcTemplate jdbc;

    public CommentRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Commentaire> forMedia(String mediaId) {
        return jdbc.query(BASE + " WHERE c.media_id = ? ORDER BY c.date_commentaire DESC", MAPPER, mediaId);
    }

    public Optional<Commentaire> findById(String id) {
        return jdbc.query(BASE + " WHERE c.id = ?", MAPPER, id).stream().findFirst();
    }

    public void insert(String id, String mediaId, String userId, String texte) {
        jdbc.update("INSERT INTO commentaires (id, media_id, user_id, texte, date_commentaire) VALUES (?, ?, ?, ?, NOW())",
                id, mediaId, userId, texte);
    }

    public void delete(String id) {
        jdbc.update("DELETE FROM commentaires WHERE id = ?", id);
    }

    public long count() {
        Long n = jdbc.queryForObject("SELECT COUNT(*) FROM commentaires", Long.class);
        return n == null ? 0 : n;
    }
}
