package backend.user_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import backend.user_service.dto.UserCreatedEvent;
import backend.user_service.dto.UserDeleteEvent;
import backend.user_service.model.User;

@Service
public class KafkaService {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    
    // Send created user event to Kafka topic
    public void sendUserCreatedEvent(User user) {
        if(user.getRole().equals("SELLER")) {
            UserCreatedEvent event = new UserCreatedEvent(user.getId(), user.getEmail(), user.getRole());
            kafkaTemplate.send("user-created-topic", event);
        }
    }

    public void sendUserUpdatedEvent(User user) {
        if(user.getRole().equals("SELLER")) {
            UserCreatedEvent event = new UserCreatedEvent(user.getId(), user.getEmail(), user.getRole());
            kafkaTemplate.send("user-updated-topic", event);
        }
        if(user.getRole().equals("CLIENT")) {
            UserDeleteEvent event = new UserDeleteEvent(user.getId());
            kafkaTemplate.send("user-deleted-topic", event);
        }
    }

    public void sendUserDeletedEvent(User user) {
        UserDeleteEvent event = new UserDeleteEvent(user.getId());
        kafkaTemplate.send("user-deleted-topic", event);
    }
}
