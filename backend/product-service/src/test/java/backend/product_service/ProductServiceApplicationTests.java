package backend.product_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.boot.test.mock.mockito.MockBean;
import backend.product_service.service.KafkaService;

@SpringBootTest
@EnableAutoConfiguration(exclude = {KafkaAutoConfiguration.class})
@TestPropertySource(properties = {
    // Also explicitly disable listeners to be safe
    "spring.kafka.listener.auto-startup=false" 
})
class ProductServiceApplicationTests {

	@MockBean
	private KafkaService kafkaService;

	@Test
	void contextLoads() {
	}

}
