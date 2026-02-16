package backend.product_service.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

import backend.product_service.model.Ordered;
import backend.product_service.model.Product;
import backend.product_service.repository.OrderedRepository;
import backend.product_service.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
class OrderEventConsumerTest {

    @Mock
    private OrderedRepository orderedRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderEventConsumer orderEventConsumer;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId("prod-123");
        testProduct.setName("Test Product");
        testProduct.setQuantity(50);
        testProduct.setUserId("seller-1");
    }

    @Test
    void shouldHandleOrderCreatedEvent() {
        Map<String, Object> event = new HashMap<>();
        event.put("orderId", "order-1");
        event.put("clientId", "client-1");
        
        List<Map<String, Object>> items = new ArrayList<>();
        Map<String, Object> item = new HashMap<>();
        item.put("productId", "prod-123");
        item.put("quantity", 5);
        item.put("price", 99.99);
        item.put("sellerId", "seller-1");
        items.add(item);
        event.put("items", items);

        when(productRepository.findById("prod-123")).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        orderEventConsumer.handleOrderCreated(event);

        verify(orderedRepository).save(any(Ordered.class));
        verify(productRepository).findById("prod-123");
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void shouldReduceProductQuantityOnOrderCreated() {
        Map<String, Object> event = new HashMap<>();
        event.put("orderId", "order-1");
        event.put("clientId", "client-1");
        
        List<Map<String, Object>> items = new ArrayList<>();
        Map<String, Object> item = new HashMap<>();
        item.put("productId", "prod-123");
        item.put("quantity", 10);
        item.put("price", 49.99);
        item.put("sellerId", "seller-1");
        items.add(item);
        event.put("items", items);

        when(productRepository.findById("prod-123")).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        orderEventConsumer.handleOrderCreated(event);

        verify(productRepository).save(any(Product.class));
    }

    @Test
    void shouldHandleOrderCreatedWithMultipleItems() {
        Map<String, Object> event = new HashMap<>();
        event.put("orderId", "order-2");
        event.put("clientId", "client-2");
        
        List<Map<String, Object>> items = new ArrayList<>();
        Map<String, Object> item1 = new HashMap<>();
        item1.put("productId", "prod-1");
        item1.put("quantity", 2);
        item1.put("price", 100.0);
        item1.put("sellerId", "seller-1");
        
        Map<String, Object> item2 = new HashMap<>();
        item2.put("productId", "prod-2");
        item2.put("quantity", 3);
        item2.put("price", 50.0);
        item2.put("sellerId", "seller-2");
        
        items.add(item1);
        items.add(item2);
        event.put("items", items);

        when(productRepository.findById("prod-1")).thenReturn(Optional.of(testProduct));
        when(productRepository.findById("prod-2")).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        orderEventConsumer.handleOrderCreated(event);

        verify(orderedRepository, times(2)).save(any(Ordered.class));
    }

    @Test
    void shouldHandleProductNotFoundInOrderCreated() {
        Map<String, Object> event = new HashMap<>();
        event.put("orderId", "order-1");
        event.put("clientId", "client-1");
        
        List<Map<String, Object>> items = new ArrayList<>();
        Map<String, Object> item = new HashMap<>();
        item.put("productId", "nonexistent");
        item.put("quantity", 5);
        item.put("price", 99.99);
        item.put("sellerId", "seller-1");
        items.add(item);
        event.put("items", items);

        when(productRepository.findById("nonexistent")).thenReturn(Optional.empty());

        orderEventConsumer.handleOrderCreated(event);

        verify(orderedRepository).save(any(Ordered.class));
        verify(productRepository, times(0)).save(any(Product.class));
    }

    @Test
    void shouldHandleOrderDeletedEvent() {
        List<Ordered> orderedList = new ArrayList<>();
        Ordered ordered = new Ordered();
        ordered.setOrderId("order-1");
        ordered.setProductId("prod-123");
        ordered.setQuantity(5);
        orderedList.add(ordered);

        Map<String, Object> event = new HashMap<>();
        event.put("orderId", "order-1");

        when(orderedRepository.findByOrderId("order-1")).thenReturn(orderedList);
        when(productRepository.findById("prod-123")).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        orderEventConsumer.handleOrderDeleted(event);

        verify(orderedRepository).findByOrderId("order-1");
        verify(productRepository).save(any(Product.class));
        verify(orderedRepository).deleteAll(orderedList);
    }

    @Test
    void shouldRestoreProductQuantityOnOrderDeleted() {
        List<Ordered> orderedList = new ArrayList<>();
        Ordered ordered = new Ordered();
        ordered.setOrderId("order-1");
        ordered.setProductId("prod-123");
        ordered.setQuantity(15);
        orderedList.add(ordered);

        Map<String, Object> event = new HashMap<>();
        event.put("orderId", "order-1");

        when(orderedRepository.findByOrderId("order-1")).thenReturn(orderedList);
        when(productRepository.findById("prod-123")).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        orderEventConsumer.handleOrderDeleted(event);

        verify(productRepository).findById("prod-123");
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void shouldHandleOrderDeletedWithMultipleItems() {
        List<Ordered> orderedList = new ArrayList<>();
        Ordered ordered1 = new Ordered();
        ordered1.setOrderId("order-1");
        ordered1.setProductId("prod-1");
        ordered1.setQuantity(2);
        
        Ordered ordered2 = new Ordered();
        ordered2.setOrderId("order-1");
        ordered2.setProductId("prod-2");
        ordered2.setQuantity(3);
        
        orderedList.add(ordered1);
        orderedList.add(ordered2);

        Map<String, Object> event = new HashMap<>();
        event.put("orderId", "order-1");

        when(orderedRepository.findByOrderId("order-1")).thenReturn(orderedList);
        when(productRepository.findById("prod-1")).thenReturn(Optional.of(testProduct));
        when(productRepository.findById("prod-2")).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        orderEventConsumer.handleOrderDeleted(event);

        verify(productRepository, times(2)).save(any(Product.class));
        verify(orderedRepository).deleteAll(orderedList);
    }

    @Test
    void shouldHandleOrderDeletedWithNoItems() {
        List<Ordered> orderedList = new ArrayList<>();

        Map<String, Object> event = new HashMap<>();
        event.put("orderId", "order-1");

        when(orderedRepository.findByOrderId("order-1")).thenReturn(orderedList);

        orderEventConsumer.handleOrderDeleted(event);

        verify(orderedRepository).findByOrderId("order-1");
        verify(orderedRepository).deleteAll(orderedList);
    }

    @Test
    void shouldHandleOrderDeletedWithProductNotFound() {
        List<Ordered> orderedList = new ArrayList<>();
        Ordered ordered = new Ordered();
        ordered.setOrderId("order-1");
        ordered.setProductId("nonexistent");
        ordered.setQuantity(5);
        orderedList.add(ordered);

        Map<String, Object> event = new HashMap<>();
        event.put("orderId", "order-1");

        when(orderedRepository.findByOrderId("order-1")).thenReturn(orderedList);
        when(productRepository.findById("nonexistent")).thenReturn(Optional.empty());

        orderEventConsumer.handleOrderDeleted(event);

        verify(orderedRepository).deleteAll(orderedList);
        verify(productRepository, times(0)).save(any(Product.class));
    }
}
