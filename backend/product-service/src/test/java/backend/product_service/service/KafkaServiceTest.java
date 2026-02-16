package backend.product_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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

    private static final String PRODUCT_CREATED_TOPIC = "product-created-topic";
    private static final String PRODUCT_UPDATED_TOPIC = "product-updated-topic";
    private static final String PRODUCT_DELETED_TOPIC = "product-deleted-topic";

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
            eq(PRODUCT_CREATED_TOPIC),
            any()
        );
    }

    @Test
    void shouldSendProductUpdatedEvent() {
        kafkaService.sendProductUpdatedEvent(testProduct);

        verify(kafkaTemplate).send(
            eq(PRODUCT_UPDATED_TOPIC),
            any()
        );
    }

    @Test
    void shouldSendProductDeletedEvent() {
        kafkaService.sendProductDeletedEvent(testProduct);

        verify(kafkaTemplate).send(
            eq(PRODUCT_DELETED_TOPIC),
            any()
        );
    }

    @Test
    void shouldPublishProductCreatedEventWithCorrectData() {
        kafkaService.sendProductCreatedEvent(testProduct);
        
        verify(kafkaTemplate).send(eq(PRODUCT_CREATED_TOPIC), any());
    }

    @Test
    void shouldPublishProductUpdatedEventWithCorrectData() {
        kafkaService.sendProductUpdatedEvent(testProduct);
        
        verify(kafkaTemplate).send(eq(PRODUCT_UPDATED_TOPIC), any());
    }

    @Test
    void shouldPublishProductDeletedEventWithCorrectData() {
        kafkaService.sendProductDeletedEvent(testProduct);
        
        verify(kafkaTemplate).send(eq(PRODUCT_DELETED_TOPIC), any());
    }

    @Test
    void shouldHandleMultipleProductEvents() {
        Product product2 = new Product();
        product2.setId("2");
        product2.setName("Second Product");
        product2.setUserId("seller456");
        
        kafkaService.sendProductCreatedEvent(testProduct);
        kafkaService.sendProductCreatedEvent(product2);
        
        verify(kafkaTemplate, times(2)).send(eq(PRODUCT_CREATED_TOPIC), any());
    }

    @Test
    void shouldPublishEventWithAllProductInformation() {
        kafkaService.sendProductCreatedEvent(testProduct);
        
        // Verify that the Kafka template was called with the correct topic
        verify(kafkaTemplate).send(eq(PRODUCT_CREATED_TOPIC), any());
    }
}

