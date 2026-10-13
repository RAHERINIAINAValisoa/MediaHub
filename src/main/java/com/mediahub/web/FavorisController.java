package com.mediahub.web;

import com.mediahub.config.Auth;
import com.mediahub.repository.MediaRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FavorisController {

    private final MediaRepository medias;

    public FavorisController(MediaRepository medias) {
        this.medias = medias;
    }

    @GetMapping("/favoris")
    public String favoris(HttpServletRequest request, Model model) {
        model.addAttribute("medias", medias.favorisOf(Auth.current(request).id()));
        return "favoris";
    }
}
