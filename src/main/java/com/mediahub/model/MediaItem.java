package com.mediahub.model;

import com.mediahub.util.Urls;

import java.time.LocalDateTime;

/** Un média avec les informations de son auteur et son nombre de "J'aime". */
public record MediaItem(String id, String titre, String description, String categorie, String fichier,
                        String auteurId, LocalDateTime date, boolean valide,
                        String auteurPrenom, String auteurNom, String auteurPhoto, int likes) {

    public String url() {
        return Urls.upload(fichier);
    }

    public String auteurNomComplet() {
        if (auteurId == null || auteurNom == null) {
            return "Auteur inconnu";
        }
        return auteurPrenom + " " + auteurNom;
    }

    public String auteurAvatarUrl() {
        return Urls.avatar(auteurPhoto);
    }

    public boolean isImage() {
        return "image".equals(categorie);
    }

    public boolean isVideo() {
        return "video".equals(categorie);
    }

    public boolean isAudio() {
        return "audio".equals(categorie);
    }
}
