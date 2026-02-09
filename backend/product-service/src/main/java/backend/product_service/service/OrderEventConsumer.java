package backend.product_service.service;

import java.util.List;
import java.util.Map;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import backend.product_service.model.Ordered;
import backend.product_service.model.Product;
import backend.product_service.repository.OrderedRepository;
import backend.product_service.repository.ProductRepository;


@Service
public class OrderEventConsumer {
    private final OrderedRepository orderedRepository;
    private final ProductRepository productRepository;
    public OrderEventConsumer(OrderedRepository orderedRepository, ProductRepository productRepository) {
        this.orderedRepository = orderedRepository;
        this.productRepository = productRepository;
    }

    // ✔ order-created
    @KafkaListener(topics = "order-created-topic")
    public void handleOrderCreated(Map<String, Object> event) {
        List<Map<String, Object>> items = (List<Map<String, Object>>) event.get("items");
        for (Map<String, Object> item : items) {
            Ordered ordered = new Ordered();
            ordered.setOrderId((String) event.get("orderId"));
            ordered.setProductId((String) item.get("productId"));
            ordered.setQuantity((Integer) item.get("quantity"));
            ordered.setPrice((Double) item.get("price"));
            ordered.setClientId((String) event.get("clientId"));
            ordered.setSellerId((String) item.get("sellerId"));
            orderedRepository.save(ordered);
            // reduce product quantity
            Product product = productRepository.findById(ordered.getProductId()).orElse(null);
            if (product != null) {
                product.setQuantity(product.getQuantity() - ordered.getQuantity());
                productRepository.save(product);
            }
        }
    }

    // ✔ order-cancelled/deleted
    @KafkaListener(topics = "order-deleted-topic")
    public void handleOrderDeleted(Map<String, Object> event) {
        String orderId = (String) event.get("orderId");
        System.out.println("Received order-deleted event for orderId: " + orderId);
        List<Ordered> orderedList = orderedRepository.findByOrderId(orderId);
        System.out.println("Found " + orderedList);
        // restore product quantity
        for (Ordered ordered : orderedList) {
            Product product = productRepository.findById(ordered.getProductId()).orElse(null);
            if (product != null) {
                product.setQuantity(product.getQuantity() + ordered.getQuantity());
                productRepository.save(product);
            }
        }
        orderedRepository.deleteAll(orderedList);
    }
}
