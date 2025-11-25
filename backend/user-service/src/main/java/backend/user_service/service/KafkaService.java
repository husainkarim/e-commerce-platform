package backend.user_service.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import backend.user_service.model.User;

@Service
public class KafkaService {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    
    // Send created user event to Kafka topic
    public void sendUserCreatedEvent(User user) {
        if ("seller".equals(user.getRole())) {
            Map<String, Object> event = new HashMap<>();
            event.put("userId", user.getId());
            event.put("email", user.getEmail());
            event.put("role", user.getRole());
            kafkaTemplate.send("user-created-topic", event);
        }
    }

    public void sendUserUpdatedEvent(User user) {
        Map<String, Object> event = new HashMap<>();
        event.put("userId", user.getId());
        event.put("role", user.getRole());
        kafkaTemplate.send("user-updated-topic", event);
    }

    public void sendUserDeletedEvent(User user) {
        Map<String, Object> event = new HashMap<>();
        event.put("userId", user.getId());
        kafkaTemplate.send("user-deleted-topic", event);
    }
}
