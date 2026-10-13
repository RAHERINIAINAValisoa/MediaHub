package com.mediahub.config;

import com.mediahub.model.Flash;
import com.mediahub.model.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.support.RequestContextUtils;

@ControllerAdvice
public class GlobalControllerAdvice {

    private final String maxFileSize;

    public GlobalControllerAdvice(@Value("${spring.servlet.multipart.max-file-size}") String maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    /** Utilisateur connecté, disponible dans tous les templates sous le nom "user" (absent si visiteur). */
    @ModelAttribute("user")
    public User user(HttpServletRequest request) {
        return Auth.current(request);
    }

    /** Chemin courant, utilisé pour surligner le lien actif du menu. */
    @ModelAttribute("currentPath")
    public String currentPath(HttpServletRequest request) {
        return request.getRequestURI();
    }

    /** Fichier plus gros que la limite MAX_FILE_SIZE : retour au formulaire avec un message clair. */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String fileTooLarge(HttpServletRequest request) {
        RequestContextUtils.getOutputFlashMap(request).put("flash",
                Flash.error("Fichier trop volumineux", "Le fichier dépasse la taille maximale autorisée (" + maxFileSize + ")."));
        return request.getRequestURI().startsWith("/profile") ? "redirect:/profile" : "redirect:/upload";
    }
}
