package backend.product_service.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import backend.product_service.model.Product;
import backend.product_service.model.Seller;
import backend.product_service.repository.ProductRepository;
import backend.product_service.repository.SellerRepository;

@ExtendWith(MockitoExtension.class)
class UserEventConsumerTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private KafkaService kafkaService;

    @Mock
    private SellerRepository sellerRepository;

    @InjectMocks
    private UserEventConsumer userEventConsumer;

    private Seller testSeller;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        testSeller = new Seller();
        testSeller.setUserId("seller-1");
        testSeller.setEmail("seller@example.com");
        testSeller.setRole("seller");

        testProduct = new Product();
        testProduct.setId("prod-1");
        testProduct.setName("Test Product");
        testProduct.setUserId("seller-1");
    }

    @Test
    void shouldHandleSellerCreatedEvent() {
        Map<String, Object> event = new HashMap<>();
        event.put("userId", "seller-1");
        event.put("email", "seller@example.com");
        event.put("role", "seller");

        when(sellerRepository.existsById("seller-1")).thenReturn(false);

        userEventConsumer.handleSellerCreated(event);

        verify(sellerRepository).save(any(Seller.class));
    }

    @Test
    void shouldIgnoreNonSellerUsers() {
        Map<String, Object> event = new HashMap<>();
        event.put("userId", "user-1");
        event.put("email", "user@example.com");
        event.put("role", "customer");

        userEventConsumer.handleSellerCreated(event);

        verify(sellerRepository, times(0)).save(any(Seller.class));
    }

    @Test
    void shouldIgnoreExistingSeller() {
        Map<String, Object> event = new HashMap<>();
        event.put("userId", "seller-1");
        event.put("email", "seller@example.com");
        event.put("role", "seller");

        when(sellerRepository.existsById("seller-1")).thenReturn(true);

        userEventConsumer.handleSellerCreated(event);

        verify(sellerRepository, times(0)).save(any(Seller.class));
    }

    @Test
    void shouldIgnoreEventWithoutRole() {
        Map<String, Object> event = new HashMap<>();
        event.put("userId", "seller-1");
        event.put("email", "seller@example.com");

        userEventConsumer.handleSellerCreated(event);

        verify(sellerRepository, times(0)).save(any(Seller.class));
    }

    @Test
    void shouldHandleUserRoleUpdatedToSeller() {
        Map<String, Object> event = new HashMap<>();
        event.put("userId", "seller-1");
        event.put("email", "seller@example.com");
        event.put("newRole", "seller");

        when(sellerRepository.findByUserId("seller-1")).thenReturn(testSeller);

        userEventConsumer.handleUserRoleUpdated(event);

        verify(sellerRepository).save(any(Seller.class));
    }

    @Test
    void shouldHandleUserRoleUpdatedFromSeller() {
        Map<String, Object> event = new HashMap<>();
        event.put("userId", "seller-1");
        event.put("newRole", "customer");

        when(sellerRepository.findByUserId("seller-1")).thenReturn(testSeller);
        when(productRepository.findByUserId("seller-1")).thenReturn(new ArrayList<>());

        userEventConsumer.handleUserRoleUpdated(event);

        verify(sellerRepository).deleteById("seller-1");
    }

    @Test
    void shouldDeleteAllProductsWhenSellerRoleRevoked() {
        Map<String, Object> event = new HashMap<>();
        event.put("userId", "seller-1");
        event.put("newRole", "customer");

        List<Product> products = new ArrayList<>();
        products.add(testProduct);

        when(sellerRepository.findByUserId("seller-1")).thenReturn(testSeller);
        when(productRepository.findByUserId("seller-1")).thenReturn(products);

        userEventConsumer.handleUserRoleUpdated(event);

        verify(kafkaService).sendProductDeletedEvent(testProduct);
        verify(productRepository).deleteAll(products);
        verify(sellerRepository).deleteById("seller-1");
    }

    @Test
    void shouldIgnoreNonExistentSellerInRoleUpdate() {
        Map<String, Object> event = new HashMap<>();
        event.put("userId", "nonexistent");
        event.put("newRole", "customer");

        when(sellerRepository.findByUserId("nonexistent")).thenReturn(null);

        userEventConsumer.handleUserRoleUpdated(event);

        verify(sellerRepository, times(0)).deleteById("nonexistent");
    }

    @Test
    void shouldHandleUserDeletedEvent() {
        Map<String, Object> event = new HashMap<>();
        event.put("userId", "seller-1");

        List<Product> products = new ArrayList<>();
        products.add(testProduct);

        when(sellerRepository.findByUserId("seller-1")).thenReturn(testSeller);
        when(productRepository.findByUserId("seller-1")).thenReturn(products);

        userEventConsumer.handleUserDeleted(event);

        verify(kafkaService).sendProductDeletedEvent(testProduct);
        verify(productRepository).deleteAll(products);
        verify(sellerRepository).deleteById("seller-1");
    }

    @Test
    void shouldDeleteMultipleProductsWhenUserDeleted() {
        Map<String, Object> event = new HashMap<>();
        event.put("userId", "seller-1");

        List<Product> products = new ArrayList<>();
        Product prod1 = new Product();
        prod1.setId("prod-1");
        prod1.setUserId("seller-1");
        
        Product prod2 = new Product();
        prod2.setId("prod-2");
        prod2.setUserId("seller-1");
        
        products.add(prod1);
        products.add(prod2);

        when(sellerRepository.findByUserId("seller-1")).thenReturn(testSeller);
        when(productRepository.findByUserId("seller-1")).thenReturn(products);

        userEventConsumer.handleUserDeleted(event);

        verify(kafkaService, times(2)).sendProductDeletedEvent(any(Product.class));
        verify(productRepository).deleteAll(products);
    }

    @Test
    void shouldIgnoreNonExistentSellerDeletion() {
        Map<String, Object> event = new HashMap<>();
        event.put("userId", "nonexistent");

        when(sellerRepository.findByUserId("nonexistent")).thenReturn(null);

        userEventConsumer.handleUserDeleted(event);

        verify(sellerRepository, times(0)).deleteById("nonexistent");
    }

    @Test
    void shouldGetUserNameById() {
        List<Seller> sellers = new ArrayList<>();
        sellers.add(testSeller);

        when(sellerRepository.findAll()).thenReturn(sellers);

        String userName = userEventConsumer.getUserNameById("seller-1");

        assertEquals("seller@example.com", userName);
    }

    @Test
    void shouldReturnUnknownSellerWhenNotFound() {
        when(sellerRepository.findAll()).thenReturn(new ArrayList<>());

        String userName = userEventConsumer.getUserNameById("nonexistent");

        assertEquals("Unknown Seller", userName);
    }

    @Test
    void shouldFindCorrectSellerAmongMultipleSellers() {
        List<Seller> sellers = new ArrayList<>();
        
        Seller seller1 = new Seller();
        seller1.setUserId("seller-1");
        seller1.setEmail("seller1@example.com");
        
        Seller seller2 = new Seller();
        seller2.setUserId("seller-2");
        seller2.setEmail("seller2@example.com");
        
        sellers.add(seller1);
        sellers.add(seller2);

        when(sellerRepository.findAll()).thenReturn(sellers);

        String userName = userEventConsumer.getUserNameById("seller-2");

        assertEquals("seller2@example.com", userName);
    }
}
