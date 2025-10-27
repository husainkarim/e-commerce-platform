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

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
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
    // product details
    @GetMapping("/details")
    public ResponseEntity<Map<String, Object>> getProductDetails(@RequestParam String id) {
        Map<String, Object> response = new HashMap<>();
        // Logic to fetch product details from database
        Product product = productRepository.findById(id).orElse(null);
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
        // Logic to save product to database
        Product savedProduct = productRepository.save(product);
        response.put("product", savedProduct);
        response.put("message", "Product created successfully");
        return new ResponseEntity<>(response, HttpStatus.CREATED); // 201
    }

    // update product
    @PutMapping("/update")
    public ResponseEntity<Map<String, Object>> updateProduct(@RequestBody Product product, @RequestParam String id) {
        Map<String, Object> response = new HashMap<>();
        // Logic to update product in database
        Product existingProduct = productRepository.findById(id).orElse(null);
        if (existingProduct != null) {
            product.setId(id);
            Product updatedProduct = productRepository.save(product);
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
    public ResponseEntity<Map<String, Object>> deleteProduct(@RequestParam String id) {
        Map<String, Object> response = new HashMap<>();

        // check if product exists
        if (!productRepository.existsById(id)) {
            response.put("message", "Product not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND); // 404
        }
        // Logic to delete product from database
        productRepository.deleteById(id);
        response.put("message", "Product deleted successfully");
        return new ResponseEntity<>(response, HttpStatus.OK); // 200
    }
}
