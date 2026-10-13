package com.mediahub.model;

import com.mediahub.util.Urls;

public record User(String id, String nom, String prenom, String email, String motDePasse,
                   String photo, String role, boolean active) {

    public boolean isAdmin() {
        return "admin".equals(role);
    }

    public String fullName() {
        return prenom + " " + nom;
    }

    public String photoUrl() {
        return Urls.avatar(photo);
    }
}
