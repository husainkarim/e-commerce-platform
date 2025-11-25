package backend.product_service.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import backend.product_service.model.Product;

@Service
public class KafkaService {
    
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void sendProductCreatedEvent(Product product) {
        Map<String, Object> event = new HashMap<>();
        event.put("id", product.getId());
        event.put("name", product.getName());
        event.put("type", "created");
        kafkaTemplate.send("product-created-topic", event);
    }

    // ✔ product-updated
    public void sendProductUpdatedEvent(Product product) {
        Map<String, Object> event = new HashMap<>();
        event.put("id", product.getId());
        event.put("name", product.getName());
        event.put("type", "updated");
        kafkaTemplate.send("product-updated-topic", event);
    }

    // ✔ product-deleted
    public void sendProductDeletedEvent(Product product) {
        Map<String, Object> event = new HashMap<>();
        event.put("id", product.getId());
        event.put("type", "deleted");
        kafkaTemplate.send("product-deleted-topic", event);
    }

}
