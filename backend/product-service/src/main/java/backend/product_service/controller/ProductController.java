package backend.product_service.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import backend.product_service.model.Product;
import backend.product_service.repository.ProductRepository;
import backend.product_service.service.KafkaService;
import backend.product_service.service.UserEventConsumer;
import backend.product_service.shared.events.UserCreatedEvent;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductRepository productRepository;
    private final KafkaService kafkaService;
    private final UserEventConsumer userEventConsumer;

    public ProductController(ProductRepository productRepository, KafkaService kafkaService, UserEventConsumer userEventConsumer) {
        this.productRepository = productRepository;
        this.kafkaService = kafkaService;
        this.userEventConsumer = userEventConsumer;
    }

    // product list
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> listProducts() {
        Map<String, Object> response = new HashMap<>();
        // Logic to fetch product list from database
        List<Product> productList = productRepository.findAll();

        response.put("products", productList);
        response.put("message", "Product list fetched successfully");
        return new ResponseEntity<>(response, HttpStatus.OK); // 200
    }

    // get user products
    @GetMapping("/user-products")
    public ResponseEntity<Map<String, Object>> getUserProducts(@RequestParam String userId) {
        Map<String, Object> response = new HashMap<>();
        List<UserCreatedEvent> userSellerList = userEventConsumer.getUserSellerList();
        if (userSellerList.stream().noneMatch(user -> user.getUserId().equals(userId))) {
            response.put("message", "User is not a seller or does not exist");
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN); // 403
        }

        // check if userId is provided
        if (userId == null || userId.isEmpty()) {
            response.put("message", "User ID is required");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST); // 400
        }

        // Logic to fetch user products from database
        List<Product> userProducts = productRepository.findByUserId(userId);
        response.put("products", userProducts);
        response.put("message", "User products fetched successfully");
        return new ResponseEntity<>(response, HttpStatus.OK); // 200
    }

    // product details
    @GetMapping("/details")
    public ResponseEntity<Map<String, Object>> getProductDetails(@RequestParam String productId) {
        Map<String, Object> response = new HashMap<>();
        // Logic to fetch product details from database
        Product product = productRepository.findById(productId).orElse(null);
        if (product != null) {
            response.put("product", product);
            response.put("message", "Product details fetched successfully");
            return new ResponseEntity<>(response, HttpStatus.OK); // 200
        } else {
            response.put("message", "Product not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND); // 404
        }
    }

    // create product
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createProduct(@RequestBody Product product) {
        Map<String, Object> response = new HashMap<>();
        List<UserCreatedEvent> userSellerList = userEventConsumer.getUserSellerList();
        if (userSellerList.stream().noneMatch(user -> user.getUserId().equals(product.getUserId()))) {
            response.put("message", "User is not a seller or does not exist");
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN); // 403
        }

        // Logic to save product to database
        product.setId(null); // ensure id is null for new product
        Product savedProduct = productRepository.save(product);
        kafkaService.sendProductCreatedEvent(savedProduct);
        response.put("product", savedProduct);
        response.put("message", "Product created successfully");
        return new ResponseEntity<>(response, HttpStatus.CREATED); // 201
    }

    // update product
    @PutMapping("/update")
    public ResponseEntity<Map<String, Object>> updateProduct(@RequestBody Product product, @RequestParam String productId) {
        Map<String, Object> response = new HashMap<>();
        List<UserCreatedEvent> userSellerList = userEventConsumer.getUserSellerList();
        if (userSellerList.stream().noneMatch(user -> user.getUserId().equals(product.getUserId()))) {
            response.put("message", "User is not a seller or does not exist");
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN); // 403
        }

        // Logic to update product in database
        Product existingProduct = productRepository.findById(productId).orElse(null);
        if (existingProduct != null) {
            if (existingProduct.getId() == null ? product.getId() != null : !existingProduct.getId().equals(product.getId())) {
                response.put("message", "Product ID in request body does not match the ID in query parameter");
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED); // 401
            }
            product.setId(productId);
            Product updatedProduct = productRepository.save(product);
            kafkaService.sendProductUpdatedEvent(updatedProduct);
            response.put("product", updatedProduct);
            response.put("message", "Product updated successfully");
            return new ResponseEntity<>(response, HttpStatus.OK); // 200
        } else {
            response.put("message", "Product not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND); // 404
        }
    }

    // delete product
    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, Object>> deleteProduct(@RequestParam String productId) {
        Map<String, Object> response = new HashMap<>();

        List<UserCreatedEvent> userSellerList = userEventConsumer.getUserSellerList();
        if (userSellerList.stream().noneMatch(user -> user.getUserId().equals(
            productRepository.findById(productId).map(Product::getUserId).orElse(null)))) {
            response.put("message", "User is not a seller or does not exist");
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN); // 403
        }

        // check if product exists
        if (!productRepository.existsById(productId)) {
            response.put("message", "Product not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND); // 404
        }
        // Logic to delete product from database
        productRepository.deleteById(productId);
        Product deletedProduct = new Product();
        deletedProduct.setId(productId);
        kafkaService.sendProductDeletedEvent(deletedProduct);
        response.put("message", "Product deleted successfully");
        return new ResponseEntity<>(response, HttpStatus.OK); // 200
    }
}
