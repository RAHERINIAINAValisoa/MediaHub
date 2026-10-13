package com.mediahub.service;

import com.mediahub.util.FileSniffer;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

/** Enregistre et supprime les fichiers téléversés dans le dossier configuré (UPLOAD_DIR). */
@Service
public class FileStorageService {

    private static final Logger log = LoggerFactory.getLogger(FileStorageService.class);

    private final Path root;

    public FileStorageService(@Value("${app.upload-dir}") String uploadDir) {
        this.root = Path.of(uploadDir).toAbsolutePath().normalize();
    }

    @PostConstruct
    void init() throws IOException {
        Files.createDirectories(root.resolve("medias"));
        Files.createDirectories(root.resolve("profiles"));
        log.info("Dossier des fichiers téléversés : {}", root);
    }

    public Path root() {
        return root;
    }

    /** Extensions autorisées selon la catégorie ("image", "video", "audio"), ou null si la catégorie est invalide. */
    public static Set<String> allowedExtensions(String categorie) {
        return switch (categorie == null ? "" : categorie) {
            case "image" -> FileSniffer.IMAGE_EXT;
            case "video" -> FileSniffer.VIDEO_EXT;
            case "audio" -> FileSniffer.AUDIO_EXT;
            default -> null;
        };
    }

    public static String extensionOf(String filename) {
        if (filename == null) {
            return "";
        }
        int dot = filename.lastIndexOf('.');
        return dot < 0 ? "" : filename.substring(dot + 1).toLowerCase(Locale.ROOT);
    }

    /** Contrôle l'extension ET le contenu réel du fichier. */
    public boolean isValid(MultipartFile file, Set<String> allowedExt) {
        String ext = extensionOf(file.getOriginalFilename());
        if (!allowedExt.contains(ext)) {
            return false;
        }
        try (InputStream in = file.getInputStream()) {
            return FileSniffer.matches(in, ext);
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Enregistre le fichier dans le sous-dossier donné ("medias" ou "profiles").
     *
     * @return le chemin relatif à conserver en base, par exemple "medias/media_ab12_1700000000.jpg"
     */
    public String store(MultipartFile file, String subDir, String prefix) throws IOException {
        String ext = extensionOf(file.getOriginalFilename());
        String name = prefix + "_" + UUID.randomUUID().toString().substring(0, 8) + "_" + (System.currentTimeMillis() / 1000) + "." + ext;
        Path target = root.resolve(subDir).resolve(name).normalize();
        if (!target.startsWith(root)) {
            throw new IOException("Chemin de destination invalide");
        }
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        }
        return subDir + "/" + name;
    }

    /** Supprime un fichier à partir de son chemin relatif ; ne lève jamais d'exception. */
    public void delete(String relativePath) {
        if (relativePath == null || relativePath.isBlank()) {
            return;
        }
        try {
            Path target = root.resolve(relativePath).normalize();
            if (target.startsWith(root)) {
                Files.deleteIfExists(target);
            }
        } catch (IOException e) {
            log.warn("Impossible de supprimer {} : {}", relativePath, e.getMessage());
        }
    }
}
