package backend.order_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.boot.test.mock.mockito.MockBean;
import backend.order_service.service.KafkaService;

@SpringBootTest
@EnableAutoConfiguration(exclude = {KafkaAutoConfiguration.class})
@TestPropertySource(properties = {
    // Also explicitly disable listeners to be safe
    "spring.kafka.listener.auto-startup=false",
	"spring.data.mongodb.uri=mongodb://localhost:27017/testdb",
})
class OrderServiceApplicationTests {

	@MockBean
	private KafkaService kafkaService;

	@Test
	void contextLoads() {
	}

}
