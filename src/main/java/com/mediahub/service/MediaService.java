package com.mediahub.service;

import com.mediahub.model.MediaItem;
import com.mediahub.repository.MediaRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MediaService {

    private final MediaRepository medias;
    private final FileStorageService storage;

    public MediaService(MediaRepository medias, FileStorageService storage) {
        this.medias = medias;
        this.storage = storage;
    }

    /** Supprime la ligne en base (likes, favoris et commentaires partent en cascade) puis le fichier physique. */
    public boolean delete(String mediaId) {
        Optional<MediaItem> media = medias.findById(mediaId);
        if (media.isEmpty()) {
            return false;
        }
        medias.delete(mediaId);
        storage.delete(media.get().fichier());
        return true;
    }
}
