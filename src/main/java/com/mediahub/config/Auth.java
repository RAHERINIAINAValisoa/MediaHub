package com.mediahub.config;

import com.mediahub.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/** Accès à l'utilisateur connecté (stocké en session, rechargé depuis la base à chaque requête). */
public final class Auth {

    static final String SESSION_KEY = "userId";
    static final String REQUEST_KEY = "currentUser";

    private Auth() {
    }

    /** @return l'utilisateur connecté, ou null. */
    public static User current(HttpServletRequest request) {
        return (User) request.getAttribute(REQUEST_KEY);
    }

    public static void login(HttpServletRequest request, User user) {
        HttpSession session = request.getSession(true);
        request.changeSessionId(); // protège contre la fixation de session
        request.getSession().setAttribute(SESSION_KEY, user.id());
        request.setAttribute(REQUEST_KEY, user);
    }

    public static void logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        request.removeAttribute(REQUEST_KEY);
    }
}
