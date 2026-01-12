package backend.order_service.service;

import java.util.Map;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import backend.order_service.model.Client;
import backend.order_service.repository.ClientRepository;

@Service
public class UserEventConsumer {
    private final ClientRepository clientRepository;
    public UserEventConsumer(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @KafkaListener(topics = "user-created-topic")
    public void handleSellerCreated(Map<String, Object> event) {
        if (event.get("role") == null || !event.get("role").equals("client")) {
            return; // not a client
        }
        if (this.clientRepository.existsById((String) event.get("userId"))) {
            return; // already exists
        }
        // Allow this client to use order service
        Client client = new Client((String) event.get("userId"), (String) event.get("email"), (String) event.get("role"));
        this.clientRepository.save(client);
    }

    @KafkaListener(topics = "user-updated-topic")
    public void handleUserRoleUpdated(Map<String, Object> event) {
        if (!this.clientRepository.existsById((String) event.get("userId"))) {
            return; // does not exist
        }
        // Handle user role update logic here   
        if(event.get("newRole").equals("client")) {
            // Add to client list
            Client client = new Client((String) event.get("userId"), (String) event.get("email"), (String) event.get("newRole"));
            this.clientRepository.save(client);
        } else {
            // Remove from client list
            this.clientRepository.deleteById((String) event.get("userId")); 
        }
    }

    @KafkaListener(topics = "user-deleted-topic")
    public void handleUserDeleted(Map<String, Object> event) {
        if (!this.clientRepository.existsById((String) event.get("userId"))) {
            return; // does not exist
        }
        if (event.get("role") == null || !event.get("role").equals("client")) {
            return; // not a client
        }
        // Remove from client list
        this.clientRepository.deleteById((String) event.get("userId"));
    }
}
