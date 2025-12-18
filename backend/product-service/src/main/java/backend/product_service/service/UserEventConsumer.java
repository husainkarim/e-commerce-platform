package backend.product_service.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import backend.product_service.model.Product;
import backend.product_service.repository.ProductRepository;
import backend.product_service.model.Seller;
import backend.product_service.repository.SellerRepository;

@Service
public class UserEventConsumer {
    private final ProductRepository productRepository;
    private final KafkaService kafkaService;
    private final SellerRepository sellerRepository;

    private List<Seller> userSellerList = new ArrayList<>();

    public UserEventConsumer(ProductRepository productRepository, KafkaService kafkaService, SellerRepository sellerRepository) {
        this.productRepository = productRepository;
        this.kafkaService = kafkaService;
        this.sellerRepository = sellerRepository;
    }

    public List<Seller> getUserSellerList() {
        return userSellerList;
    }

    public String getUserNameById(String userId) {
        for (Seller user : userSellerList) {
            if (user.getUserId().equals(userId)) {
                return user.getEmail();
            }
        }
        return "Unknown Seller";
    }
    
    @KafkaListener(topics = "user-created-topic")
    public void handleSellerCreated(Map<String, Object> event) {
        // Allow this seller to use product service
        Seller seller = new Seller((String) event.get("userId"), (String) event.get("email"), (String) event.get("role"));
        this.sellerRepository.save(seller);
        this.userSellerList = this.sellerRepository.findAll();
    }

    @KafkaListener(topics = "user-updated-topic")
    public void handleUserRoleUpdated(Map<String, Object> event) {
        // Handle user role update logic here   
        if(event.get("newRole").equals("seller")) {
            // Add to seller list
            Seller seller = new Seller((String) event.get("userId"), (String) event.get("email"), (String) event.get("newRole"));
            this.sellerRepository.save(seller);
            this.userSellerList = this.sellerRepository.findAll();
        } else {
            // Remove from seller list
            // remove all products from database by get all products with userId
            List<Product> products = productRepository.findByUserId((String) event.get("userId"));
            //send to media to remove images
            for (Product product : products) {
                kafkaService.sendProductDeletedEvent(product);
            }
            productRepository.deleteAll(products);
            this.sellerRepository.deleteById((String) event.get("userId"));
            this.userSellerList = this.sellerRepository.findAll();
        }
    }

    @KafkaListener( topics = "user-deleted-topic")
    public void handleUserDeleted(Map<String, Object> event) {
        // Handle user deletion logic here
        // remove all products from database by get all products with userId
        List<Product> products = productRepository.findByUserId((String) event.get("userId"));
        //send to media to remove images
        for (Product product : products) {
            kafkaService.sendProductDeletedEvent(product);
        }
        productRepository.deleteAll(products);
        this.sellerRepository.deleteById((String) event.get("userId"));
        this.userSellerList = this.sellerRepository.findAll();
    }
}
