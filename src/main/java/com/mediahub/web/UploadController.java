package com.mediahub.web;

import com.mediahub.config.Auth;
import com.mediahub.model.Flash;
import com.mediahub.model.User;
import com.mediahub.repository.MediaRepository;
import com.mediahub.service.FileStorageService;
import com.mediahub.util.Ids;
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
import java.util.Set;
import java.util.TreeSet;

@Controller
public class UploadController {

    private final MediaRepository medias;
    private final FileStorageService storage;

    public UploadController(MediaRepository medias, FileStorageService storage) {
        this.medias = medias;
        this.storage = storage;
    }

    @GetMapping("/upload")
    public String form() {
        return "upload";
    }

    @PostMapping("/upload")
    public String submit(@RequestParam(defaultValue = "") String titre,
                         @RequestParam(defaultValue = "") String description,
                         @RequestParam(defaultValue = "") String categorie,
                         @RequestParam(name = "media_file", required = false) MultipartFile file,
                         HttpServletRequest request, Model model, RedirectAttributes redirect) {
        User user = Auth.current(request);
        Set<String> allowed = FileStorageService.allowedExtensions(categorie);

        String error = null;
        if (titre.isBlank() || file == null || file.isEmpty()) {
            error = "Veuillez renseigner tous les champs obligatoires.";
        } else if (allowed == null) {
            error = "Catégorie de média invalide.";
        } else if (titre.length() > 255) {
            error = "Le titre est trop long (255 caractères maximum).";
        } else if (!storage.isValid(file, allowed)) {
            error = "Le fichier ne correspond pas au format autorisé pour la catégorie sélectionnée ("
                    + String.join(", ", new TreeSet<>(allowed)) + ").";
        }

        if (error == null) {
            String stored = null;
            try {
                stored = storage.store(file, "medias", "media");
                String id = Ids.next("med_");
                medias.insert(id, titre, description, categorie, stored, user.id());
                redirect.addFlashAttribute("flash",
                        Flash.success("Média publié !", "Votre fichier a été téléversé et indexé avec succès."));
                return "redirect:/media/" + id;
            } catch (IOException | DataAccessException e) {
                storage.delete(stored); // nettoyage si l'enregistrement en base échoue
                error = "Une erreur technique s'est produite lors de l'enregistrement du média.";
            }
        }

        model.addAttribute("flash", Flash.error("Échec de publication", error));
        model.addAttribute("titre", titre);
        model.addAttribute("description", description);
        model.addAttribute("categorie", categorie);
        return "upload";
    }
}
