package backend.product_service.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.kafka.annotation.KafkaListener;

import backend.product_service.shared.events.UserCreatedEvent;
import backend.product_service.shared.events.UserDeleteEvent;
import backend.product_service.shared.events.UserRoleUpdated;
import backend.product_service.repository.ProductRepository;
import org.springframework.stereotype.Service;

import backend.product_service.service.KafkaService;
import backend.product_service.model.Product;


@Service
public class UserEventConsumer {
    private final ProductRepository productRepository;
    private final KafkaService kafkaService;

    private List<UserCreatedEvent> userSellerList = new ArrayList<>();

    public UserEventConsumer(ProductRepository productRepository, KafkaService kafkaService) {
        this.productRepository = productRepository;
        this.kafkaService = kafkaService;
    }

    public List<UserCreatedEvent> getUserSellerList() {
        return userSellerList;
    }
    @KafkaListener(topics = "user-created-topic")
    public void handleSellerCreated(UserCreatedEvent event) {
        // Allow this seller to use product service
        this.userSellerList.add(event);
        System.out.println(this.userSellerList);
    }

    @KafkaListener(topics = "user-updated-topic")
    public void handleUserRoleUpdated(UserRoleUpdated event) {
        // Handle user role update logic here   
        if(event.getNewRole().equals("SELLER")) {
            // Add to seller list
            this.userSellerList.add(new UserCreatedEvent(event.getUserId(), "", event.getNewRole()));
            System.out.println(this.userSellerList);
        } else {
            // Remove from seller list
            this.userSellerList.removeIf(user -> user.getUserId().equals(event.getUserId()));
            System.out.println(this.userSellerList);
            // remove all products from database by get all products with userId
            List<Product> products = productRepository.findByUserId(event.getUserId());
            //send to media to remove images
            for (Product product : products) {
                kafkaService.sendProductDeletedEvent(product);
            }
            productRepository.deleteAll(products);
            
        }
    }

    @KafkaListener( topics = "user-deleted-topic")
    public void handleUserDeleted(UserDeleteEvent event) {
        // Handle user deletion logic here
        this.userSellerList.removeIf(user -> user.getUserId().equals(event.getUserId()));
        System.out.println(this.userSellerList);
        // remove all products from database by get all products with userId
        List<Product> products = productRepository.findByUserId(event.getUserId());
        //send to media to remove images
        for (Product product : products) {
            kafkaService.sendProductDeletedEvent(product);
        }
        productRepository.deleteAll(products);
    }
}
