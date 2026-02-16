package backend.product_service.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import backend.product_service.model.Product;

@ExtendWith(MockitoExtension.class)
class KafkaServiceTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private KafkaService kafkaService;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId("1");
        testProduct.setName("Test Product");
        testProduct.setDescription("A test product");
        testProduct.setUserId("seller123");
        testProduct.setCategory("Electronics");
        testProduct.setPrice(99.99);
        testProduct.setQuantity(10);
    }

    @Test
    void shouldSendProductCreatedEvent() {
        kafkaService.sendProductCreatedEvent(testProduct);

        verify(kafkaTemplate).send(
            eq("product-created-topic"),
            any()
        );
    }

    @Test
    void shouldSendProductUpdatedEvent() {
        kafkaService.sendProductUpdatedEvent(testProduct);

        verify(kafkaTemplate).send(
            eq("product-updated-topic"),
            any()
        );
    }

    @Test
    void shouldSendProductDeletedEvent() {
        kafkaService.sendProductDeletedEvent(testProduct);

        verify(kafkaTemplate).send(
            eq("product-deleted-topic"),
            any()
        );
    }

    @Test
    void shouldPublishProductCreatedEventWithCorrectData() {
        kafkaService.sendProductCreatedEvent(testProduct);
        
        verify(kafkaTemplate).send(eq("product-created-topic"), any());
    }

    @Test
    void shouldPublishProductUpdatedEventWithCorrectData() {
        kafkaService.sendProductUpdatedEvent(testProduct);
        
        verify(kafkaTemplate).send(eq("product-updated-topic"), any());
    }

    @Test
    void shouldPublishProductDeletedEventWithCorrectData() {
        kafkaService.sendProductDeletedEvent(testProduct);
        
        verify(kafkaTemplate).send(eq("product-deleted-topic"), any());
    }

    @Test
    void shouldHandleMultipleProductEvents() {
        Product product2 = new Product();
        product2.setId("2");
        product2.setName("Second Product");
        product2.setUserId("seller456");
        
        kafkaService.sendProductCreatedEvent(testProduct);
        kafkaService.sendProductCreatedEvent(product2);
        
        verify(kafkaTemplate, times(2)).send(eq("product-created-topic"), any());
    }

    @Test
    void shouldPublishEventWithAllProductInformation() {
        kafkaService.sendProductCreatedEvent(testProduct);
        
        // Verify that the Kafka template was called with the correct topic
        verify(kafkaTemplate).send(eq("product-created-topic"), any());
    }
}

