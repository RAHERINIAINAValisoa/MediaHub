package com.mediahub.web;

import com.mediahub.config.Auth;
import com.mediahub.model.Commentaire;
import com.mediahub.model.Flash;
import com.mediahub.model.MediaItem;
import com.mediahub.model.User;
import com.mediahub.repository.CommentRepository;
import com.mediahub.repository.InteractionRepository;
import com.mediahub.repository.MediaRepository;
import com.mediahub.util.Ids;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Controller
public class MediaController {

    private static final DateTimeFormatter LONG_DATE = DateTimeFormatter.ofPattern("dd MMMM yyyy 'à' HH:mm", Locale.FRENCH);
    private static final int MAX_COMMENT_LENGTH = 1000;

    private final MediaRepository medias;
    private final InteractionRepository interactions;
    private final CommentRepository comments;

    public MediaController(MediaRepository medias, InteractionRepository interactions, CommentRepository comments) {
        this.medias = medias;
        this.interactions = interactions;
        this.comments = comments;
    }

    @GetMapping("/media/{id}")
    public String detail(@PathVariable String id, HttpServletRequest request, Model model) {
        User user = Auth.current(request);
        Optional<MediaItem> found = medias.findById(id);
        if (found.isEmpty() || (!found.get().valide() && (user == null || !user.isAdmin()))) {
            return "redirect:/galerie";
        }
        MediaItem media = found.get();
        List<Commentaire> commentaires = comments.forMedia(id);

        model.addAttribute("media", media);
        model.addAttribute("publishedAt", LONG_DATE.format(media.date()));
        model.addAttribute("commentaires", commentaires);
        model.addAttribute("liked", user != null && interactions.hasLiked(user.id(), id));
        model.addAttribute("favori", user != null && interactions.isFavori(user.id(), id));
        return "media";
    }

    // ---------- Actions AJAX : J'aime / Favoris (réponses JSON consommées par main.js) ----------

    @PostMapping("/media/{id}/like")
    @org.springframework.web.bind.annotation.ResponseBody
    public ResponseEntity<Map<String, Object>> like(@PathVariable String id, HttpServletRequest request) {
        User user = Auth.current(request);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("success", false, "error", "unauthorized"));
        }
        if (medias.findById(id).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("success", false, "error", "invalid_media"));
        }
        boolean state = interactions.toggleLike(user.id(), id);
        return ResponseEntity.ok(Map.of("success", true, "state", state, "count", interactions.likesCount(id)));
    }

    @PostMapping("/media/{id}/favori")
    @org.springframework.web.bind.annotation.ResponseBody
    public ResponseEntity<Map<String, Object>> favori(@PathVariable String id, HttpServletRequest request) {
        User user = Auth.current(request);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("success", false, "error", "unauthorized"));
        }
        if (medias.findById(id).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("success", false, "error", "invalid_media"));
        }
        return ResponseEntity.ok(Map.of("success", true, "state", interactions.toggleFavori(user.id(), id)));
    }

    // ---------- Commentaires ----------

    @PostMapping("/media/{id}/commentaires")
    public String addComment(@PathVariable String id, @RequestParam(defaultValue = "") String commentaire,
                             HttpServletRequest request, RedirectAttributes redirect) {
        User user = Auth.current(request);
        if (user == null) {
            return "redirect:/login";
        }
        if (medias.findById(id).isEmpty()) {
            return "redirect:/galerie";
        }
        String text = commentaire.trim();
        if (text.isEmpty()) {
            redirect.addFlashAttribute("flash", Flash.toastError("Le commentaire ne peut pas être vide."));
        } else if (text.length() > MAX_COMMENT_LENGTH) {
            redirect.addFlashAttribute("flash", Flash.toastError("Commentaire trop long (" + MAX_COMMENT_LENGTH + " caractères maximum)."));
        } else {
            comments.insert(Ids.next("com_"), id, user.id(), text);
            redirect.addFlashAttribute("flash", Flash.toastSuccess("Commentaire ajouté !"));
        }
        return "redirect:/media/" + id;
    }

    @PostMapping("/media/{id}/commentaires/{commentId}/supprimer")
    public String deleteComment(@PathVariable String id, @PathVariable String commentId,
                                HttpServletRequest request, RedirectAttributes redirect) {
        User user = Auth.current(request);
        if (user == null) {
            return "redirect:/login";
        }
        Optional<Commentaire> comment = comments.findById(commentId);
        // Seul l'auteur du commentaire ou un administrateur peut le supprimer
        if (comment.isPresent() && comment.get().mediaId().equals(id)
                && (user.id().equals(comment.get().userId()) || user.isAdmin())) {
            comments.delete(commentId);
            redirect.addFlashAttribute("flash", Flash.toastSuccess("Commentaire supprimé !"));
        }
        return "redirect:/media/" + id;
    }
}
