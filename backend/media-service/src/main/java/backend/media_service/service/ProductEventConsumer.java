package backend.media_service.service;

import java.util.List;
import java.util.Map;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import backend.media_service.model.Media;
import backend.media_service.model.ProductAllowed;
import backend.media_service.repository.MediaRepository;
import backend.media_service.repository.ProductAllowedRepository;

@Service
public class ProductEventConsumer {

    private final MediaRepository mediaRepository;
    private final ProductAllowedRepository productAllowedRepository;
    private final FileStorageService fileStorageService;

    public ProductEventConsumer(MediaRepository mediaRepository, ProductAllowedRepository productAllowedRepository, FileStorageService fileStorageService) {
        this.mediaRepository = mediaRepository;
        this.productAllowedRepository = productAllowedRepository;
        this.fileStorageService = fileStorageService;
    }

    @KafkaListener(topics = "product-created-topic")
    public void handleProductCreated(Map<String, Object> event) {
        if (this.productAllowedRepository.existsById((String) event.get("productId"))) {
            return; // already exists
        }
        ProductAllowed productAllowed = new ProductAllowed((String) event.get("productId"), (String) event.get("name"));
        this.productAllowedRepository.save(productAllowed);
    }

    @KafkaListener(topics = "product-updated-topic")
    public void handleProductUpdated(Map<String, Object> event) {
        if (!this.productAllowedRepository.existsById((String) event.get("productId"))) {
            return; // does not exist
        }
        // update product allowed info
        this.productAllowedRepository.deleteById((String) event.get("productId"));
        ProductAllowed updatedProductAllowed = new ProductAllowed((String) event.get("productId"), (String) event.get("name"));
        this.productAllowedRepository.save(updatedProductAllowed);
    }

    @KafkaListener(topics = "product-deleted-topic")
    public void handleProductDeleted(Map<String, Object> event) {
        if (!this.productAllowedRepository.existsById((String) event.get("productId"))) {
            return; // does not exist
        }
        // delete all media related to this product
        List<Media> mediaList = mediaRepository.findByProductId((String) event.get("productId"));
        for (Media media : mediaList) {
            try {
                String fileUrl = media.getImagePath();
                fileStorageService.deleteFileByUrl(fileUrl);
                mediaRepository.delete(media);
            } catch (Exception e) {
                System.err.println("Error deleting media for product " + event.get("productId") + ": " + e.getMessage());
            }
        }
        // delete product allowed info
        this.productAllowedRepository.deleteById((String) event.get("productId"));
    }
}
