package com.mediahub.util;

/** Construit les URLs publiques des fichiers (servis sous /uploads/**). */
public final class Urls {

    public static final String DEFAULT_AVATAR = "/assets/images/default-avatar.svg";

    private Urls() {
    }

    public static String upload(String relativePath) {
        return relativePath == null || relativePath.isBlank() ? "" : "/uploads/" + relativePath;
    }

    public static String avatar(String photo) {
        return photo == null || photo.isBlank() ? DEFAULT_AVATAR : upload(photo);
    }
}
