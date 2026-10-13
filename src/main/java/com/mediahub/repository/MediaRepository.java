package com.mediahub.repository;

import com.mediahub.model.MediaItem;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class MediaRepository {

    private static final String BASE = """
            SELECT m.id, m.titre, m.description, m.categorie, m.fichier, m.auteur_id, m.date_publication, m.valide,
                   u.prenom AS auteur_prenom, u.nom AS auteur_nom, u.photo AS auteur_photo,
                   (SELECT COUNT(*) FROM likes l WHERE l.media_id = m.id) AS likes_count
            FROM medias m
            LEFT JOIN users u ON u.id = m.auteur_id
            """;

    private static final RowMapper<MediaItem> MAPPER = (rs, i) -> new MediaItem(
            rs.getString("id"), rs.getString("titre"), rs.getString("description"), rs.getString("categorie"),
            rs.getString("fichier"), rs.getString("auteur_id"), rs.getTimestamp("date_publication").toLocalDateTime(),
            rs.getBoolean("valide"), rs.getString("auteur_prenom"), rs.getString("auteur_nom"),
            rs.getString("auteur_photo"), rs.getInt("likes_count"));

    private final JdbcTemplate jdbc;

    public MediaRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /** Recherche publique : seuls les médias validés, filtrés par texte, catégorie et tri. */
    public List<MediaItem> searchPublic(String q, String categorie, String sort) {
        StringBuilder sql = new StringBuilder(BASE).append(" WHERE m.valide = TRUE");
        List<Object> params = new ArrayList<>();

        if (q != null && !q.isBlank()) {
            String like = "%" + q.trim().replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_") + "%";
            sql.append(" AND (m.titre ILIKE ? OR m.description ILIKE ?")
               .append(" OR (COALESCE(u.prenom, '') || ' ' || COALESCE(u.nom, '')) ILIKE ?)");
            params.add(like);
            params.add(like);
            params.add(like);
        }
        if (categorie != null && !categorie.equals("all")) {
            sql.append(" AND m.categorie = ?");
            params.add(categorie);
        }
        sql.append(switch (sort == null ? "recent" : sort) {
            case "oldest" -> " ORDER BY m.date_publication ASC";
            case "popular" -> " ORDER BY likes_count DESC, m.date_publication DESC";
            default -> " ORDER BY m.date_publication DESC";
        });
        return jdbc.query(sql.toString(), MAPPER, params.toArray());
    }

    public List<MediaItem> trending(int limit) {
        return jdbc.query(BASE + " WHERE m.valide = TRUE ORDER BY likes_count DESC, m.date_publication DESC LIMIT ?",
                MAPPER, limit);
    }

    public List<MediaItem> findAll() {
        return jdbc.query(BASE + " ORDER BY m.date_publication DESC", MAPPER);
    }

    public Optional<MediaItem> findById(String id) {
        return jdbc.query(BASE + " WHERE m.id = ?", MAPPER, id).stream().findFirst();
    }

    public List<MediaItem> findByAuthor(String userId) {
        return jdbc.query(BASE + " WHERE m.auteur_id = ?", MAPPER, userId);
    }

    public List<MediaItem> favorisOf(String userId) {
        return jdbc.query(BASE + " JOIN favoris f ON f.media_id = m.id WHERE f.user_id = ? AND m.valide = TRUE"
                + " ORDER BY m.date_publication DESC", MAPPER, userId);
    }

    public void insert(String id, String titre, String description, String categorie, String fichier, String auteurId) {
        jdbc.update("""
                INSERT INTO medias (id, titre, description, categorie, fichier, auteur_id, date_publication, valide)
                VALUES (?, ?, ?, ?, ?, ?, NOW(), TRUE)""",
                id, titre.trim(), description == null ? "" : description.trim(), categorie, fichier, auteurId);
    }

    public void update(String id, String titre, String description, boolean valide) {
        jdbc.update("UPDATE medias SET titre = ?, description = ?, valide = ? WHERE id = ?",
                titre.trim(), description == null ? "" : description.trim(), valide, id);
    }

    public void toggleValide(String id) {
        jdbc.update("UPDATE medias SET valide = NOT valide WHERE id = ?", id);
    }

    public void delete(String id) {
        jdbc.update("DELETE FROM medias WHERE id = ?", id);
    }

    public Map<String, Integer> countByCategorie() {
        Map<String, Integer> result = new LinkedHashMap<>(Map.of("image", 0, "video", 0, "audio", 0));
        jdbc.query("SELECT categorie, COUNT(*) AS n FROM medias GROUP BY categorie",
                rs -> {
                    result.put(rs.getString("categorie"), rs.getInt("n"));
                });
        return result;
    }
}
