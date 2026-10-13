package com.mediahub.web;

import com.mediahub.config.Auth;
import com.mediahub.model.Flash;
import com.mediahub.model.User;
import com.mediahub.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;
import java.util.regex.Pattern;

@Controller
public class AuthController {

    static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginForm(HttpServletRequest request) {
        return Auth.current(request) != null ? "redirect:/" : "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam(defaultValue = "") String email,
                        @RequestParam(defaultValue = "") String password,
                        HttpServletRequest request, Model model, RedirectAttributes redirect) {
        if (Auth.current(request) != null) {
            return "redirect:/";
        }
        String error;
        if (email.isBlank() || password.isEmpty()) {
            error = "Veuillez renseigner votre email et votre mot de passe.";
        } else {
            Optional<User> user = userService.authenticate(email, password);
            if (user.isPresent()) {
                Auth.login(request, user.get());
                redirect.addFlashAttribute("flash", Flash.toastSuccess("Connexion réussie ! Bienvenue sur MediaHub."));
                return "redirect:/";
            }
            error = userService.isDisabledWithValidPassword(email, password)
                    ? "Votre compte a été désactivé par l'administrateur."
                    : "Identifiants de connexion incorrects.";
        }
        model.addAttribute("flash", Flash.error("Erreur de connexion", error));
        model.addAttribute("email", email);
        return "login";
    }

    @GetMapping("/register")
    public String registerForm(HttpServletRequest request) {
        return Auth.current(request) != null ? "redirect:/" : "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam(defaultValue = "") String nom,
                           @RequestParam(defaultValue = "") String prenom,
                           @RequestParam(defaultValue = "") String email,
                           @RequestParam(defaultValue = "") String password,
                           @RequestParam(name = "confirm_password", defaultValue = "") String confirmPassword,
                           HttpServletRequest request, Model model, RedirectAttributes redirect) {
        if (Auth.current(request) != null) {
            return "redirect:/";
        }
        String error = null;
        if (nom.isBlank() || prenom.isBlank() || email.isBlank() || password.isEmpty() || confirmPassword.isEmpty()) {
            error = "Veuillez remplir tous les champs obligatoires.";
        } else if (!password.equals(confirmPassword)) {
            error = "Les mots de passe ne correspondent pas.";
        } else if (password.length() < 6) {
            error = "Le mot de passe doit contenir au moins 6 caractères.";
        } else if (!EMAIL.matcher(email.trim()).matches()) {
            error = "Veuillez saisir une adresse email valide.";
        } else if (nom.length() > 100 || prenom.length() > 100 || email.length() > 255) {
            error = "Un des champs est trop long.";
        } else if (!userService.register(nom, prenom, email, password, "user")) {
            error = "Cette adresse email est déjà associée à un compte.";
        }

        if (error == null) {
            redirect.addFlashAttribute("flash", Flash.success("Inscription réussie !",
                    "Votre compte a été créé avec succès. Vous pouvez maintenant vous connecter."));
            return "redirect:/login";
        }
        model.addAttribute("flash", Flash.error("Erreur d'inscription", error));
        model.addAttribute("nom", nom);
        model.addAttribute("prenom", prenom);
        model.addAttribute("email", email);
        return "register";
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        Auth.logout(request);
        return "redirect:/login";
    }
}
