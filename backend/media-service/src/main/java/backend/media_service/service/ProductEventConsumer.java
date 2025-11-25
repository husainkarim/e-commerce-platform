package backend.media_service.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import backend.media_service.model.Media;
import backend.media_service.repository.MediaRepository;

@Service
public class ProductEventConsumer {
    private List<Map<String, Object>> products = new ArrayList<>();

    private final MediaRepository mediaRepository;
    private final FileStorageService fileStorageService;
    public ProductEventConsumer(MediaRepository mediaRepository, FileStorageService fileStorageService) {
        this.mediaRepository = mediaRepository;
        this.fileStorageService = fileStorageService;
    }

    public List<Map<String, Object>> getProducts() {
        return products;
    }

    @KafkaListener(topics = "product-created-topic")
    public void handleProductCreated(Map<String, Object> event) {
        System.out.println("Received ProductCreatedEvent: " + event);
        products.add(event);
    }

    @KafkaListener(topics = "product-updated-topic")
    public void handleProductUpdated(Map<String, Object> event) {
        System.out.println("Received ProductUpdatedEvent: " + event);
        products.removeIf(p -> p.get("productId").equals(event.get("productId")));
        Map<String, Object> eventProduct = new HashMap<>();
        eventProduct.put("productId", event.get("productId"));
        eventProduct.put("name", event.get("name"));
        products.add(eventProduct);
    }

    @KafkaListener(topics = "product-deleted-topic")
    public void handleProductDeleted(Map<String, Object> event) {
        System.out.println("Received ProductDeletedEvent: " + event);
        products.removeIf(p -> p.get("productId").equals(event.get("productId")));
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
    }

}
