package com.mediahub.model;

import com.mediahub.util.Urls;

import java.time.LocalDateTime;

public record Commentaire(String id, String mediaId, String userId, String texte, LocalDateTime date,
                          String auteurPrenom, String auteurNom, String auteurPhoto) {

    public String auteurNomComplet() {
        return userId == null || auteurNom == null ? "Anonyme" : auteurPrenom + " " + auteurNom;
    }

    public String auteurAvatarUrl() {
        return Urls.avatar(auteurPhoto);
    }
}
