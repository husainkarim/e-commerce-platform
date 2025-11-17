package backend.media_service.service;

import org.springframework.stereotype.Service;
import backend.media_service.shared.events.ProductCreatedEvent;
import backend.media_service.shared.events.ProductDeletedEvent;
import backend.media_service.shared.events.ProductUpdatedEvent;
import backend.media_service.kafka.KafkaService;

@Service
public class ProductEventConsumer {
    private List<ProductCreatedEvent> products = new ArrayList<>();

    private final MediaRepository mediaRepository;
    private final KafkaService kafkaService;
    private final FileStorageService fileStorageService;
    public ProductEventConsumer(MediaRepository mediaRepository, KafkaService kafkaService, FileStorageService fileStorageService) {
        this.mediaRepository = mediaRepository;
        this.kafkaService = kafkaService;
        this.fileStorageService = fileStorageService;
    }

    @KafkaListener(topics = "product-created", groupId = "media-service")
    public void handleProductCreated(ProductCreatedEvent event) {
        System.out.println("Received ProductCreatedEvent: " + event);
        products.add(event);
    }

    @KafkaListener(topics = "product-updated", groupId = "media-service")
    public void handleProductUpdated(ProductUpdatedEvent event) {
        System.out.println("Received ProductUpdatedEvent: " + event);
        products.removeIf(p -> p.getProductId().equals(event.getProductId()));
        products.add(new ProductCreatedEvent(event.getProductId(), event.getName()));
    }

    @KafkaListener(topics = "product-deleted", groupId = "media-service")
    public void handleProductDeleted(ProductDeletedEvent event) {
        System.out.println("Received ProductDeletedEvent: " + event);
        products.removeIf(p -> p.getProductId().equals(event.getProductId()));
        // delete all media related to this product
        List<Media> mediaList = mediaRepository.findByProductId(event.getProductId());
        for (Media media : mediaList) {
            try {
                String fileUrl = media.getUrl();
                fileStorageService.deleteFileByUrl(fileUrl);
                mediaRepository.delete(media);
            } catch (Exception e) {
                System.err.println("Error deleting media for product " + event.getProductId() + ": " + e.getMessage());
            }
        }
    }

}
