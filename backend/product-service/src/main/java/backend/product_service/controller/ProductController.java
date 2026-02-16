package backend.product_service.controller;

import java.util.Arrays;
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

import backend.product_service.dto.ProductDetails;
import backend.product_service.dto.SellerDashboard;
import backend.product_service.model.Product;
import backend.product_service.model.Seller;
import backend.product_service.repository.OrderedRepository;
import backend.product_service.repository.ProductRepository;
import backend.product_service.repository.SellerRepository;
import backend.product_service.service.KafkaService;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductRepository productRepository;
    private final KafkaService kafkaService;
    private final SellerRepository sellerRepository;
    private final OrderedRepository orderedRepository;

    private static final String[] CATEGORIES = {
        "General", "Electronics", "Fashion", "Home", "Sports", "Books", "Toys",
        "Health", "Automotive", "Garden", "Music", "Movies", "Groceries",
        "Jewelry", "Beauty"
    };
    
    private static final String MESSAGE = "message";
    private static final String PRODUCT = "product";
    private static final String USER_NOT_SELLER_MESSAGE = "User is not a seller or does not exist";
    private static final String PRODUCT_NOT_FOUND_MESSAGE = "Product not found";

    public ProductController(ProductRepository productRepository, KafkaService kafkaService, SellerRepository sellerRepository, OrderedRepository orderedRepository) {
        this.productRepository = productRepository;
        this.kafkaService = kafkaService;
        this.sellerRepository = sellerRepository;
        this.orderedRepository = orderedRepository;
    }

    // product list
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> listProducts() {
        Map<String, Object> response = new HashMap<>();
        // Logic to fetch product list from database
        List<Product> productList = productRepository.findAll();

        response.put("products", productList);
        response.put(MESSAGE, "Product list fetched successfully");
        return new ResponseEntity<>(response, HttpStatus.OK); // 200
    }

    // get user products
    @GetMapping("/user-products")
    public ResponseEntity<Map<String, Object>> getUserProducts(@RequestParam String userId) {
        Map<String, Object> response = new HashMap<>();
        List<Seller> userSellerList = sellerRepository.findAll();
        if (userSellerList.stream().noneMatch(user -> user.getUserId().equals(userId))) {
            response.put(MESSAGE, USER_NOT_SELLER_MESSAGE);
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN); // 403
        }

        // check if userId is provided
        if (userId == null || userId.isEmpty()) {
            response.put(MESSAGE, "User ID is required");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST); // 400
        }

        // Logic to fetch user products from database
        List<Product> userProducts = productRepository.findByUserId(userId);
        List<ProductDetails> detailedProducts = userProducts.stream().map(product -> {
            return new ProductDetails(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getCategory(),
                product.getPrice(),
                orderedRepository.findByProductId(product.getId()).stream()
                    .filter(order -> order.getProductId().equals(product.getId()))
                    .mapToDouble(order -> order.getPrice() * order.getQuantity())
                    .sum(), // revenue
                product.getQuantity(),
                orderedRepository.findByProductId(product.getId()).stream()
                    .mapToInt(ordered -> ordered.getQuantity())
                    .sum(), // unitsSold
                product.getUserId()
            );
        }).toList();
        SellerDashboard sellerDashboard = new SellerDashboard(detailedProducts);
        response.put("sellerDashboard", sellerDashboard);
        response.put(MESSAGE, "User products fetched successfully");
        return new ResponseEntity<>(response, HttpStatus.OK); // 200
    }

    // product details
    @GetMapping("/details")
    public ResponseEntity<Map<String, Object>> getProductDetails(@RequestParam String productId) {
        Map<String, Object> response = new HashMap<>();
        // Logic to fetch product details from database
        Product product = productRepository.findById(productId).orElse(null);
        if (product != null) {
            ProductDetails productDetails = new ProductDetails(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getCategory(),
                product.getPrice(),
                orderedRepository.findByProductId(productId).stream()
                    .mapToDouble(order -> order.getPrice() * order.getQuantity())
                    .sum(),
                product.getQuantity(),
                orderedRepository.findByProductId(productId).stream()
                    .mapToInt(ordered -> ordered.getQuantity())
                    .sum(),
                product.getUserId()
            );
            response.put(PRODUCT, productDetails);
            response.put(MESSAGE, "Product details fetched successfully");
            return new ResponseEntity<>(response, HttpStatus.OK); // 200
        } else {
            response.put(MESSAGE, PRODUCT_NOT_FOUND_MESSAGE);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND); // 404
        }
    }

    // create product
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createProduct(@RequestBody Product product) {
        Map<String, Object> response = new HashMap<>();
        List<Seller> userSellerList = sellerRepository.findAll();
        if (userSellerList.stream().noneMatch(user -> user.getUserId().equals(product.getUserId()))) {
            response.put(MESSAGE, USER_NOT_SELLER_MESSAGE);
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN); // 403
        }
        // validation for catogory
        if (!Arrays.asList(CATEGORIES).contains(product.getCategory())) {
            response.put(MESSAGE, "Invalid category");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST); // 400
        }
        // Logic to save product to database
        product.setId(null); // ensure id is null for new product
        Product savedProduct = productRepository.save(product);
        kafkaService.sendProductCreatedEvent(savedProduct);
        response.put(PRODUCT, savedProduct);
        response.put(MESSAGE, "Product created successfully");
        return new ResponseEntity<>(response, HttpStatus.CREATED); // 201
    }

    // update product
    @PutMapping("/update")
    public ResponseEntity<Map<String, Object>> updateProduct(@RequestBody Product product, @RequestParam String productId) {
        Map<String, Object> response = new HashMap<>();
        List<Seller> userSellerList = sellerRepository.findAll();
        if (userSellerList.stream().noneMatch(user -> user.getUserId().equals(product.getUserId()))) {
            response.put(MESSAGE, USER_NOT_SELLER_MESSAGE);
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN); // 403
        }

        // validation for catogory
        if (!Arrays.asList(CATEGORIES).contains(product.getCategory())) {
            response.put(MESSAGE, "Invalid category");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST); // 400
        }

        // Logic to update product in database
        Product existingProduct = productRepository.findById(productId).orElse(null);
        if (existingProduct != null) {
            if (existingProduct.getId() == null ? product.getId() != null : !existingProduct.getId().equals(product.getId())) {
                response.put(MESSAGE, "Product ID in request body does not match the ID in query parameter");
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED); // 401
            }
            product.setId(productId);
            Product updatedProduct = productRepository.save(product);
            kafkaService.sendProductUpdatedEvent(updatedProduct);
            response.put(PRODUCT, updatedProduct);
            response.put(MESSAGE, "Product updated successfully");
            return new ResponseEntity<>(response, HttpStatus.OK); // 200
        } else {
            response.put(MESSAGE, PRODUCT_NOT_FOUND_MESSAGE);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND); // 404
        }
    }

    // delete product
    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, Object>> deleteProduct(@RequestParam String productId) {
        Map<String, Object> response = new HashMap<>();

        List<Seller> userSellerList = sellerRepository.findAll();
        if (userSellerList.stream().noneMatch(user -> user.getUserId().equals(
            productRepository.findById(productId).map(Product::getUserId).orElse(null)))) {
            response.put(MESSAGE, USER_NOT_SELLER_MESSAGE);
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN); // 403
        }

        // check if product exists
        if (!productRepository.existsById(productId)) {
            response.put(MESSAGE, PRODUCT_NOT_FOUND_MESSAGE);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND); // 404
        }
        // send kafka event before deleting
        Product deletedProduct = new Product();
        deletedProduct.setId(productId);
        kafkaService.sendProductDeletedEvent(deletedProduct);
        // Logic to delete product from database
        productRepository.deleteById(productId);
        
        response.put(MESSAGE, "Product deleted successfully");
        return new ResponseEntity<>(response, HttpStatus.OK); // 200
    }
}
