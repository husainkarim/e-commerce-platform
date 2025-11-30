package backend.product_service.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import backend.product_service.model.Product;
import backend.product_service.repository.ProductRepository;

@Service
public class UserEventConsumer {
    private final ProductRepository productRepository;
    private final KafkaService kafkaService;

    private List<Map<String, Object>> userSellerList = new ArrayList<>();

    public UserEventConsumer(ProductRepository productRepository, KafkaService kafkaService) {
        this.productRepository = productRepository;
        this.kafkaService = kafkaService;
    }

    public List<Map<String, Object>> getUserSellerList() {
        return userSellerList;
    }

    public String getUserNameById(String userId) {
        for (Map<String, Object> user : userSellerList) {
            if (user.get("userId").equals(userId)) {
                return (String) user.get("email");
            }
        }
        return "Unknown Seller";
    }
    
    @KafkaListener(topics = "user-created-topic")
    public void handleSellerCreated(Map<String, Object> event) {
        // Allow this seller to use product service
        this.userSellerList.add(event);
        System.out.println(this.userSellerList);
    }

    @KafkaListener(topics = "user-updated-topic")
    public void handleUserRoleUpdated(Map<String, Object> event) {
        // Handle user role update logic here   
        if(event.get("newRole").equals("seller")) {
            // Add to seller list
            Map<String, Object> newSeller = new HashMap<>();
            newSeller.put("userId", event.get("userId"));
            newSeller.put("email", "");
            newSeller.put("role", event.get("newRole"));
            this.userSellerList.add(newSeller);
            System.out.println(this.userSellerList);
        } else {
            // Remove from seller list
            this.userSellerList.removeIf(user -> user.get("userId").equals(event.get("userId")));
            System.out.println(this.userSellerList);
            // remove all products from database by get all products with userId
            List<Product> products = productRepository.findByUserId((String) event.get("userId"));
            //send to media to remove images
            for (Product product : products) {
                kafkaService.sendProductDeletedEvent(product);
            }
            productRepository.deleteAll(products);
            
        }
    }

    @KafkaListener( topics = "user-deleted-topic")
    public void handleUserDeleted(Map<String, Object> event) {
        // Handle user deletion logic here
        this.userSellerList.removeIf(user -> user.get("userId").equals(event.get("userId")));
        System.out.println(this.userSellerList);
        // remove all products from database by get all products with userId
        List<Product> products = productRepository.findByUserId((String) event.get("userId"));
        //send to media to remove images
        for (Product product : products) {
            kafkaService.sendProductDeletedEvent(product);
        }
        productRepository.deleteAll(products);
    }
}
