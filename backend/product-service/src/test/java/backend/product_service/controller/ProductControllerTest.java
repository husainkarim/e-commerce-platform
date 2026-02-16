package backend.product_service.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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

import com.fasterxml.jackson.databind.ObjectMapper;

import backend.product_service.model.Product;
import backend.product_service.model.Seller;
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
    
    private ObjectMapper objectMapper;

    private Product testProduct;
    private Seller testSeller;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        testProduct = new Product();
        testProduct.setId("1");
        testProduct.setName("Test Product");
        testProduct.setDescription("A test product");
        testProduct.setCategory("Electronics");
        testProduct.setPrice(99.99);
        testProduct.setQuantity(10);
        testProduct.setUserId("seller123");

        testSeller = new Seller();
        testSeller.setUserId("seller123");
        testSeller.setEmail("seller@example.com");
        testSeller.setRole("seller");
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
    void shouldGetProductDetails() throws Exception {
        when(productRepository.findById("1")).thenReturn(java.util.Optional.of(testProduct));
        when(orderedRepository.findByProductId("1")).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/products/details")
                .param("productId", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.product.id").value("1"));
    }

    @Test
    void shouldReturnNotFoundForInvalidProduct() throws Exception {
        when(productRepository.findById("nonexistent")).thenReturn(java.util.Optional.empty());

        mockMvc.perform(get("/api/products/details")
                .param("productId", "nonexistent")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnForbiddenForUserProductsWithoutSellerStatus() throws Exception {
        List<Seller> sellers = new ArrayList<>();
        when(sellerRepository.findAll()).thenReturn(sellers);

        mockMvc.perform(get("/api/products/user-products")
                .param("userId", "seller123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnUserProductsForValidSeller() throws Exception {
        List<Seller> sellers = new ArrayList<>();
        sellers.add(testSeller);
        
        List<Product> userProducts = new ArrayList<>();
        userProducts.add(testProduct);

        when(sellerRepository.findAll()).thenReturn(sellers);
        when(productRepository.findByUserId("seller123")).thenReturn(userProducts);
        when(orderedRepository.findByProductId("1")).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/products/user-products")
                .param("userId", "seller123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnBadRequestForEmptyUserId() throws Exception {
        List<Seller> sellers = new ArrayList<>();
        when(sellerRepository.findAll()).thenReturn(sellers);

        mockMvc.perform(get("/api/products/user-products")
                .param("userId", "")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
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

    @Test
    void shouldVerifyProductPrice() throws Exception {
        when(productRepository.findById("1")).thenReturn(java.util.Optional.of(testProduct));
        when(orderedRepository.findByProductId("1")).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/products/details")
                .param("productId", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.product.price").value(99.99));
    }

    @Test
    void shouldVerifyProductQuantity() throws Exception {
        when(productRepository.findById("1")).thenReturn(java.util.Optional.of(testProduct));
        when(orderedRepository.findByProductId("1")).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/products/details")
                .param("productId", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.product.quantity").value(10));
    }

    @Test
    void shouldHandleMultipleSellerProducts() throws Exception {
        List<Seller> sellers = new ArrayList<>();
        sellers.add(testSeller);
        
        List<Product> userProducts = new ArrayList<>();
        Product prod1 = new Product();
        prod1.setId("1");
        prod1.setName("Product 1");
        prod1.setUserId("seller123");
        
        Product prod2 = new Product();
        prod2.setId("2");
        prod2.setName("Product 2");
        prod2.setUserId("seller123");
        
        userProducts.add(prod1);
        userProducts.add(prod2);

        when(sellerRepository.findAll()).thenReturn(sellers);
        when(productRepository.findByUserId("seller123")).thenReturn(userProducts);
        when(orderedRepository.findByProductId("1")).thenReturn(new ArrayList<>());
        when(orderedRepository.findByProductId("2")).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/api/products/user-products")
                .param("userId", "seller123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void shouldCreateProductSuccessfully() throws Exception {
        Product newProduct = new Product();
        newProduct.setName("New Product");
        newProduct.setDescription("A new product");
        newProduct.setCategory("Electronics");
        newProduct.setPrice(49.99);
        newProduct.setQuantity(20);
        newProduct.setUserId("seller123");

        Product savedProduct = new Product();
        savedProduct.setId("2");
        savedProduct.setName("New Product");
        savedProduct.setDescription("A new product");
        savedProduct.setCategory("Electronics");
        savedProduct.setPrice(49.99);
        savedProduct.setQuantity(20);
        savedProduct.setUserId("seller123");

        List<Seller> sellers = new ArrayList<>();
        sellers.add(testSeller);
        
        when(sellerRepository.findAll()).thenReturn(sellers);
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        mockMvc.perform(post("/api/products/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newProduct)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.product.id").value("2"))
                .andExpect(jsonPath("$.message").value("Product created successfully"));
    }

    @Test
    void shouldReturnForbiddenWhenCreatingProductWithoutSellerStatus() throws Exception {
        Product newProduct = new Product();
        newProduct.setName("New Product");
        newProduct.setCategory("Electronics");
        newProduct.setPrice(49.99);
        newProduct.setQuantity(20);
        newProduct.setUserId("notaseller");

        List<Seller> sellers = new ArrayList<>();
        when(sellerRepository.findAll()).thenReturn(sellers);

        mockMvc.perform(post("/api/products/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newProduct)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("User is not a seller or does not exist"));
    }

    @Test
    void shouldReturnBadRequestForInvalidCategoryOnCreate() throws Exception {
        Product newProduct = new Product();
        newProduct.setName("New Product");
        newProduct.setCategory("InvalidCategory");
        newProduct.setPrice(49.99);
        newProduct.setQuantity(20);
        newProduct.setUserId("seller123");

        List<Seller> sellers = new ArrayList<>();
        sellers.add(testSeller);
        when(sellerRepository.findAll()).thenReturn(sellers);

        mockMvc.perform(post("/api/products/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newProduct)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid category"));
    }

    @Test
    void shouldUpdateProductSuccessfully() throws Exception {
        Product updateProduct = new Product();
        updateProduct.setId("1");
        updateProduct.setName("Updated Product");
        updateProduct.setDescription("An updated product");
        updateProduct.setCategory("Electronics");
        updateProduct.setPrice(149.99);
        updateProduct.setQuantity(15);
        updateProduct.setUserId("seller123");

        List<Seller> sellers = new ArrayList<>();
        sellers.add(testSeller);
        
        when(sellerRepository.findAll()).thenReturn(sellers);
        when(productRepository.findById("1")).thenReturn(java.util.Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(updateProduct);

        mockMvc.perform(put("/api/products/update")
                .param("productId", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Product updated successfully"));
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingNonexistentProduct() throws Exception {
        Product updateProduct = new Product();
        updateProduct.setId("999");
        updateProduct.setName("Updated Product");
        updateProduct.setCategory("Electronics");
        updateProduct.setPrice(149.99);
        updateProduct.setQuantity(15);
        updateProduct.setUserId("seller123");

        List<Seller> sellers = new ArrayList<>();
        sellers.add(testSeller);
        
        when(sellerRepository.findAll()).thenReturn(sellers);
        when(productRepository.findById("999")).thenReturn(java.util.Optional.empty());

        mockMvc.perform(put("/api/products/update")
                .param("productId", "999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateProduct)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Product not found"));
    }

    @Test
    void shouldReturnForbiddenWhenUpdatingProductWithoutSellerStatus() throws Exception {
        Product updateProduct = new Product();
        updateProduct.setId("1");
        updateProduct.setName("Updated Product");
        updateProduct.setCategory("Electronics");
        updateProduct.setPrice(149.99);
        updateProduct.setQuantity(15);
        updateProduct.setUserId("notaseller");

        List<Seller> sellers = new ArrayList<>();
        when(sellerRepository.findAll()).thenReturn(sellers);

        mockMvc.perform(put("/api/products/update")
                .param("productId", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateProduct)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnBadRequestForInvalidCategoryOnUpdate() throws Exception {
        Product updateProduct = new Product();
        updateProduct.setId("1");
        updateProduct.setName("Updated Product");
        updateProduct.setCategory("InvalidCategory");
        updateProduct.setPrice(149.99);
        updateProduct.setQuantity(15);
        updateProduct.setUserId("seller123");

        List<Seller> sellers = new ArrayList<>();
        sellers.add(testSeller);
        
        when(sellerRepository.findAll()).thenReturn(sellers);

        mockMvc.perform(put("/api/products/update")
                .param("productId", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateProduct)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid category"));
    }

    @Test
    void shouldDeleteProductSuccessfully() throws Exception {
        List<Seller> sellers = new ArrayList<>();
        sellers.add(testSeller);
        
        when(sellerRepository.findAll()).thenReturn(sellers);
        when(productRepository.findById("1")).thenReturn(java.util.Optional.of(testProduct));
        when(productRepository.existsById("1")).thenReturn(true);

        mockMvc.perform(delete("/api/products/delete")
                .param("productId", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Product deleted successfully"));
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonexistentProduct() throws Exception {
        List<Seller> sellers = new ArrayList<>();
        sellers.add(testSeller);
        
        when(sellerRepository.findAll()).thenReturn(sellers);
        when(productRepository.findById("999")).thenReturn(java.util.Optional.of(testProduct));
        when(productRepository.existsById("999")).thenReturn(false);

        mockMvc.perform(delete("/api/products/delete")
                .param("productId", "999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Product not found"));
    }

    @Test
    void shouldReturnForbiddenWhenDeletingProductWithoutSellerStatus() throws Exception {
        List<Seller> sellers = new ArrayList<>();
        when(sellerRepository.findAll()).thenReturn(sellers);
        when(productRepository.findById("1")).thenReturn(java.util.Optional.of(testProduct));

        mockMvc.perform(delete("/api/products/delete")
                .param("productId", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnUnauthorizedForProductIdMismatchOnUpdate() throws Exception {
        Product updateProduct = new Product();
        updateProduct.setId("2");  // Different ID
        updateProduct.setName("Updated Product");
        updateProduct.setCategory("Electronics");
        updateProduct.setPrice(149.99);
        updateProduct.setQuantity(15);
        updateProduct.setUserId("seller123");

        List<Seller> sellers = new ArrayList<>();
        sellers.add(testSeller);
        
        when(sellerRepository.findAll()).thenReturn(sellers);
        when(productRepository.findById("1")).thenReturn(java.util.Optional.of(testProduct));

        mockMvc.perform(put("/api/products/update")
                .param("productId", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateProduct)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Product ID in request body does not match the ID in query parameter"));
    }
}

