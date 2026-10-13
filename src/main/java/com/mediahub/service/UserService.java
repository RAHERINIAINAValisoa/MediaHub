package com.mediahub.service;

import com.mediahub.model.MediaItem;
import com.mediahub.model.User;
import com.mediahub.repository.MediaRepository;
import com.mediahub.repository.UserRepository;
import com.mediahub.util.Ids;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository users;
    private final MediaRepository medias;
    private final MediaService mediaService;
    private final FileStorageService storage;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public UserService(UserRepository users, MediaRepository medias, MediaService mediaService, FileStorageService storage) {
        this.users = users;
        this.medias = medias;
        this.mediaService = mediaService;
        this.storage = storage;
    }

    public String hash(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    /** @return l'utilisateur si les identifiants sont corrects et le compte actif. */
    public Optional<User> authenticate(String email, String password) {
        return users.findByEmail(email)
                .filter(User::active)
                .filter(u -> encoder.matches(password, u.motDePasse()));
    }

    /** Vrai si les identifiants sont corrects mais que le compte a été désactivé par un administrateur. */
    public boolean isDisabledWithValidPassword(String email, String password) {
        return users.findByEmail(email)
                .filter(u -> !u.active())
                .filter(u -> encoder.matches(password, u.motDePasse()))
                .isPresent();
    }

    /** @return false si l'adresse email est déjà utilisée. */
    public boolean register(String nom, String prenom, String email, String password, String role) {
        if (users.findByEmail(email).isPresent()) {
            return false;
        }
        try {
            users.insert(Ids.next("usr_"), nom, prenom, email, hash(password), role);
            return true;
        } catch (DuplicateKeyException e) {
            return false;
        }
    }

    /** Supprime le compte, son avatar et toutes ses publications (fichiers compris). */
    public void deleteWithContent(User user) {
        for (MediaItem media : medias.findByAuthor(user.id())) {
            mediaService.delete(media.id());
        }
        storage.delete(user.photo());
        users.delete(user.id());
    }
}
