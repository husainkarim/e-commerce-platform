package backend.product_service.service;

import org.springframework.stereotype.Service;
import backend.product_service.model.Product;
import backend.product_service.repository.ProductRepository;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    // Constructor injection
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product createProduct(Product product) {
        // Business logic: e.g., validation, or slight modification
        // For simplicity, we just save and notify Kafka
        Product savedProduct = productRepository.save(product);
        return savedProduct;
    }

    // ... other methods like getProductById, updateProduct, etc.
}
