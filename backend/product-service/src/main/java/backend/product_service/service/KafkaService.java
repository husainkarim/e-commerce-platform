package backend.product_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import backend.product_service.shared.events.ProductCreatedEvent;
import backend.product_service.shared.events.ProductUpdatedEvent;
import backend.product_service.shared.events.ProductDeletedEvent;
import backend.product_service.model.Product;

@Service
public class KafkaService {
    
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    // ✔ product-created
    public void sendProductCreatedEvent(Product product) {
        ProductCreatedEvent event = new ProductCreatedEvent(product.getId(), product.getName());
        kafkaTemplate.send("product-created-topic", event);
    }
    
    // ✔ product-updated
    public void sendProductUpdatedEvent(Product product) {
        ProductUpdatedEvent event = new ProductUpdatedEvent(product.getId(), product.getName());
        kafkaTemplate.send("product-updated-topic", event);
    }

    // ✔ product-deleted
    public void sendProductDeletedEvent(Product product) {
        ProductDeletedEvent event = new ProductDeletedEvent(product.getId());
        kafkaTemplate.send("product-deleted-topic", event);
    }

}
