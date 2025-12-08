package backend.product_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@EnableAutoConfiguration(exclude = {KafkaAutoConfiguration.class})
@TestPropertySource(properties = {
    // Also explicitly disable listeners to be safe
    "spring.kafka.listener.auto-startup=false" 
})
class ProductServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
