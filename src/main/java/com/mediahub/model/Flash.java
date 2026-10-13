package com.mediahub.model;

/** Notification affichée avec SweetAlert2 après une action (modale ou "toast"). */
public record Flash(String type, String title, String text, boolean toast) {

    public static Flash success(String title, String text) {
        return new Flash("success", title, text, false);
    }

    public static Flash error(String title, String text) {
        return new Flash("error", title, text, false);
    }

    public static Flash toastSuccess(String title) {
        return new Flash("success", title, "", true);
    }

    public static Flash toastError(String title) {
        return new Flash("error", title, "", true);
    }
}
