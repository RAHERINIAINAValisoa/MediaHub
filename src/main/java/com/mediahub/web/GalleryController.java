package com.mediahub.web;

import com.mediahub.repository.MediaRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Set;

@Controller
public class GalleryController {

    private static final Set<String> CATEGORIES = Set.of("all", "image", "video", "audio");
    private static final Set<String> SORTS = Set.of("recent", "oldest", "popular");

    private final MediaRepository medias;

    public GalleryController(MediaRepository medias) {
        this.medias = medias;
    }

    @GetMapping("/galerie")
    public String gallery(@RequestParam(defaultValue = "") String q,
                          @RequestParam(defaultValue = "all") String cat,
                          @RequestParam(defaultValue = "recent") String sort,
                          Model model) {
        String category = CATEGORIES.contains(cat) ? cat : "all";
        String order = SORTS.contains(sort) ? sort : "recent";
        String query = q.trim();

        model.addAttribute("medias", medias.searchPublic(query, category, order));
        model.addAttribute("q", query);
        model.addAttribute("cat", category);
        model.addAttribute("sort", order);
        return "galerie";
    }
}
