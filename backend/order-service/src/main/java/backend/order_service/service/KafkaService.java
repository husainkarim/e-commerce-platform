package backend.order_service.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import backend.order_service.model.Order;

@Service
public class KafkaService {
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    //send order-created event
    public void sendOrderCreatedEvent(Order order) {
        Map<String, Object> event = new HashMap<>();
        event.put("orderId", order.getId());
        event.put("clientId", order.getUserId());
        event.put("items", order.getItems());
        kafkaTemplate.send("order-created-topic", event);
    }

    //send order-cancelled/deleted event
    public void sendOrderDeletedEvent(Order order) {
        Map<String, Object> event = new HashMap<>();
        event.put("orderId", order.getId());
        event.put("clientId", order.getUserId());
        kafkaTemplate.send("order-deleted-topic", event);
    }
}
