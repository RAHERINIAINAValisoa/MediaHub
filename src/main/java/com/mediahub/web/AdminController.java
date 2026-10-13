package com.mediahub.web;

import com.mediahub.config.Auth;
import com.mediahub.model.Flash;
import com.mediahub.model.User;
import com.mediahub.repository.CommentRepository;
import com.mediahub.repository.MediaRepository;
import com.mediahub.repository.UserRepository;
import com.mediahub.service.MediaService;
import com.mediahub.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;
import java.util.Optional;

/** Tableau de bord administrateur (accès protégé par AuthInterceptor). */
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserRepository users;
    private final MediaRepository medias;
    private final CommentRepository comments;
    private final UserService userService;
    private final MediaService mediaService;

    public AdminController(UserRepository users, MediaRepository medias, CommentRepository comments,
                           UserService userService, MediaService mediaService) {
        this.users = users;
        this.medias = medias;
        this.comments = comments;
        this.userService = userService;
        this.mediaService = mediaService;
    }

    @GetMapping
    public String dashboard(@RequestParam(defaultValue = "users") String tab, Model model) {
        Map<String, Integer> byCategory = medias.countByCategorie();
        model.addAttribute("tab", "medias".equals(tab) ? "medias" : "users");
        model.addAttribute("users", users.findAll());
        model.addAttribute("medias", medias.findAll());
        model.addAttribute("totalUsers", users.count());
        model.addAttribute("totalComments", comments.count());
        model.addAttribute("countImages", byCategory.get("image"));
        model.addAttribute("countVideos", byCategory.get("video"));
        model.addAttribute("countAudios", byCategory.get("audio"));
        return "admin/dashboard";
    }

    // ---------- Utilisateurs ----------

    @PostMapping("/users/{id}/statut")
    public String toggleStatus(@PathVariable String id, HttpServletRequest request, RedirectAttributes redirect) {
        Optional<User> target = users.findById(id);
        if (isSelf(request, id)) {
            return done(redirect, "users", Flash.error("Erreur", "Vous ne pouvez pas désactiver votre propre compte administrateur."));
        }
        if (target.isEmpty()) {
            return done(redirect, "users", Flash.error("Erreur", "Utilisateur introuvable."));
        }
        users.toggleActive(id);
        return done(redirect, "users", Flash.success("Succès !",
                target.get().active() ? "Compte utilisateur désactivé." : "Compte utilisateur réactivé."));
    }

    @PostMapping("/users/{id}/role")
    public String toggleRole(@PathVariable String id, HttpServletRequest request, RedirectAttributes redirect) {
        Optional<User> target = users.findById(id);
        if (isSelf(request, id)) {
            return done(redirect, "users", Flash.error("Erreur", "Vous ne pouvez pas modifier votre propre rôle administrateur."));
        }
        if (target.isEmpty()) {
            return done(redirect, "users", Flash.error("Erreur", "Utilisateur introuvable."));
        }
        users.toggleRole(id);
        return done(redirect, "users", Flash.success("Succès !",
                "Rôle de l'utilisateur modifié en " + (target.get().isAdmin() ? "USER" : "ADMIN") + "."));
    }

    @PostMapping("/users/{id}/supprimer")
    public String deleteUser(@PathVariable String id, HttpServletRequest request, RedirectAttributes redirect) {
        Optional<User> target = users.findById(id);
        if (isSelf(request, id)) {
            return done(redirect, "users", Flash.error("Erreur", "Vous ne pouvez pas supprimer votre propre compte."));
        }
        if (target.isEmpty()) {
            return done(redirect, "users", Flash.error("Erreur", "Utilisateur introuvable."));
        }
        userService.deleteWithContent(target.get());
        return done(redirect, "users", Flash.success("Succès !",
                "Compte utilisateur et ses publications associées supprimés définitivement."));
    }

    // ---------- Médias ----------

    @PostMapping("/medias/{id}/visibilite")
    public String toggleVisibility(@PathVariable String id, RedirectAttributes redirect) {
        return medias.findById(id).map(m -> {
            medias.toggleValide(id);
            return done(redirect, "medias", Flash.success("Succès !", m.valide()
                    ? "Le média a été retiré de la galerie publique."
                    : "Le média a été approuvé et publié."));
        }).orElseGet(() -> done(redirect, "medias", Flash.error("Erreur", "Média introuvable.")));
    }

    @PostMapping("/medias/{id}/modifier")
    public String editMedia(@PathVariable String id,
                            @RequestParam(defaultValue = "") String titre,
                            @RequestParam(defaultValue = "") String description,
                            @RequestParam(required = false) String valide,
                            RedirectAttributes redirect) {
        if (titre.isBlank()) {
            return done(redirect, "medias", Flash.error("Erreur", "Le titre du média ne peut pas être vide."));
        }
        if (medias.findById(id).isEmpty()) {
            return done(redirect, "medias", Flash.error("Erreur", "Média introuvable."));
        }
        medias.update(id, titre, description, valide != null);
        return done(redirect, "medias", Flash.success("Succès !", "Informations du média modifiées."));
    }

    @PostMapping("/medias/{id}/supprimer")
    public String deleteMedia(@PathVariable String id, RedirectAttributes redirect) {
        return mediaService.delete(id)
                ? done(redirect, "medias", Flash.success("Succès !", "Média supprimé définitivement du serveur."))
                : done(redirect, "medias", Flash.error("Erreur", "Erreur lors de la suppression du média."));
    }

    // ---------- Utilitaires ----------

    private static boolean isSelf(HttpServletRequest request, String id) {
        User current = Auth.current(request);
        return current != null && current.id().equals(id);
    }

    private static String done(RedirectAttributes redirect, String tab, Flash flash) {
        redirect.addFlashAttribute("flash", flash);
        return "redirect:/admin?tab=" + tab;
    }
}
