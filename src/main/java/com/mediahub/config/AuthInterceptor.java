package com.mediahub.config;

import com.mediahub.model.User;
import com.mediahub.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Set;

/**
 * Charge l'utilisateur connecté et protège les pages :
 * /upload, /favoris, /profile => connexion requise ; /admin/** => administrateur uniquement.
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    private static final Set<String> LOGIN_REQUIRED = Set.of("/upload", "/favoris", "/profile");

    private final UserRepository users;

    public AuthInterceptor(UserRepository users) {
        this.users = users;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        User user = null;
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute(Auth.SESSION_KEY) instanceof String id) {
            user = users.findById(id).filter(User::active).orElse(null);
            if (user == null) {
                session.invalidate(); // compte supprimé ou désactivé
            }
        }
        request.setAttribute(Auth.REQUEST_KEY, user);

        String path = request.getRequestURI();
        if (path.equals("/admin") || path.startsWith("/admin/")) {
            if (user == null) {
                response.sendRedirect("/login");
                return false;
            }
            if (!user.isAdmin()) {
                response.sendRedirect("/");
                return false;
            }
        } else if (LOGIN_REQUIRED.contains(path) && user == null) {
            response.sendRedirect("/login");
            return false;
        }
        return true;
    }
}
