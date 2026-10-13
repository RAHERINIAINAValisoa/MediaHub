package com.mediahub.web;

import com.mediahub.config.Auth;
import com.mediahub.model.Flash;
import com.mediahub.model.User;
import com.mediahub.repository.UserRepository;
import com.mediahub.service.FileStorageService;
import com.mediahub.service.UserService;
import com.mediahub.util.FileSniffer;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
public class ProfileController {

    private static final long MAX_AVATAR_BYTES = 5L * 1024 * 1024;

    private final UserRepository users;
    private final UserService userService;
    private final FileStorageService storage;

    public ProfileController(UserRepository users, UserService userService, FileStorageService storage) {
        this.users = users;
        this.userService = userService;
        this.storage = storage;
    }

    @GetMapping("/profile")
    public String form() {
        return "profile";
    }

    @PostMapping("/profile")
    public String update(@RequestParam(defaultValue = "") String nom,
                         @RequestParam(defaultValue = "") String prenom,
                         @RequestParam(defaultValue = "") String email,
                         @RequestParam(defaultValue = "") String password,
                         @RequestParam(name = "confirm_password", defaultValue = "") String confirmPassword,
                         @RequestParam(name = "photo", required = false) MultipartFile photo,
                         HttpServletRequest request, Model model, RedirectAttributes redirect) {
        User user = Auth.current(request);

        String error = null;
        if (nom.isBlank() || prenom.isBlank() || email.isBlank()) {
            error = "Le nom, le prénom et l'email sont obligatoires.";
        } else if (!AuthController.EMAIL.matcher(email.trim()).matches()) {
            error = "Veuillez saisir une adresse email valide.";
        } else if (!password.isEmpty() && !password.equals(confirmPassword)) {
            error = "Les nouveaux mots de passe ne correspondent pas.";
        } else if (!password.isEmpty() && password.length() < 6) {
            error = "Le mot de passe doit faire au moins 6 caractères.";
        } else if (users.emailTakenByOther(email, user.id())) {
            error = "Impossible de mettre à jour le profil. L'email est déjà utilisé.";
        }

        String newPhoto = null;
        boolean hasPhoto = photo != null && !photo.isEmpty();
        if (error == null && hasPhoto) {
            if (!storage.isValid(photo, FileSniffer.IMAGE_EXT)) {
                error = "Format d'image non supporté (JPEG, PNG et WEBP uniquement).";
            } else if (photo.getSize() > MAX_AVATAR_BYTES) {
                error = "La photo de profil est trop volumineuse (maximum 5 Mo).";
            } else {
                try {
                    newPhoto = storage.store(photo, "profiles", "avatar_" + user.id());
                } catch (IOException e) {
                    error = "Une erreur est survenue lors de l'enregistrement de l'image.";
                }
            }
        }

        if (error == null) {
            try {
                users.updateProfile(user.id(), nom, prenom, email, newPhoto,
                        password.isEmpty() ? null : userService.hash(password));
                if (newPhoto != null) {
                    storage.delete(user.photo()); // ancienne photo remplacée
                }
                redirect.addFlashAttribute("flash", Flash.success("Profil mis à jour !",
                        "Vos modifications ont été enregistrées avec succès."));
                return "redirect:/profile";
            } catch (DataAccessException e) {
                storage.delete(newPhoto);
                error = "Impossible de mettre à jour le profil. L'email est peut-être déjà utilisé.";
            }
        } else {
            storage.delete(newPhoto);
        }

        model.addAttribute("flash", Flash.error("Erreur de mise à jour", error));
        return "profile";
    }
}
