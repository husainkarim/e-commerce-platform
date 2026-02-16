package backend.product_service.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import backend.product_service.model.Product;
import backend.product_service.repository.OrderedRepository;
import backend.product_service.repository.ProductRepository;
import backend.product_service.repository.SellerRepository;
import backend.product_service.service.KafkaService;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductRepository productRepository;

    @MockBean
    private SellerRepository sellerRepository;

    @MockBean
    private OrderedRepository orderedRepository;

    @MockBean
    private KafkaService kafkaService;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId("1");
        testProduct.setName("Test Product");
        testProduct.setDescription("A test product");
        testProduct.setCategory("Electronics");
        testProduct.setPrice(99.99);
        testProduct.setQuantity(10);
        testProduct.setUserId("seller123");
    }

    @Test
    void shouldListAllProducts() throws Exception {
        List<Product> products = new ArrayList<>();
        products.add(testProduct);
        when(productRepository.findAll()).thenReturn(products);

        mockMvc.perform(get("/api/products/list")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.products", hasSize(1)))
                .andExpect(jsonPath("$.message").value("Product list fetched successfully"));
    }

    @Test
    void shouldReturnEmptyProductList() throws Exception {
        when(productRepository.findAll()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/products/list")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.products", hasSize(0)));
    }

    @Test
    void shouldReturnMultipleProducts() throws Exception {
        List<Product> products = new ArrayList<>();
        Product prod1 = new Product();
        prod1.setId("1");
        prod1.setName("Product 1");
        products.add(prod1);
        
        Product prod2 = new Product();
        prod2.setId("2");
        prod2.setName("Product 2");
        products.add(prod2);
        
        when(productRepository.findAll()).thenReturn(products);

        mockMvc.perform(get("/api/products/list")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.products", hasSize(2)));
    }

    @Test
    void shouldHandleEmptySearchResults() throws Exception {
        when(productRepository.findByCategory("NonExistent")).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/products/list")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnProductNotFoundWhenDetailsNotExist() throws Exception {
        when(productRepository.findById("nonexistent")).thenReturn(java.util.Optional.empty());

        mockMvc.perform(get("/api/products/details")
                .param("productId", "nonexistent")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnProductDetailsWhenFound() throws Exception {
        when(productRepository.findById("1")).thenReturn(java.util.Optional.of(testProduct));
        when(orderedRepository.findByProductId("1")).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/products/details")
                .param("productId", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.product.id").value("1"));
    }

    @Test
    void shouldHandleMultipleProducts() throws Exception {
        List<Product> products = new ArrayList<>();
        Product prod1 = new Product();
        prod1.setId("1");
        prod1.setName("Product 1");
        products.add(prod1);
        
        Product prod2 = new Product();
        prod2.setId("2");
        prod2.setName("Product 2");
        products.add(prod2);
        
        when(productRepository.findAll()).thenReturn(products);

        mockMvc.perform(get("/api/products/list")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.products", hasSize(2)));
    }

    @Test
    void shouldReturnProductWithCorrectProperties() throws Exception {
        when(productRepository.findById("1")).thenReturn(java.util.Optional.of(testProduct));
        when(orderedRepository.findByProductId("1")).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/products/details")
                .param("productId", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.product.name").value("Test Product"));
    }

    @Test
    void shouldFilterProductsByUserId() throws Exception {
        List<Product> userProducts = new ArrayList<>();
        userProducts.add(testProduct);
        when(productRepository.findByUserId("seller123")).thenReturn(userProducts);

        mockMvc.perform(get("/api/products/list")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}

