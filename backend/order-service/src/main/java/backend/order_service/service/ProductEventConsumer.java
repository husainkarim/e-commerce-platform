package backend.order_service.service;

import java.util.List;
import java.util.Map;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import backend.order_service.model.ProductAllowed;
import backend.order_service.repository.ProductAllowedRepository;

@Service
public class ProductEventConsumer {
    private final ProductAllowedRepository productAllowedRepository;

    public ProductEventConsumer(ProductAllowedRepository productAllowedRepository) {
        this.productAllowedRepository = productAllowedRepository;
    }

    public Boolean existsById(String productId) {
        return this.productAllowedRepository.existsById(productId);
    }

    @KafkaListener(topics = "product-created-topic")
    public void handleProductCreated(Map<String, Object> event) {
        List<ProductAllowed> existingProducts = this.productAllowedRepository.findByProductId((String) event.get("productId"));
        if (!existingProducts.isEmpty()) {
            return; // already exists
        }
        ProductAllowed productAllowed = new ProductAllowed((String) event.get("productId"), (String) event.get("name"), (String) event.get("sellerId"));
        this.productAllowedRepository.save(productAllowed);
    }

    @KafkaListener(topics = "product-updated-topic")
    public void handleProductUpdated(Map<String, Object> event) {
        List<ProductAllowed> existingProducts = this.productAllowedRepository.findByProductId((String) event.get("productId"));
        if (!existingProducts.isEmpty()) {
            // update product allowed info
            this.productAllowedRepository.deleteById((String) event.get("productId"));
        }
        ProductAllowed updatedProductAllowed = new ProductAllowed((String) event.get("productId"), (String) event.get("name"), (String) event.get("sellerId"));
        this.productAllowedRepository.save(updatedProductAllowed);
    }

    @KafkaListener(topics = "product-deleted-topic")
    public void handleProductDeleted(Map<String, Object> event) {
        List<ProductAllowed> existingProducts = this.productAllowedRepository.findByProductId((String) event.get("productId"));
        if (existingProducts.isEmpty()) {
            return; // does not exist
        }
        // delete product allowed info
        this.productAllowedRepository.deleteById((String) event.get("productId"));
    }
}
