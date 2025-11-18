package backend.media_service.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import backend.media_service.model.Media;
import backend.media_service.repository.MediaRepository;
import backend.media_service.shared.events.ProductCreatedEvent;
import backend.media_service.shared.events.ProductDeletedEvent;
import backend.media_service.shared.events.ProductUpdatedEvent;

@Service
public class ProductEventConsumer {
    private List<ProductCreatedEvent> products = new ArrayList<>();

    private final MediaRepository mediaRepository;
    private final FileStorageService fileStorageService;
    public ProductEventConsumer(MediaRepository mediaRepository, FileStorageService fileStorageService) {
        this.mediaRepository = mediaRepository;
        this.fileStorageService = fileStorageService;
    }

    public List<ProductCreatedEvent> getProducts() {
        return products;
    }

    @KafkaListener(topics = "product-created-topic")
    public void handleProductCreated(ProductCreatedEvent event) {
        System.out.println("Received ProductCreatedEvent: " + event);
        products.add(event);
    }

    @KafkaListener(topics = "product-updated-topic")
    public void handleProductUpdated(ProductUpdatedEvent event) {
        System.out.println("Received ProductUpdatedEvent: " + event);
        products.removeIf(p -> p.getProductId().equals(event.getProductId()));
        products.add(new ProductCreatedEvent(event.getProductId(), event.getName()));
    }

    @KafkaListener(topics = "product-deleted-topic")
    public void handleProductDeleted(ProductDeletedEvent event) {
        System.out.println("Received ProductDeletedEvent: " + event);
        products.removeIf(p -> p.getProductId().equals(event.getProductId()));
        // delete all media related to this product
        List<Media> mediaList = mediaRepository.findByProductId(event.getProductId());
        for (Media media : mediaList) {
            try {
                String fileUrl = media.getImagePath();
                fileStorageService.deleteFileByUrl(fileUrl);
                mediaRepository.delete(media);
            } catch (Exception e) {
                System.err.println("Error deleting media for product " + event.getProductId() + ": " + e.getMessage());
            }
        }
    }

}
