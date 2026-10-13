package com.mediahub.util;

import java.util.UUID;

/** Génère des identifiants lisibles du type "usr_1a2b3c4d5e6f" (même format que l'ancienne version). */
public final class Ids {

    private Ids() {
    }

    public static String next(String prefix) {
        return prefix + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
    }
}
