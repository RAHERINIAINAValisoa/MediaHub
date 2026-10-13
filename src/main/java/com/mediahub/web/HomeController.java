package com.mediahub.web;

import com.mediahub.repository.MediaRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final MediaRepository medias;

    public HomeController(MediaRepository medias) {
        this.medias = medias;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("trending", medias.trending(4));
        return "index";
    }
}
