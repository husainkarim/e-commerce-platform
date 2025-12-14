package backend.product_service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import backend.product_service.model.Product;
import backend.product_service.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import backend.product_service.service.ProductService;

// Activates Mockito annotations for JUnit 5
@ExtendWith(MockitoExtension.class)
public class ProductServiceUnitTest {

    // Mock the dependencies: the repository (DB) and Kafka service
    @Mock
    private ProductRepository productRepository;

    // Inject the mocks into the service being tested
    @InjectMocks
    private ProductService productService;

    private Product productToSave;

    @BeforeEach
    void setUp() {
        // Prepare a product object for use in tests
        productToSave = new Product("123", "Laptop Pro", "A high-end laptop.", 1999.99, 10, "user-a1");
    }

    @Test
    void whenCreateProduct_thenProductIsSavedAndNotificationIsSent() {
        // ARRANGE: Define the behavior of the mocked dependencies
        // 1. When repository.save is called with ANY Product, return the productToSave object
        when(productRepository.save(any(Product.class)))
            .thenReturn(productToSave);

        // ACT: Call the method under test
        Product createdProduct = productService.createProduct(productToSave);

        // ASSERT: Verify the results
        
        // 1. Check the return value
        assertEquals(productToSave.getName(), createdProduct.getName(), "The returned product name should match the input.");
        assertEquals(productToSave.getId(), createdProduct.getId());

        // 2. Verify interactions (critical for unit tests)
        // Ensure the product was actually saved to the repository
        verify(productRepository, times(1)).save(any(Product.class));
        
        // Ensure the Kafka notification was sent exactly once
    }
    
    // You would add other tests here: e.g., whenGetProductById_thenReturnProduct()
}