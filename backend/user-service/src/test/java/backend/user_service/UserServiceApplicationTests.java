package backend.user_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.mock.mockito.MockBean;
import backend.user_service.repository.UserRepository;
import backend.user_service.service.KafkaService;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
                properties = {
                  "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration",
                  "spring.kafka.listener.auto-startup=false",
                  "spring.data.mongodb.uri=mongodb://localhost:27017/testdb",
                  "jwt.secret=testsecretkey"
                })
@EnableAutoConfiguration(exclude = {KafkaAutoConfiguration.class})
class UserServiceApplicationTests {

	@MockBean
    private UserRepository userRepository;

    @Test
    void contextLoads() {
        // Just ensures Spring context starts without real Mongo/Kafka
    }
}
