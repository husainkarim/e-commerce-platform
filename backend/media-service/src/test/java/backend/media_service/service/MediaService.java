package backend.media_service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import backend.media_service.model.Media;
import backend.media_service.repository.MediaRepository;

@Service
public class MediaService {
    private final MediaRepository mediaRepository;

    public MediaService(MediaRepository mediaRepository) {
        this.mediaRepository = mediaRepository;
    }

    public Media saveMedia(Media media) {
        Media savedMedia = mediaRepository.save(media);
        // Notify other services that new media is available
        return savedMedia;
    }
    
    public List<Media> findByProductId(String productId) {
        return mediaRepository.findByProductId(productId);
    }
}