package backend.user_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.mock.mockito.MockBean;
import backend.user_service.repository.UserRepository;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import backend.user_service.service.KafkaService;

@SpringBootTest
@ActiveProfiles("test")
@EnableAutoConfiguration(exclude = {KafkaAutoConfiguration.class})
class UserServiceApplicationTests {

	@MockBean
    private UserRepository userRepository;

    @MockBean
	private KafkaService kafkaService;

    @Test
    void contextLoads() {
        // Just ensures Spring context starts without real Mongo/Kafka
    }
}
