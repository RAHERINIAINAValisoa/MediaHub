package com.mediahub.repository;

import com.mediahub.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {

    private static final RowMapper<User> MAPPER = (rs, i) -> new User(
            rs.getString("id"), rs.getString("nom"), rs.getString("prenom"), rs.getString("email"),
            rs.getString("mot_de_passe"), rs.getString("photo"), rs.getString("role"), rs.getBoolean("active"));

    private final JdbcTemplate jdbc;

    public UserRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Optional<User> findById(String id) {
        return jdbc.query("SELECT * FROM users WHERE id = ?", MAPPER, id).stream().findFirst();
    }

    public Optional<User> findByEmail(String email) {
        return jdbc.query("SELECT * FROM users WHERE LOWER(email) = LOWER(?)", MAPPER, email.trim())
                .stream().findFirst();
    }

    public List<User> findAll() {
        return jdbc.query("SELECT * FROM users ORDER BY created_at, nom", MAPPER);
    }

    public long count() {
        Long n = jdbc.queryForObject("SELECT COUNT(*) FROM users", Long.class);
        return n == null ? 0 : n;
    }

    public boolean emailTakenByOther(String email, String userId) {
        Integer n = jdbc.queryForObject(
                "SELECT COUNT(*) FROM users WHERE LOWER(email) = LOWER(?) AND id <> ?", Integer.class, email.trim(), userId);
        return n != null && n > 0;
    }

    public void insert(String id, String nom, String prenom, String email, String hash, String role) {
        jdbc.update("INSERT INTO users (id, nom, prenom, email, mot_de_passe, role, active) VALUES (?, ?, ?, ?, ?, ?, TRUE)",
                id, nom.trim(), prenom.trim(), email.trim().toLowerCase(), hash, role);
    }

    public void updateProfile(String id, String nom, String prenom, String email, String newPhoto, String newHash) {
        jdbc.update("""
                UPDATE users SET nom = ?, prenom = ?, email = ?,
                       photo = COALESCE(?, photo),
                       mot_de_passe = COALESCE(?, mot_de_passe)
                WHERE id = ?""",
                nom.trim(), prenom.trim(), email.trim().toLowerCase(), newPhoto, newHash, id);
    }

    public void toggleActive(String id) {
        jdbc.update("UPDATE users SET active = NOT active WHERE id = ?", id);
    }

    public void toggleRole(String id) {
        jdbc.update("UPDATE users SET role = CASE WHEN role = 'admin' THEN 'user' ELSE 'admin' END WHERE id = ?", id);
    }

    public void delete(String id) {
        jdbc.update("DELETE FROM users WHERE id = ?", id);
    }
}
